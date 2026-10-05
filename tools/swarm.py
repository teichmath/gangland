#!/usr/bin/env python3
"""
Run many seeded autoplay games in parallel and summarize what went wrong.
Usage:  python3 tools/swarm.py [games] [first_seed] [workers]
        python3 tools/swarm.py 200 1 8
Each game is reproducible with:  python3 tools/autoplay.py <seed>
Logs of games with a problem are kept in logs/swarm.noindex/ (the .noindex suffix keeps
Spotlight from indexing them); logs of clean games are deleted.
"""

import subprocess, sys, re, os
from collections import defaultdict
from concurrent.futures import ThreadPoolExecutor
from pathlib import Path

ROOT_DIR = Path(__file__).parent.parent
LOG_DIR  = ROOT_DIR / "logs" / "swarm.noindex"

games   = int(sys.argv[1]) if len(sys.argv) > 1 else 100
first   = int(sys.argv[2]) if len(sys.argv) > 2 else 1
workers = int(sys.argv[3]) if len(sys.argv) > 3 else (os.cpu_count() or 4)

def play(seed):
    out = subprocess.run([sys.executable, str(ROOT_DIR / "tools" / "autoplay.py"), str(seed)],
                         capture_output=True, text=True,
                         env=dict(os.environ, GANGLAND_LOG_DIR=str(LOG_DIR))).stdout
    m = re.search(r"^Log: (.*)$", out, re.M)
    return seed, Path(m.group(1)) if m else None

problems = defaultdict(list)   # kind -> [(seed, example line)]

with ThreadPoolExecutor(workers) as pool:
    for n, (seed, log) in enumerate(pool.map(play, range(first, first + games)), 1):
        print(f"\r{n}/{games} games", end="", flush=True)
        if log is None:
            problems["harness failure"].append((seed, ""))
            continue
        lines = log.read_text(encoding="utf-8", errors="replace").splitlines()
        seen = set()
        for i, line in enumerate(lines):
            m = re.match(r"OUT <<(INVARIANT|AUTOMATION_ERROR) ?(\S*)", line)
            if m:
                kind = f"{m.group(1).lower()} {m.group(2)}"
            elif line.startswith("HANG"):
                kind = "hang (no prompt; game looping)"
            elif "Exception in thread" in line:
                where = next((l.strip()[4:] for l in lines[i+1:i+12] if "at Gangland." in l), "")
                kind = f"crash {line.split(':')[0].split()[-1]} {where}"
            else:
                continue
            if kind not in seen:
                seen.add(kind)
                problems[kind].append((seed, line[4:].strip()))
        if not seen:
            log.unlink()

print(f"\n\n{games} games, seeds {first}-{first + games - 1}\n")
if not problems:
    print("No crashes or invariant violations.")
for kind, hits in sorted(problems.items(), key=lambda kv: (not kv[0].startswith("crash"), -len(kv[1]))):
    seeds = [s for s, _ in hits]
    print(f"{len(hits):4d} games  {kind}")
    print(f"            e.g. seed {seeds[0]}: {hits[0][1][:150]}")
    print(f"            seeds: {', '.join(map(str, seeds[:12]))}{' ...' if len(seeds) > 12 else ''}")
