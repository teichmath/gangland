#!/usr/bin/env python3
"""
How rare is each line of the game?  Counts, for every source line, how many games ran it.
Usage:  python3 tools/rarity.py [exec_dir]
        (exec_dir defaults to logs/coverage.noindex/exec, which `swarm.py --coverage --rarity` keeps)
Writes logs/coverage.noindex/rarity.json: {"games": N, "files": {"Gangland.java": {"<line>": games_that_ran_it}}}
Line numbers are editor line numbers (javac counts the bare \\r in main()'s title art as line breaks; this undoes that).
"""

import json, os, subprocess, sys, tempfile
import xml.etree.ElementTree as ET
from collections import Counter, defaultdict
from concurrent.futures import ThreadPoolExecutor
from pathlib import Path

ROOT_DIR  = Path(__file__).parent.parent
COV_DIR   = ROOT_DIR / "logs" / "coverage.noindex"
CLASS_DIR = Path(os.environ.get("GANGLAND_CLASS_DIR", str(ROOT_DIR / "out")))
JACOCO    = ["java", "-jar", str(ROOT_DIR / "tools" / "jacoco" / "lib" / "jacococli.jar")]

exec_dir = Path(sys.argv[1]) if len(sys.argv) > 1 else COV_DIR / "exec"
execs = sorted(exec_dir.glob("*.exec"))
if not execs:
    sys.exit(f"no .exec files in {exec_dir}")
classes = sum((["--classfiles", str(c)] for c in CLASS_DIR.glob("*.class") if not c.name.startswith("Automation")), [])

def javac_to_editor(path):
    """javac line number -> editor line number (javac also breaks lines at a lone \\r)."""
    text = (ROOT_DIR / "src" / path).read_bytes()
    mapping, javac, editor, i = {1: 1}, 1, 1, 0
    while i < len(text):
        c = text[i]
        if c == 0x0A or (c == 0x0D and (i + 1 >= len(text) or text[i + 1] != 0x0A)):
            javac += 1
            if c == 0x0A:
                editor += 1
            mapping[javac] = editor
        i += 1
    return mapping

def lines_run(exec_file):
    with tempfile.NamedTemporaryFile(suffix=".xml") as xml:
        subprocess.run(JACOCO + ["report", str(exec_file), *classes, "--xml", xml.name],
                       check=True, capture_output=True)
        root = ET.parse(xml.name).getroot()
    return {(sf.get("name"), int(l.get("nr"))) for sf in root.iter("sourcefile")
            for l in sf.iter("line") if int(l.get("ci")) > 0}

counts = Counter()
with ThreadPoolExecutor(os.cpu_count() or 4) as pool:
    for n, ran in enumerate(pool.map(lines_run, execs), 1):
        counts.update(ran)
        print(f"\r{n}/{len(execs)} games", end="", flush=True, file=sys.stderr)
print(file=sys.stderr)

files = defaultdict(dict)
maps = {}
for (name, line), c in counts.items():
    if name not in maps:
        maps[name] = javac_to_editor(name)
    files[name][str(maps[name].get(line, line))] = c
out = COV_DIR / "rarity.json"
out.write_text(json.dumps({"games": len(execs), "files": files}))
print(f"{len(execs)} games -> {out}")
