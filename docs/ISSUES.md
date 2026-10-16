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

### C7 — Zombie player pulled into a battle they declined, then `Battle` crashes
**File:** `Gangland.java` ~line 15890 (mid-battle zombie join) and ~line 14520 (`battle_choice_numbers.get(size() - 1)`)  
**Status:** fixed  
When the player is a zombie and answers N to "Do you join the battle?", the battle runs without the player's action menu. But each round, every zombie present (`zombies_who_fight`, which includes the player) gets a 50% chance to wander into the fight, so the player could be pulled in anyway with no battle choices recorded. The run-away check then read the last choice from the empty list: `IndexOutOfBoundsException: Index -1 out of bounds for length 0`. Discovered by autoplay (seeds 106, 137). Fixed by keeping the player out of the mid-battle zombie join (they already chose at the start) and guarding the read with `battle_choice_numbers.size() > 0`, as the drink check above it already does.

### C8 — `Battle` incantation spoken again on a caster's later attacks, index -1
**File:** `Gangland.java` ~line 14997 (attack by incantation)  
**Status:** fixed  
`ArrayIndexOutOfBoundsException: Index -1 out of bounds for length 20` during a beast fight. `uses_incantation` is decided once per round, but the incantation block ran on every one of a being's attacks. A successful incantation erases the speaker's words (`setincantation(-1, -1, -1)`), so a caster with two or more attacks hit `incantations[-1]` on the second. The player's own incantation also went off once per attack, doing its damage again. Fixed by speaking the incantation on the first attack slot only (`z == 0`), which is the slot `battle_order` already reserves for it. Seen once in autoplay before the harness's startup changed (no current replay seed); 1000 seeded games after the fix showed no recurrence.

### C9 — Beings, including the player, left at impossible board positions; the game could hang
**File:** `Gangland.java` — `randomRunaway()`, `runWithoutLeader()`, the party run in `Battle`, the pet movement loop in `main()`, `moveAlongForestCommuters()`; `Player.java` — `changexy()`  
**Status:** fixed  
Live beings ended up with one coordinate on the board and one off it, e.g. `(4,0)`, `(9,-6)`, `(5,11)`. `zeroround_clock()` only applies a pending coordinate that is above 0, so a pending move like `(9,0)` moved x and left y wherever the being was in transit. When it happened to the player, the game looped forever printing "There is no one here." (17 of 1000 autoplay games). Causes found with autoplay:
- **Player hang:** `randomRunaway()`'s forest-entrance branch read `roll == 3 && y-1 > 0 || forest_direction == "north"`, so the forest direction fired on every roll and combined with the rolled direction into a diagonal off the board. Parenthesized so the forest direction only lifts the edge check for its own roll.
- **Pets stuck with a stale forest flag:** pets put back at the forest entrance kept `in_the_forest`, so their later steps went through `changexyoffboard`, which has no edge check. The pet loop now calls the existing `checkAndCorrectInTheForest()` for each pet, and `randomRunaway()` does the same for its runner, so the board/forest rules follow where the being actually is.
- **Pets staying behind at the forest entrance** still took their random step with `changexyoffboard`; now only a pet that follows its master in does.
- **Pets running into the forest from a fight** while their master was outside got no forest perception and landed at an unset entry point; they now get their own via `enterForest()`.
- **Gangs walking inside the forest** used `Player.changexy`, whose edge check was hardcoded for the board (`== 11`), so they could walk out of the forest's south and east sides; the check now uses the forest's bounds (13–18) when starting in the forest.
- **Party run-aways** apply one direction, worked out for a single representative, to every member. Each runner's step is now checked with `validRunStep()` and replaced with that runner's own `randomRunaway()` step if it would leave its grid.

After the fixes, 2000 autoplay games (seeds 1–2000, half of them never used while fixing) had no hangs and no impossible positions.

### C10 — Dead pets mourned over and over, draining the master's morale
**File:** `Gangland.java` `mournPets()` (~line 18160)  
**Status:** fixed  
`mournPets()` runs inside the encounter loop, many times per turn, and a dead pet never lost its master. So every call took another 20–40 morale from the master: in 83 of 100 autoplay games a master's morale was driven to 0 within a single turn and kept there, and in one game a single pet was mourned 1143 times. The mourning text never appeared at all, because the inner check tested the pet's health both `< 0` and `> 0` (it meant the master). Also `text` carried over between pets and a pet killed at exactly 0 health was skipped. Fixed: each dead pet is mourned once and then released with `leave_master()` (as `petMourns()` does for dead masters), the inner check tests that the master is alive and with the pet, `text` resets per pet, and death is `health <= 0`.

### C11 — Pets could share a name with an NPC
**File:** `Gangland.java` ~line 9880 (pet naming), `nameIsTaken()`  
**Status:** fixed  
The duplicate-name warning only fired for people the player already knew, and even then "Are you sure?" allowed it. Names are looked up by exact text (`getNumberFromName()` returns the first match), so rumors, pet bonds and kill credit could point at the wrong being (46 of 1000 autoplay games). Now any taken name is rejected with "That name's taken around here. Pick another." `nameIsTaken()` checks every current being's name and the base names of all humans, so "Tom 3" is taken if "Tom" is.

