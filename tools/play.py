#!/usr/bin/env python3
"""
Gangland human-play logging harness.
Starts the game and passes your keystrokes through, logging everything.
Usage:  python3 play.py
Log is saved to play_<timestamp>.log in the game directory.
"""

import subprocess, threading, sys, time
from datetime import datetime
from pathlib import Path

ROOT_DIR  = Path(__file__).parent.parent
CLASS_DIR = str(ROOT_DIR / "out")
LOG_DIR   = ROOT_DIR / "logs"
LOG_DIR.mkdir(exist_ok=True)
log_path  = str(LOG_DIR / f"play_{datetime.now():%Y%m%d_%H%M%S}.log")


class Tee:
    """Write to a file and a stream simultaneously, with timestamps."""
    def __init__(self, path):
        self._f = open(path, "w", encoding="utf-8")

    def write_out(self, text):
        ts = datetime.now().strftime("%H:%M:%S.%f")[:-3]
        self._f.write(f"{ts} OUT {text}")
        self._f.flush()

    def write_in(self, text):
        ts = datetime.now().strftime("%H:%M:%S.%f")[:-3]
        self._f.write(f"{ts} IN  {text!r}\n")
        self._f.flush()

    def close(self):
        self._f.close()


def reader_thread(proc, tee):
    """Copy game stdout → user's stdout and log."""
    try:
        for line in proc.stdout:
            sys.stdout.write(line)
            sys.stdout.flush()
            tee.write_out(line)
    except Exception:
        pass


def main():
    print(f"Logging to: {log_path}\n", flush=True)
    tee = Tee(log_path)

    proc = subprocess.Popen(
        ["java", "-cp", CLASS_DIR, "Gangland"],
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        cwd=CLASS_DIR,
        text=True,
        bufsize=1,
    )

    threading.Thread(target=reader_thread, args=(proc, tee), daemon=True).start()

    try:
        for line in sys.stdin:
            tee.write_in(line.rstrip("\n"))
            proc.stdin.write(line)
            proc.stdin.flush()
    except (KeyboardInterrupt, EOFError):
        pass
    finally:
        proc.terminate()
        tee.close()
        print(f"\nSession saved to: {log_path}", flush=True)


if __name__ == "__main__":
    main()
