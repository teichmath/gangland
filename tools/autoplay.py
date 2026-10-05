#!/usr/bin/env python3
"""
Gangland autoplay harness.
Plays automatically to explore code paths and find bugs.
Logs all output + inputs to autoplay_<timestamp>.log.
"""

import subprocess, threading, time, re, random, sys, os, json
from datetime import datetime
from pathlib import Path

ROOT_DIR    = Path(__file__).parent.parent
CLASS_DIR   = os.environ.get("GANGLAND_CLASS_DIR", str(ROOT_DIR / "out"))   # compiled game to run
LOG_DIR     = Path(os.environ.get("GANGLAND_LOG_DIR", ROOT_DIR / "logs"))
LOG_DIR.mkdir(parents=True, exist_ok=True)

# Lines the game prints for harnesses under GANGLAND_AUTOMATION=1 (not ASCII art like "<<--").
MARKER      = re.compile(r"^<<[A-Z_]+[ >]")

STUCK_SECS  = 10.0  # no prompt for this long = the game is hung
MAX_INPUTS  = 400

# Usage: autoplay.py [seed]   -- same seed + same choices replays the same game.
SEED        = sys.argv[1] if len(sys.argv) > 1 else str(random.randrange(2**63))
random.seed(SEED)           # makes the harness's own choices reproducible too

log_path = str(LOG_DIR / f"autoplay_{datetime.now():%Y%m%d_%H%M%S}_seed{SEED}_{os.getpid()}.log")


# ---------------------------------------------------------------------------
# Game subprocess wrapper
# ---------------------------------------------------------------------------

class Game:
    def __init__(self):
        self.proc = subprocess.Popen(
            ["java", "-cp", CLASS_DIR, "Gangland"],
            stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
            cwd=CLASS_DIR, text=True, bufsize=1,
            env=dict(os.environ, GANGLAND_SEED=SEED, GANGLAND_AUTOMATION="1"),
        )
        self._lines = []        # rolling buffer, last 200 lines
        self._last_t = time.time()
        self._lock = threading.Lock()
        self._prompt = threading.Event()   # set when the game prints <<PROMPT>>
        self.state = None                  # latest <<STATE {json}>> from the game
        self.violations = []               # <<INVARIANT ...>> lines seen so far
        self._log = open(log_path, "w", encoding="utf-8")
        threading.Thread(target=self._reader, daemon=True).start()

    def _reader(self):
        try:
            for line in self.proc.stdout:
                if MARKER.match(line):
                    self._log.write(f"OUT {line}")
                    self._log.flush()
                    if line.startswith("<<PROMPT"):
                        self._prompt.set()
                    elif line.startswith("<<STATE "):
                        self.state = json.loads(line[len("<<STATE "):-3])
                    elif line.startswith(("<<INVARIANT", "<<AUTOMATION_ERROR")):
                        self.violations.append(line.strip())
                        sys.stdout.write(f"\033[31m{line}\033[0m")
                    continue
                if "Exception in thread" in line:
                    self.violations.append("CRASH " + line.strip())
                with self._lock:
                    self._lines.append(line.rstrip("\n"))
                    if len(self._lines) > 200:
                        self._lines.pop(0)
                    self._last_t = time.time()
                self._log.write(f"OUT {line}")
                self._log.flush()
                sys.stdout.write(line)
                sys.stdout.flush()
        except Exception:
            pass

    def send(self, text):
        self._log.write(f"IN  {text!r}\n")
        self._log.flush()
        print(f"\033[33m>>> {text!r}\033[0m", flush=True)
        self._prompt.clear()
        self.proc.stdin.write(text + "\n")
        self.proc.stdin.flush()

    def wait_prompt(self, timeout=STUCK_SECS):
        """Block until the game is waiting for input. False if it never asked."""
        return self._prompt.wait(timeout)

    def recent(self, n=30):
        with self._lock:
            return list(self._lines[-n:])

    def wait_for(self, text, timeout=90):
        """Block until `text` appears in recent output, or timeout expires."""
        deadline = time.time() + timeout
        while time.time() < deadline:
            with self._lock:
                if any(text in line for line in self._lines[-30:]):
                    return True
            time.sleep(0.2)
        return False

    def clear_buffer(self):
        with self._lock:
            self._lines.clear()
            self._last_t = time.time()

    def close(self):
        self.proc.kill()
        self._log.close()


