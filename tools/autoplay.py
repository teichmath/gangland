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
DEBUG_MSG   = re.compile(r"^\s*\**\s*(Wha\?|Stuck at|The Undead .* is in the longhouse)")

STUCK_SECS  = 10.0  # no prompt for this long = the game is hung

# Usage: autoplay.py [seed]   -- same seed + same choices replays the same game.
SEED        = sys.argv[1] if len(sys.argv) > 1 else str(random.randrange(2**63))
random.seed(SEED)           # makes the harness's own choices reproducible too

# Play style, picked per seed (from its own random stream, so a wanderer game replays exactly as before
# styles existed) or forced with GANGLAND_STYLE:
#   wanderer   roams and answers questions at random; deserts gangs about as often as it joins them
#   loyal      joins gangs and stays: follows the leader, won't abandon it, greets gangmates, calls out deserters
#   ambitious  like loyal, but wants to lead: challenges leaders, claims leadership, opposes newcomers, fights for it
STYLES      = {"wanderer": 0.4, "loyal": 0.35, "ambitious": 0.25}
STYLE       = os.environ.get("GANGLAND_STYLE") or random.Random(f"style{SEED}").choices(list(STYLES), list(STYLES.values()))[0]
if STYLE not in STYLES:
    sys.exit(f"GANGLAND_STYLE must be one of {', '.join(STYLES)}")
GANG_MINDED = STYLE != "wanderer"
# roughly 2 inputs per game turn; gang styles play longer, since gang politics take time to unfold
MAX_INPUTS  = int(os.environ.get("GANGLAND_MAX_INPUTS", 700 if GANG_MINDED else 400))

log_path = str(LOG_DIR / f"autoplay_{datetime.now():%Y%m%d_%H%M%S}_seed{SEED}_{STYLE}_{os.getpid()}.log")

# GANGLAND_COVERAGE_DIR=<dir> runs the game under the JaCoCo agent, writing <dir>/seed<N>_<pid>.exec on exit.
COVERAGE_DIR = os.environ.get("GANGLAND_COVERAGE_DIR")
JACOCO_AGENT = ROOT_DIR / "tools" / "jacoco" / "lib" / "jacocoagent.jar"


# ---------------------------------------------------------------------------
# Game subprocess wrapper
# ---------------------------------------------------------------------------