### C12 — Graveyard array overflows at 50,000 graves
**File:** `Gangland.java` ~line 3737 (`game.graveyard[game.next_grave] = game.army[j]`)  
**Status:** wontfix (unreachable in normal play)  
`ArrayIndexOutOfBoundsException: Index 50000` was only seen while the autoplay harness kept pressing Enter into a C9 hang. Normal games fill about 1.2–1.5 graves per turn, so 50,000 would take over 30,000 turns. Two related scans were bounded to the filled slots (`next_grave`): the generation lookup for a reused life (~line 3746), which scanned all 50,000 slots and would crash at the first empty one whenever it ran (only reachable from the Cave of Lives, U1), and `findGrave()`, which scanned `user*100` slots.

### C13 — Writing on the ground in the forest crashed
**File:** `Gangland.java` ~line 5940 (More → Write)  
**Status:** fixed  
`writings` only covers the 10×10 board, but Write was offered everywhere, so writing in the forest threw `ArrayIndexOutOfBoundsException: Index 17 out of bounds for length 11`. Reading writings was already limited to the board; Write now uses the same check and says "There's no bare ground here to write on." elsewhere. Found by autoplay (seed 600).

### C14 — Pig hit by an apple bomb crashes `Battle` when it dies
**File:** `Gangland.java` — `multipleWounded()` (~line 16270); pig marker in `Battle` (~line 15364)  
**Status:** fixed  
`NullPointerException` in `getNumberFromName()` when a slain pig's `last_foe` was null. Damage that hits a whole side at once (an apple bomb, or a beast hitting a whole party) goes through `multipleWounded()`, which wounded every target but never recorded the attacker, unlike single-target hits. A pig whose only wound came from an apple bomb died with no `last_foe`, and the pig marker looked up that null name. Fixed by passing the attacker to `multipleWounded()` and calling `setlast_foe()` for each target, so apple-bomb kills now also credit the thrower (pig debts, kill rumors). `getNumberFromName()` also returns -1 for a null name, as it does for any unknown name. Found by autoplay (seed 1170).


### C15 — Initials shorter than 3 letters crash game over
**File:** `Gangland.java` `gameOver()` (~line 24145)  
**Status:** fixed  
`init_entry.substring(0, 3)` threw `StringIndexOutOfBoundsException` when a player who made the high-score table typed 1 or 2 letters, or just pressed Enter. Found as soon as autoplay started quitting games (4 of 5 quitting test games crashed). Now takes up to 3 characters.

### C16 — High-score table lost and duplicated entries
**File:** `Gangland.java` `gameOver()` (~line 24150)  
**Status:** fixed  
Inserting a new score shifted the entries below it top-down, so the displaced score was copied into every slot beneath it and the rest were lost. Example from autoplay: games scoring 68, 41 and 71 produced the table [71, 68, 68, 68]. The shift now runs bottom-up. This is the "High score tracking issue" from the dev notes. Autoplay now checks every high-score table it sees against the games' best scores (`HIGHSCORE` problems in `tools/swarm.py`).

### C17 — Pets from an old fight dragged into a later battle elsewhere
**File:** `Gangland.java` `civilWar()`, `countSidesForBattle()`  
**Status:** fixed  
When a gang brawl (`civilWar`) gathered pets but then turned into a one-on-one `Duel` (the pets "understand their place" and stay out), it returned without reaching `resetBattleAll()`, so `pets_who_fight` kept those pets. The next battle anywhere added them to its war array, wherever they were: for example, a cat standing on the board at (10,4) fought a gnome in the forest at (13,14). This happened in 1,277 of 2,000 autoplay games. It is the dev notes' "battle pulling a player in from a different location" anomaly. Both list builders now start from empty lists. Autoplay checks that every fighter is on the battle square when `Battle()` starts (`fighter_not_at_battle`).

### C18 — Snake and spider venom got in on a missed bite
**File:** `Gangland.java` `Battle()` beast attacks  
**Status:** fixed  
A red snake's or spider's attack called `poison()` whether or not the bite did damage. About 40% of venomous bites miss (red snake 523 of 1,250, spider 7,676 of 19,810 in a swarm), and each miss could still poison the target. Venom now only gets in when the bite lands. The dev notes report this as "snake poisoning incorrectly". Only red snakes are venomous in the code (`Snake.java` overrides the species to "snake" for every color), so a plain snake can't poison anyone.
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

