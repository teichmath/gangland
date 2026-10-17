#!/usr/bin/env python3
"""
Run many seeded autoplay games in parallel and summarize what went wrong.
Usage:  python3 tools/swarm.py [games] [first_seed] [workers] [--coverage] [--rarity]
        python3 tools/swarm.py 200 1 8
Each game is reproducible with:  python3 tools/autoplay.py <seed>
Logs of games with a problem are kept in logs/swarm.noindex/ (the .noindex suffix keeps
Spotlight from indexing them); logs of clean games are deleted.
--coverage runs every game under JaCoCo (tools/jacoco/lib) and writes an HTML report of which
lines of the game ran to logs/coverage.noindex/report/index.html.
--rarity (implies --coverage) also counts how many games ran each line: logs/coverage.noindex/rarity.json.
"""

import subprocess, sys, re, os, shutil
import xml.etree.ElementTree as ET
from collections import defaultdict
from concurrent.futures import ThreadPoolExecutor
from pathlib import Path

ROOT_DIR = Path(__file__).parent.parent
LOG_DIR  = ROOT_DIR / "logs" / "swarm.noindex"
COV_DIR  = ROOT_DIR / "logs" / "coverage.noindex"
CLASS_DIR = os.environ.get("GANGLAND_CLASS_DIR", str(ROOT_DIR / "out"))
JACOCO_CLI = ROOT_DIR / "tools" / "jacoco" / "lib" / "jacococli.jar"

rarity   = "--rarity" in sys.argv
coverage = "--coverage" in sys.argv or rarity
args = [a for a in sys.argv[1:] if not a.startswith("--")]
games   = int(args[0]) if len(args) > 0 else 100
first   = int(args[1]) if len(args) > 1 else 1
workers = int(args[2]) if len(args) > 2 else (os.cpu_count() or 4)

env = dict(os.environ, GANGLAND_LOG_DIR=str(LOG_DIR))
if coverage:
    if not JACOCO_CLI.exists():
        sys.exit(f"--coverage needs JaCoCo in {JACOCO_CLI.parent} (jacocoagent.jar, jacococli.jar)")
    shutil.rmtree(COV_DIR, ignore_errors=True)
    env["GANGLAND_COVERAGE_DIR"] = str(COV_DIR / "exec")

def play(seed):
    out = subprocess.run([sys.executable, str(ROOT_DIR / "tools" / "autoplay.py"), str(seed)],
                         capture_output=True, text=True, env=env).stdout
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
            elif line.startswith("NOTE "):
                kind = "note: " + " ".join(line.split()[1:2])
            elif line.startswith("DEBUGMSG"):
                kind = "debug message: " + " ".join(line.split()[1:4])
            elif line.startswith("HIGHSCORE"):
                kind = "high-score table wrong"
            elif "Exception in thread" in line:
                where = next((l.strip()[4:] for l in lines[i+1:i+12] if "at Gangland." in l), "")
                kind = f"crash {line.split(':')[0].split()[-1]} {where}"
            else:
                continue
            if kind not in seen:
                seen.add(kind)
                problems[kind].append((seed, re.sub(r"^(OUT|DEBUGMSG) ", "", line).strip()))
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

if coverage:
    execs = sorted((COV_DIR / "exec").glob("*.exec"))
    print(f"\nCoverage: merging {len(execs)} of {games} games' data (hung games have none)...")
    jacoco = ["java", "-jar", str(JACOCO_CLI)]
    subprocess.run(jacoco + ["merge", *map(str, execs), "--destfile", str(COV_DIR / "swarm.exec")],
                   check=True, capture_output=True)
    # the game's classes only, not the automation harness built into it
    game_classes = [str(c) for c in Path(CLASS_DIR).glob("*.class") if not c.name.startswith("Automation")]
    subprocess.run(jacoco + ["report", str(COV_DIR / "swarm.exec"), *sum((["--classfiles", c] for c in game_classes), []),
                             "--sourcefiles", str(ROOT_DIR / "src"), "--html", str(COV_DIR / "report"),
                             "--xml", str(COV_DIR / "report.xml"), "--name", f"Gangland autoplay, {games} games"],
                   check=True, capture_output=True)
    if rarity:
        subprocess.run([sys.executable, str(ROOT_DIR / "tools" / "rarity.py")], check=True)
    shutil.rmtree(COV_DIR / "exec")

    def counter(el, kind):
        c = el.find(f"counter[@type='{kind}']")
        return (int(c.get("covered")), int(c.get("missed")) + int(c.get("covered"))) if c is not None else (0, 0)
    def pct(cov, tot):
        return f"{cov}/{tot} ({100 * cov / tot:.0f}%)" if tot else "-"

    root = ET.parse(COV_DIR / "report.xml").getroot()
    print(f"  lines {pct(*counter(root, 'LINE'))}, branches {pct(*counter(root, 'BRANCH'))}")
    classes = [c for c in root.iter("class")]
    print("  by class (lines):  " + ", ".join(f"{c.get('name')} {pct(*counter(c, 'LINE'))}"
          for c in sorted(classes, key=lambda c: -counter(c, "LINE")[1])))
    game = next(c for c in classes if c.get("name") == "Gangland")
    methods = [(m.get("name"), int(m.get("line") or 0), *counter(m, "LINE")) for m in game.iter("method")]
    never = [m for m in methods if m[2] == 0 and m[3] > 0]
    print(f"  Gangland methods never run: {len(never)} of {len(methods)}")
    print("  Gangland methods with the most lines never run (method, line, ran/total):")
    for name, line, cov, tot in sorted(methods, key=lambda m: -(m[3] - m[2]))[:15]:
        print(f"    {name:32s} {line:6d}   {cov}/{tot}")
    print(f"  Report: {COV_DIR / 'report' / 'index.html'}")
