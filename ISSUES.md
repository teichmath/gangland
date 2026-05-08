# Gangland — Known Issues

Status key: `fixed` | `open` | `wontfix` | `unreachable`

---

## Crashes / Hard Bugs

### C1 — `startRumor` NPE on "kills" tag
**File:** `Gangland.java` ~line 22355  
**Status:** fixed  
If `getNumberFromName()` returns `-1` (name not found), it was used directly as an array index into `army[]`, throwing `ArrayIndexOutOfBoundsException`. Fixed by caching the index and guarding with `if(subject >= 0)` before array access.

### C6 — `attack`/`greet` with only a buried corpse present crashes with AIOOB
**File:** `Gangland.java` ~line 6190  
**Status:** fixed  
`only_other_soul` is initialized to -1. `number_present` is incremented for dead buried bodies as well as live people. If the only entity at a location is a buried corpse, `number_present == 1` but `only_other_soul == -1`, causing `army[-1]` → `ArrayIndexOutOfBoundsException`. Discovered by autoplay. Fixed by using `number_present_alive` (count of live people only) in the greet/attack targeting check.

### C2 — Pet friend-set with -1 index after master death
**File:** `Gangland.java` ~line 17934  
**Status:** open  
`army[pet].setfriend(getNumberFromName(army[army[pet].getmasters_number()].getslayer()), ...)` — if the slayer name isn't found, `getNumberFromName` returns `-1`. `Player.setfriend` guards against `-1` so this won't crash, but the pet silently gets no new bond target. Acceptable for now but worth revisiting for correctness.

### C3 — `IOException` calls `System.exit(1)`
**File:** `Gangland.java` ~line 21336  
**Status:** fixed  
`stringInput()` caught `IOException` and called `System.exit(1)`, killing the session. Also, `readLine()` returning `null` (EOF) would have caused a NullPointerException on `.trim()`. Fixed to return `""` in both cases — the game's existing input validation loops re-prompt on empty input, so the session continues without interruption.

### C4 — `bio_tome` second dimension was 100, now fixed
**File:** `Gangland.java` lines ~559 and ~4247  
**Status:** fixed  
Array was `[armysize][100]`; generation counter can exceed 100, causing `ArrayIndexOutOfBoundsException`. Expanded to `[armysize][500]`.

### C5 — `joinmealchoice` assignment-instead-of-comparison
**File:** `Gangland.java` ~line 9309  
**Status:** fixed  
`if(joinmealchoice = false)` always evaluated false; player could never leave a zombie meal. Fixed to `if(!joinmealchoice)`.

---

## Unreachable / Partially Implemented Features

### U1 — Cave of Lives
**File:** `Gangland.java` ~line 5511; method at ~line 20047  
**Status:** unreachable  
The cave entrance now seals before the player can decide. The `caveOfLives()` method itself has substantial implementation (past-lives doors, darkness, laughter), but the cave battle loop ("battle against all previous rufuses, final battle against boss") is not implemented. Left unreachable until complete.

### U2 — `delmeDrinkText()` dead method
**File:** `Gangland.java` ~line 23490  
**Status:** open  
Method body is entirely commented out; method is never called. Either restore drinking mechanics or delete the method.

### U3 — Broken apples (TODO in code)
**File:** `Gangland.java` ~line 5124: `//todo: restore broken apples.`  
**Status:** open  
Apple items decay and drop correctly in the game loop, but something about the "broken" apple state is unfinished. Exact nature of the break unknown without deeper investigation.

### U4 — Spyglass inside the forest
**File:** `Gangland.java` line ~6005: spyglass explicitly disabled in forest  
**Status:** open  
`"Your spyglass is no good in the forest."` — planned to work but currently blocked. Noted as a todo at line 94.

### U5 — Pets don't follow into the forest
**File:** `Gangland.java` ~lines 3271-3272 (todo comments)  
**Status:** open  
Pets do not follow masters into the forest. Pets without a master don't exit either. Also noted in dev notes at line 65 and 115.

---

## Semantic / Display Issues

### S1 — Red snake species check
**File:** `Snake.java`, `Gangland.java` ~line 13051  
**Status:** fixed  
Brief battle text showed `"a snake"` for red snakes. Fixed display string. Also added `getspecies()` override to `Snake.java` so species-based logic always returns `"snake"` regardless of color.

### S2 — `Wererat` had undefined fields in constructor
**File:** `Wererat.java`  
**Status:** fixed  
`this.clown` and `this.number_of_pickles` were set but never declared. Removed. (Was causing Railway build failure.)

---

## Debug Commands Removed

### D1 — Developer input commands
**File:** `Gangland.java` main input loop  
**Status:** fixed  
Removed from player-accessible input: `rumor report`, `report`, `zombie report`, `pets`, `who knows zombies`, `longhouse`. Kept: `curs`, `handy map`, `stats`, `skeletons`.

---

## Developer Notes / Future Work (from in-code TODOs)

These are lower priority items noted by the developer; not crashes, just missing polish:

- **Dialogue rewrite / new jokes** (line 93)
- **Rumors about who exits the forest** (line 96)
- **Purpose conversations between NPCs** (line 97)
- **NPC gangs less likely to enter forest** (line 95)
- **Rumor of a cur should be cleared before and after main action loop** (line 16710)
- **Forgiven NPCs should like you more** (line 23290)
- **Cave battle content** — battle all previous Rufuses, final boss (line 66)

---

## Known Behavior Anomalies (from dev notes, uninvestigated)

These were flagged in the dev notes at the top of `Gangland.java` but haven't been investigated:

- Snake non-red variant incorrectly poisoning player (line 124 of dev notes)
- "Another walking corpse joins" appearing during beast (snake) battles
- Dog grief scene triggering when dog is not actually at that location
- Too many "aiees" from the same NPC
- Zombie/beast battle pulling player in from a different location
- Getting stuck at specific turn counts (1189 noted)
- High score tracking issue