class Game:
    def __init__(self):
        java = ["java", "-cp", CLASS_DIR, "Gangland"]
        if COVERAGE_DIR:
            Path(COVERAGE_DIR).mkdir(parents=True, exist_ok=True)
            exec_file = Path(COVERAGE_DIR).resolve() / f"seed{SEED}_{os.getpid()}.exec"
            java.insert(1, f"-javaagent:{JACOCO_AGENT}=destfile={exec_file}")
        self.proc = subprocess.Popen(
            java,
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
        self.quit_reached_game_over = False
        self.best_scores = []              # each finished game's "BEST SCORE" (high scores last for the whole run)
        self._table = None                 # high-score table rows while one is being printed
        self._log = open(log_path, "w", encoding="utf-8")
        self._reader_thread = threading.Thread(target=self._reader, daemon=True)
        self._reader_thread.start()

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
                        KNOWN_NAMES.update(b["name"] for b in self.state["here"] if b["species"] == "human")
                    elif line.startswith(("<<INVARIANT", "<<AUTOMATION_ERROR")):
                        self.violations.append(line.strip())
                        sys.stdout.write(f"\033[31m{line}\033[0m")
                    continue
                if "Exception in thread" in line:
                    self.violations.append("CRASH " + line.strip())
                if DEBUG_MSG.match(line):
                    # the game's own "this should never happen" prints (Wha? 1694, *** Stuck at 1189)
                    self.violations.append("DEBUGMSG " + line.strip())
                    self._log.write(f"DEBUGMSG {line.strip()}\n")
                self._watch_high_scores(line)
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

    def _watch_high_scores(self, line):
        """Check each printed high-score table against the best scores of the games played so far."""
        m = re.search(r"BEST SCORE\s*:\s*(-?\d+)", line)
        if m:
            self.best_scores.append(int(m.group(1)))
        if "HIGH SCORES" in line:
            self._table = []
        elif self._table is not None:
            row = re.match(r"\s*\d+\.\s+\S.*?\s(-?\d+)\s*$", line)
            if row:
                self._table.append(int(row.group(1)))
            elif line.startswith("*******"):
                expected = sorted((b for b in self.best_scores if b > -1), reverse=True)[:10]
                if self._table != expected:
                    problem = f"HIGHSCORE table {self._table} but games' best scores were {self.best_scores}"
                    self.violations.append(problem)
                    self._log.write(problem + "\n")
                self._table = None

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
        # Closing stdin makes the game exit normally (<<EOF>> under automation), which lets the
        # coverage agent write its data; a game that doesn't exit in time (e.g. hung) is killed.
        try:
            self.proc.stdin.close()
            self.proc.wait(timeout=10)
        except Exception:
            self.proc.kill()
        self._reader_thread.join(timeout=5)   # let the game's last output reach the log
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
SKIP      = frozenset({"Quit", "Help"})   # chosen deliberately: Help now and then, Quit at the end (see main)
HELP_CHANCE = 0.03          # how often to open Help from a menu that offers it
CHALLENGE_CHANCE = 0.35     # how often to take "Challenge" when a greeting offers it (a way to become leader)
QUIT_AT_END_CHANCE = 0.5    # how many games end by quitting, to reach game over and the high-score table
INITIALS  = ["ABC", "AT", "Q", "", "ALEXANDER"]   # includes short and empty answers on purpose
MORE_GAMES_CHANCE = 0.5     # after quitting, how often to begin again (tests restarting and the high-score table)
NEXT_GAME_INPUTS = 120      # length of each extra game before quitting it too
MORE_PICKS = frozenset({"Stats", "Who", "Remember"})

# Exact names (with generation numbers) of people the player has been near, from <<STATE>> lines.
KNOWN_NAMES = set()

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

    if "Who are you" in tail:          # a new game after "begin again"
        return "Rufus"
    if "What will you name" in tail or "That name's taken" in tail:
        return random.choice(PET_NAMES)
    # Help: read a few topics, then leave
    if "Way of Gangs" in tail:         # the game's line wrapping can split "The Way of Gangs"
        return random.choice(["1", "2", "3", "4", "Exit", "Exit"])
    if "enter a number" in tail.lower() or 'type "Exit"' in tail:
        return "Exit"
    # "Whom are you greeting/attacking?" — always "stranger"; names in the buffer
    # often belong to absent NPCs mentioned in passing, causing "What?".
    if "Whom are you" in tail:
        # a lettered list of who's here: answer in each of the accepted forms, and sometimes cancel
        letters = re.findall(r"\((\w)\.\)", "\n".join(lines[-12:]))
        if "(letter)" in tail and letters:
            verb = re.search(r"Whom are you (\w+?)ing", tail)
            verb = verb.group(1) if verb else "attack"
            pick = random.choice(letters)
            return random.choice([pick, f"{verb} {pick}", f"{verb[0]} {pick}", pick, ""])
        return "stranger"
    if "Loot whom?" in tail or "Bury whom?" in tail or "Eat whom?" in tail:
        return random.choice(["a", "a", "a", ""])      # Enter backs out
    # "Which dog do you want to ask X about?" lists the pets here by letter, and only a letter or name gets out
    if "do you want to ask" in tail and "(letter)" in tail:
        return random.choice(["a", "a", "a", ""])      # Enter backs out
    # beast battles label opponents a, b, c...; a bare "a" only works against a single one
    if "(letter)" in tail and "want to" not in tail:
        return random.choice(["a a", "a b", "a c"])
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

def choose_yn_gang(lines):
    """A gang-minded style's answer to a Y/N question, or None to answer at random."""
    q = "\n".join(lines[-4:])
    amb = STYLE == "ambitious"
    if "Let's team up" in q or "Do you follow?" in q or "Do you call for support?" in q \
            or "Do you join the battle?" in q or "Do you take a side" in q or "teach me some fightin" in q:
        return "Y"
    if "want to abandon" in q:
        return "N"
    if "should be leader!" in q and "Do you agree" in q:     # someone else claims leadership
        return "N" if amb else ("Y" if random.random() < 0.8 else "N")
    if "Willing to fight for it?" in q or "Do you continue this challenge?" in q:
        return "Y" if amb else None
    if "Do you oppose" in q:                                 # a newcomer wants into the gang
        return ("Y" if random.random() < 0.5 else "N") if amb else ("Y" if random.random() < 0.15 else "N")
    return None

def gangmates(state):
    """Names of the player's gangmates on the player's square."""
    if not state or state["player"]["gang"] <= 0:
        return []
    return sorted(b["name"] for b in state["here"]
                  if b["species"] == "human" and b["gang"] == state["player"]["gang"] and not b["zombie"])

def choose_gang(opts, lines, state):
    """A gang-minded style's pick from a menu, or None to fall back to the wanderer's choice."""
    in_gang = bool(state) and state["player"]["gang"] > 0
    leader = bool(state) and state["player"]["leader"]
    amb = STYLE == "ambitious"
    tail = "\n".join(lines[-8:])
    if "You" in opts and "Me" in opts and "Who should be leader" in tail:
        return "Me" if amb else ("You" if random.random() < 0.7 else "Me")
    if "Run" in opts and in_gang:
        # running from a fight deserts the gang; only when badly hurt
        p = state["player"]
        if p["health"] * 4 >= p["maxhealth"]:
            stay = sorted(opts - {"Run"} - SKIP - {o for o in opts if "hit enter" in o.lower()})
            if stay:
                return random.choice(stay)
    if "Deserter!" in opts:                       # offered only against someone who abandoned the player's gang
        return "Deserter!"
    if "Challenge" in opts and random.random() < (0.7 if amb else 0.1):
        return "Challenge"
    if "Join" in opts and not in_gang and random.random() < 0.7:
        return "Join"
    if "N" not in opts:                           # not a main menu
        return None
    roll = random.random()
    # someone here deserted the player's gang: greet them, which offers "Deserter!"
    if state:
        grudges = sorted(set(state["player"].get("grudges", [])) & {b["name"] for b in state["here"]})
        if grudges and roll < 0.6:
            return "Greet " + random.choice(grudges)
    if in_gang and not leader:
        # stay with the gang: wait for the leader to move, talk to gangmates, rarely wander off
        mates = gangmates(state)
        if mates and roll < 0.35:
            return "Greet " + random.choice(mates)
        if roll < 0.75:
            return ""
        if roll < 0.85:
            return random.choice(sorted(opts & DIRS))
        return None
    if not in_gang and state and roll < 0.4:
        # look for a gang: greet the people here
        people = sorted(b["name"] for b in state["here"] if b["species"] == "human" and not b["zombie"])
        if people:
            return "Greet " + random.choice(people)
    return None

def choose(opts, lines, state=None):
    joined = "\n".join(lines)

    if GANG_MINDED and not (opts <= {"Y", "N"}):
        pick = choose_gang(opts, lines, state)
        if pick is not None:
            return pick

    # --- hit-Enter-only screens -----------------------------------------
    henter = {o for o in opts if "hit enter" in o.lower() or "hit Enter" in o}
    if henter and all(o in henter or o in SKIP for o in opts):
        return ""

    # --- Y / N prompt ----------------------------------------------------
    if opts <= {"Y", "N"}:
        if GANG_MINDED:
            pick = choose_yn_gang(lines)
            if pick is not None:
                return pick
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

    if "Help" in opts and random.random() < HELP_CHANCE:
        return "Help"
    if "Challenge" in rest and random.random() < CHALLENGE_CHANCE:
        return "Challenge"

    if rest:
        pick = random.choice(rest)
        if "(name)" in pick:            # e.g. Gregor's "Where (name)": needs someone's exact name
            names = sorted(KNOWN_NAMES) or find_names(lines)
            pick = pick.replace("(name)", random.choice(names) if names else "stranger")
        return pick
    if dirs:
        return random.choice(dirs)
    return ""


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

def choose_quitting(lines, menu):
    """While ending the game on purpose: quit from a main menu, confirm, answer game over."""
    tail = "\n".join(lines[-6:])
    if "Enter your initials" in tail:
        return random.choice(INITIALS)
    if "Are you sure you want to quit" in tail:
        return "Y"
    if "Quit" in menu:
        return "Quit"
    return None


def main():
    print(f"Logging to {log_path}  (seed {SEED}, {STYLE})\n")
    quit_at_end = random.random() < QUIT_AT_END_CHANCE
    games_wanted = 1 + (random.random() < MORE_GAMES_CHANCE) * random.choice([1, 2]) if quit_at_end else 1
    game = Game()

    # ---- startup sequence -----------------------------------------------
    game.wait_for("Who are you")
    game.send("Rufus")
    # Loading screens (hit Enter, Y/N questions) are just prompts; the main loop handles them.

    # ---- main loop ------------------------------------------------------
    inputs_sent = 0
    quitting = False
    games_over = 0
    quit_from = MAX_INPUTS          # when to start quitting the current game

    while inputs_sent < MAX_INPUTS or (quit_at_end and inputs_sent < quit_from + 100):
        quitting = quit_at_end and inputs_sent >= quit_from
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
        if quitting and any("begin again" in l for l in lines[-4:]):
            game.quit_reached_game_over = True
            games_over += 1
            if games_over >= games_wanted:
                break
            game.clear_buffer()
            game.send("")                  # begin again: a new game in the same run
            inputs_sent += 1
            quit_from = inputs_sent + NEXT_GAME_INPUTS
            continue
        action = choose_quitting(lines, parse_menu(lines)) if quitting else None
        # Priority 1: free-text prompts (no bracket menu)
        if action is None:
            action = choose_freetext(lines)
        # Priority 2: bracket menu detected; otherwise just hit Enter
        if action is None:
            menu = parse_menu(lines)
            action = choose(menu, lines, game.state) if menu else ""
        game.send(action)
        inputs_sent += 1

    game.close()
    print(f"\n--- done: {inputs_sent} inputs sent ---")
    if quit_at_end:
        print(f"Ended by quitting: reached game over {games_over} of {games_wanted} time(s)")
    if game.state:
        p = game.state["player"]
        print(f"Final: turn {game.state['turn']}, {p['where']} ({p['x']},{p['y']}), "
              f"health {p['health']}/{p['maxhealth']}, gang {p['gang']}, items {game.state['items']}")
    # forest screams per NPC (dev notes: "too many aiees from the same person"), noted when a name repeats
    with open(log_path, encoding="utf-8", errors="replace") as f:
        screams = re.findall(r'^OUT (.+?) \(perhaps\) :  "AAaaaaieeeeee', f.read(), re.M)
    with open(log_path, "a", encoding="utf-8") as f:
        for name in sorted(set(screams)):
            if screams.count(name) >= 2:
                f.write(f"NOTE aiee {name} x{screams.count(name)}\n")
    print(f"Crashes and invariant violations: {len(game.violations)}")
    for v in game.violations:
        print(f"  {v}")
    print(f"Log: {log_path}")
    print(f"Style: {STYLE}")
    forced = f"GANGLAND_STYLE={STYLE} " if os.environ.get("GANGLAND_STYLE") else ""
    print(f"Replay: {forced}python3 tools/autoplay.py {SEED}")


if __name__ == "__main__":
    main()
