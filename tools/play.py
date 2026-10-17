#!/usr/bin/env python3
"""
Play Gangland yourself, with everything logged.
Usage:  python3 tools/play.py [seed]          play (a random seed if none is given)
        python3 tools/play.py --replay LOG    re-run a logged game's inputs, fast, to reproduce it exactly

While playing:
  # some note      a line starting with # is a note for the log; it is not sent to the game
  Ctrl-D           stop playing (the log is kept)

The log goes to logs/play/play_<time>_seed<N>.log, in the same format as autoplay's logs: OUT lines are the
game's output (including the hidden <<STATE>> lines with the game's internal state), IN lines are what you
typed (each after a TIME line), NOTE lines are your notes. The game's dice are seeded, so the seed plus your inputs replay the game.
"""

import os, re, subprocess, sys, threading
from datetime import datetime
from pathlib import Path

ROOT_DIR  = Path(__file__).parent.parent
CLASS_DIR = os.environ.get("GANGLAND_CLASS_DIR", str(ROOT_DIR / "out"))
LOG_DIR   = ROOT_DIR / "logs" / "play"
MARKER    = re.compile(r"^<<[A-Z_]+[ >]")


def start_game(seed, keep_pauses):
    env = dict(os.environ, GANGLAND_SEED=str(seed), GANGLAND_AUTOMATION="1")
    if keep_pauses:
        env["GANGLAND_KEEP_PAUSES"] = "1"
    return subprocess.Popen(["java", "-cp", CLASS_DIR, "Gangland"], cwd=CLASS_DIR, env=env,
                            stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)


def pump_output(proc, log, show):
    """Copy the game's output to the log, and to the screen without the harness markers."""
    pending = b""
    while True:
        chunk = os.read(proc.stdout.fileno(), 4096)
        if not chunk:
            break
        pending += chunk
        *lines, pending = pending.split(b"\n")
        for raw in lines:
            line = raw.decode("utf-8", "replace")
            log.write(f"OUT {line}\n")
            if show and not MARKER.match(line):
                sys.stdout.write(line + "\n")
        log.flush()
        # text the game printed without a newline yet (e.g. a prompt): show it now, log it once complete
        if show and pending and not pending.startswith(b"<<"):
            sys.stdout.write(pending.decode("utf-8", "replace"))
            log.write(f"OUT {pending.decode('utf-8', 'replace')}\n")
            pending = b""
        sys.stdout.flush()


def play(seed):
    LOG_DIR.mkdir(parents=True, exist_ok=True)
    log_path = LOG_DIR / f"play_{datetime.now():%Y%m%d_%H%M%S}_seed{seed}.log"
    log = open(log_path, "w", encoding="utf-8")
    log.write(f"PLAY seed {seed}\n")
    print(f"Seed {seed}. Logging to {log_path}")
    print("Type # followed by a note at any time to leave yourself a note in the log. Ctrl-D stops.\n")
    proc = start_game(seed, keep_pauses=True)
    reader = threading.Thread(target=pump_output, args=(proc, log, True), daemon=True)
    reader.start()
    try:
        while proc.poll() is None:
            try:
                text = input()
            except EOFError:
                break
            if text.lstrip().startswith("#"):
                log.write(f"NOTE {datetime.now():%H:%M:%S} {text.lstrip()[1:].strip()}\n")
                log.flush()
                print("(noted)")
                continue
            log.write(f"TIME {datetime.now():%H:%M:%S}\n")
            log.write(f"IN  {text!r}\n")
            log.flush()
            try:
                proc.stdin.write((text + "\n").encode())
                proc.stdin.flush()
            except BrokenPipeError:
                break
    except KeyboardInterrupt:
        pass
    finally:
        try:
            proc.stdin.close()
            proc.wait(timeout=5)
        except Exception:
            proc.kill()
        reader.join(timeout=3)
        log.close()
    print(f"\nLog: {log_path}")


def replay(path):
    """Feed a logged game's inputs back in, without pauses; output goes to a .replay.log next to it."""
    text = Path(path).read_text(encoding="utf-8", errors="replace")
    m = re.search(r"^PLAY seed (\S+)", text, re.M) or re.search(r"<<SEED (\S+)>>", text)
    if not m:
        sys.exit("no seed found in that log")
    inputs = [eval(l[4:]) for l in text.splitlines() if l.startswith("IN  ")]   # repr()'d strings
    out_path = Path(path).with_suffix(".replay.log")
    with open(out_path, "w", encoding="utf-8") as log:
        log.write(f"PLAY seed {m.group(1)} (replay of {Path(path).name})\n")
        proc = start_game(m.group(1), keep_pauses=False)
        reader = threading.Thread(target=pump_output, args=(proc, log, False), daemon=True)
        reader.start()
        proc.stdin.write("".join(i + "\n" for i in inputs).encode())
        proc.stdin.close()
        proc.wait(timeout=120)
        reader.join(timeout=5)
    print(f"Replayed {len(inputs)} inputs from seed {m.group(1)}: {out_path}")


if __name__ == "__main__":
    if len(sys.argv) > 2 and sys.argv[1] == "--replay":
        replay(sys.argv[2])
    else:
        play(sys.argv[1] if len(sys.argv) > 1 else str(int.from_bytes(os.urandom(4), "big")))