### U5 — Pets and the forest
**File:** `Gangland.java` pet movement loop in `main()` (~lines 3277–3460)  
**Status:** fixed  
The old note ("pets do not follow masters into the forest") was out of date: pets did follow, but the decision ignored loyalty in practice, a pet that missed the moment could never follow, and a pet that fell behind inside was sent straight back out. Now:
- Entering is decided by loyalty alone (8+ always, 4–7 at loyalty×10%, 3 or less never). The old 4–7 roll was inverted (`d10 > loyalty`), and it rarely mattered because the general follow checks passed first: in 300 autoplay games on the previous commit every pet at the entrance followed, at every loyalty.
- A pet can follow its master in on a later turn: it heads for the entrance and takes the same loyalty test each turn it's there.
- Inside, a pet follows its master anywhere in the forest, aiming for the master's actual square (the turn's movement table only records board squares and pointed pets at (0,0)).
- A pet in the forest whose master has left no longer tries to walk toward him: the "within 6 squares" check compared forest and board coordinates, and could walk the pet off the forest's edge. It's put back at the entrance instead.
- The "stands back" message now shows for your own pet even when you aren't in a gang (`sameGang` is false for yourself with no gang), and only on the turn the master actually goes in.

Measured over 300 autoplay games: follow rates at the entrance were 0% for loyalty 1–3, 42–69% for 4–7, 100% for 8–9, with about half of all entries being late follows. The bot never brings a pet of its own to the forest, so the "stands back" message is checked by reading the code only.

---

## Semantic / Display Issues

### S1 — Red snake species check
**File:** `Snake.java`, `Gangland.java` ~line 13051  
**Status:** fixed  
Brief battle text showed `"a snake"` for red snakes. Fixed display string. Also added `getspecies()` override to `Snake.java` so species-based logic always returns `"snake"` regardless of color.

### S2 — `Wererat` had undefined fields in constructor
**File:** `Wererat.java`  
**Status:** fixed  
`this.clown` and `this.number_of_pickles` were set but never declared. Removed. (Was causing Railway build failure.) The class itself was later archived (A2): nothing ever created a Wererat.

### S3 — Female-game "Ardina" pet-name check tested the wrong variable
**File:** `Gangland.java` ~line 9889  
**Status:** fixed  
`(greet_choice.compareToIgnoreCase("ardina") == 0 && !malegame)` now tests `pet_name_answer`, like the male-game "gregor" check.

### S4 — Game bible ranges for opinion and personality were out of date
**File:** `docs/GAME_BIBLE.md` §2  
**Status:** fixed  
Personality is now listed as 1–100 (rolled 1d100), and `friend[i]` documents the grudge values: 100 toward someone who attacks, deserts the gang or runs out on a duel, 200 toward battle runaways.

---

## Archived Features

### A1 — Female version of the game
**Status:** archived (October 2026)  
The "Male or Female?" choice is gone; the game always plays the male version. Everything that made the female version work (women's names, Gregor→Ardina, the pronoun and word substitutions in `masterStringOut()`, the name checks) is preserved verbatim in `docs/archive/female-version.md`, and in git history.

### A2 — Wererat
**Status:** archived (October 2026)  
`src/Wererat.java` was never used: nothing created one, and its attack methods used an interface the battle code no longer calls. It was an early copy of `Rat`. Kept verbatim in `docs/archive/wererat.md`. The live rat creatures are `Rat` (board) and `Rattishman` (forest).

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
- **Stack-trace line numbers are 16 ahead of the editor** — the title-art line near the top of `main()` uses bare `\r` line breaks, which `javac` counts as lines. Converting that line to normal line breaks would make traces match.

---

## Known Behavior Anomalies (from dev notes)

These were flagged in the dev notes at the top of `Gangland.java`. Each now has an autoplay check (rule names in parentheses), or was traced in the code:

- ~~Snake non-red variant incorrectly poisoning player~~: venom got in on missed bites; see C18.
- ~~"Another walking corpse joins" appearing during beast (snake) battles~~: these messages are now gated on `!they_fight_beasts`. The `corpse_join_in_beast_fight` check found none in 4,000 games.
- ~~Dog grief scene triggering when dog is not actually at that location~~: the `pet_grief_not_there` check found none in 4,000 games. The C9/U5 forest fixes and the C10 mourning fix probably cured this.
- ~~Too many "aiees" from the same NPC~~: the harness notes any name that cries "aiee" more than once in a game (`note: aiee`). None in 4,000 games.
- ~~Zombie/beast battle pulling player in from a different location~~: a stale pet list; see C17.
- ~~Getting stuck at specific turn counts (1189 noted)~~: 1189 is not a turn count. It is the debug print `*** Stuck at 1189` (`Gangland.java` ~line 1793). It sits in a loop that now gives up after 12 tries, and coverage shows the print never runs. Autoplay flags any of the game's own debug prints (`Wha? NNNN`, `Stuck at`) as `DEBUGMSG` problems. The first swarm with that check caught a different one, `*** Stuck at 1356`, in 39 of 2,000 games. The dragon chose its victim by rerolling over everyone on its square until it hit a living, non-zombie human, and it printed this line on every try past the 10th. That was harmless, since such a human always exists there, but the player could see it. It now makes one roll among the eligible humans.
- ~~High score tracking issue~~ → found and fixed, see C16