# ---------------------------------------------------------------------------
# Menu parsing
# ---------------------------------------------------------------------------

def normalize_token(tok):
    """(D)rop it  ->  D    (F)ight  ->  F    anything else unchanged."""
    m = re.match(r'^\(([A-Za-z])\)', tok)
    return m.group(1).upper() if m else tok

def _parse_bracket_line(line):
    """Parse one [ ... ] line into a set of normalized tokens."""
    s = line.strip()
    m = re.match(r'\[(.*)\]', s)
    if not m:
        return set()
    content = m.group(1)
    if "/" in content:
        parts = [p.strip() for p in content.split("/") if p.strip()]
        return {normalize_token(p) for p in parts}
    else:
        tokens = [t.strip() for t in re.split(r" {2,}", content) if t.strip()]
        return {normalize_token(t) for t in tokens}

def parse_menu(lines):
    """Return frozenset of tokens from the MOST RECENT menu block in lines.

    Walks backwards through the buffer, skipping blank lines, and collects
    only the bracket lines that form the last printed menu.  Stops as soon as
    it hits a non-blank, non-bracket line so old menus are never mixed in.
    """
    opts = set()
    for line in reversed(lines):
        s = line.strip()
        if not s:
            continue          # skip blank lines between menu and surrounding text
        if not s.startswith("["):
            break             # hit non-bracket content — stop
        opts.update(_parse_bracket_line(line))
    return frozenset(opts)


def find_names(lines):
    """Heuristic: extract NPC names visible in recent output."""
    names = []
    joined = " ".join(lines)
    # "Name:" (NPC speaking), "greets Name", "sees Name", "meet Name"
    for m in re.finditer(r'\b([A-Z][a-z]{2,})\s*:', joined):
        names.append(m.group(1))
    for m in re.finditer(r'(?:greets?|meets?|sees?|with|attack(?:s|ed)?)\s+([A-Z][a-z]{2,})', joined):
        names.append(m.group(1))
    # Filter out common words that happen to be capitalised
    noise = {"You", "The", "But", "And", "Hit", "Enter", "More", "Yes", "No",
             "Greet", "Attack", "Quit", "Help", "Stats", "Who"}
    return sorted({n for n in names if n not in noise})


# ---------------------------------------------------------------------------
# Decision logic
# ---------------------------------------------------------------------------

DIRS      = frozenset({"N", "S", "E", "W"})
SKIP      = frozenset({"Quit", "Help"})   # Help has a number sub-menu we handle separately
MORE_PICKS = frozenset({"Stats", "Who", "Remember"})

PET_NAMES = ["Buddy", "Rex", "Spot", "Lucky", "Max", "Mittens", "Whiskers", "Shadow"]

# Free-text prompts that don't use bracket menus.
# Checked before menu detection.
def choose_freetext(lines):
    """Return a response string if recent output contains a free-text prompt,
    or None if no free-text prompt is detected.

    Uses a short 5-line window for one-liner prompts so that a prompt answered
    two turns ago can't re-trigger when new output arrives.  The 15-line window
    is kept only for prompts that may be preceded by several lines of narration.
    """
    tail   = "\n".join(lines[-5:])   # short window — only the current prompt
    joined = "\n".join(lines[-15:])  # longer window for context-heavy prompts

    if "What will you name" in tail or "That name's taken" in tail:
        return random.choice(PET_NAMES)
    if "enter a number" in tail.lower() or 'type "Exit"' in tail:
        return "Exit"
    # "Whom are you greeting/attacking?" — always "stranger"; names in the buffer
    # often belong to absent NPCs mentioned in passing, causing "What?".
    if "Whom are you" in tail:
        return "stranger"
    if "Loot whom?" in tail or "Bury whom?" in tail:
        return "a"
    if "(letter)" in tail and "want to" not in tail:
        return "a"
    if "want to drink" in tail or "want to offer" in tail:
        return "a"
    if "Whom do you want to think about" in tail:
        names = find_names(lines)
        return random.choice(names) if names else "stranger"
    if "What do you want to write?" in tail:
        return "hello"
    # "What?" on the last line means our previous response wasn't recognised.
    if lines and lines[-1].strip() == "What?":
        return "stranger"
    return None

def choose(opts, lines):
    joined = "\n".join(lines)

    # --- hit-Enter-only screens -----------------------------------------
    henter = {o for o in opts if "hit enter" in o.lower() or "hit Enter" in o}
    if henter and all(o in henter or o in SKIP for o in opts):
        return ""

    # --- Y / N prompt ----------------------------------------------------
    if opts <= {"Y", "N"}:
        return random.choice(["Y", "N"])

    # --- direction-only compass (spyglass) --------------------------------
    compass = frozenset({"N", "NW", "W", "SW", "S", "SE", "E", "NE"})
    if opts <= compass and opts:
        return random.choice(sorted(opts))

    # --- More sub-menu ---------------------------------------------------
    if opts & MORE_PICKS and "N" not in opts:
        safe = sorted(opts & MORE_PICKS)
        return random.choice(safe) if safe else ""

    # Remove things we never want to type
    usable = opts - SKIP - {o for o in opts if "hit enter" in o.lower()}

    if not usable:
        return ""

    # Weights: movement 45%, greet 15%, attack 10%, More 10%, other 20%
    dirs   = sorted(usable & DIRS)
    greet  = "Greet (name)" in usable
    attack = "Attack (name)" in usable
    more   = "More" in usable
    rest   = sorted(usable - DIRS - {"Greet (name)", "Attack (name)", "More"})

    roll = random.random()

    if dirs and roll < 0.45:
        return random.choice(dirs)

    if greet and roll < 0.60:
        names = find_names(lines)
        if names:
            return "Greet " + random.choice(names)
        return "greet"

    if attack and roll < 0.70:
        names = find_names(lines)
        if names:
            return "Attack " + random.choice(names)
        return "attack"

    if more and roll < 0.80:
        return "More"

    if rest:
        return random.choice(rest)
    if dirs:
        return random.choice(dirs)
    return ""


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

def main():
    print(f"Logging to {log_path}  (seed {SEED})\n")
    game = Game()

    # ---- startup sequence -----------------------------------------------
    game.wait_for("Who are you")
    game.send("Rufus")
    game.wait_for("Male or Female")
    game.send("male")
    # Loading screens (hit Enter, Y/N questions) are just prompts; the main loop handles them.

    # ---- main loop ------------------------------------------------------
    inputs_sent = 0

    while inputs_sent < MAX_INPUTS:
        if not game.wait_prompt():
            # Every read goes through stringInput(), which prints <<PROMPT>>, so a live
            # game that hasn't asked for anything in STUCK_SECS is looping.
            if game.proc.poll() is None:
                hang = f"HANG no prompt for {STUCK_SECS:.0f}s after input {inputs_sent}"
                game.violations.append(hang)
                game._log.write(hang + "\n")
                print(f"\033[31m>>> [HANG — no prompt for {STUCK_SECS:.0f}s]\033[0m", flush=True)
            else:
                print("\033[31m>>> [game exited]\033[0m", flush=True)
            break

        lines = game.recent()
        # Priority 1: free-text prompts (no bracket menu)
        action = choose_freetext(lines)
        # Priority 2: bracket menu detected; otherwise just hit Enter
        if action is None:
            menu = parse_menu(lines)
            action = choose(menu, lines) if menu else ""
        game.send(action)
        inputs_sent += 1

    print(f"\n--- done: {inputs_sent} inputs sent ---")
    if game.state:
        p = game.state["player"]
        print(f"Final: turn {game.state['turn']}, {p['where']} ({p['x']},{p['y']}), "
              f"health {p['health']}/{p['maxhealth']}, gang {p['gang']}, items {game.state['items']}")
    print(f"Crashes and invariant violations: {len(game.violations)}")
    for v in game.violations:
        print(f"  {v}")
    print(f"Log: {log_path}")
    print(f"Replay: python3 tools/autoplay.py {SEED}")
    game.close()


if __name__ == "__main__":
    main()
