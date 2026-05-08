# Gangland — Game Bible

This document describes every major game system: how it works, what the player sees, and what choices they face. It also flags incomplete or planned features so that expected behavior can be specified before code is written.

**Status key for features:** `implemented` | `partial` | `planned` | `stub`

---

## Table of Contents

1. [World Structure](#1-world-structure)
2. [Player State](#2-player-state)
3. [NPC System](#3-npc-system)
4. [Social System](#4-social-system)
5. [Rumor System](#5-rumor-system)
6. [Combat System](#6-combat-system)
7. [Pet System](#7-pet-system)
8. [Zombie System](#8-zombie-system)
9. [The Forest](#9-the-forest)
10. [Death and Rebirth](#10-death-and-rebirth)
11. [Items and Inventory](#11-items-and-inventory)
12. [Special Locations and Events](#12-special-locations-and-events)
13. [Win / Loss Conditions](#13-win--loss-conditions)
14. [Incomplete and Planned Systems](#14-incomplete-and-planned-systems)

---

## 1. World Structure

### The Main Board
The overworld is a 10×10 grid (coordinates 1 to `boardside`). Player and NPCs move across this grid one step at a time in the four cardinal directions (N, S, E, W). Each step advances the round clock.

### Named Locations (Markers)
Nine named locations are anchored to specific grid coordinates. The player and NPCs discover and reference them by name.

| # | Name | Notable feature |
|---|------|-----------------|
| 0 | Longhouse | Stone building; Gregor's base; gang gathering point |
| 1 | Stand of evergreens | Dense trees |
| 2 | Cairn of reddish rocks | Red dust → sparks for bombs |
| 3 | Tall grass area | Cover |
| 4 | Ruins | Spyglass spawn |
| 5 | Bog | Bodies sink; ground meat; stake cannot be planted |
| 6 | Fallen tree / pond | Insect encounters; water; poison antidote |
| 7 | Rocky slope / spring (fount) | Water; leadership bonus drink |
| 8 | Steep rocks / fissure | Cave entrance |

### Off-Board Coordinates
These are not accessible to the player but are used internally:

- `(−11, −11)` — storage for completed/retired NPC souls
- `(−10, −10)` — holding area for NPCs before spawn
- `(−5, −5)` — generic transit space
- `(−2, −2)` — dragon transit location
- `(−1, −1)` — tool/item cache
- `(0, 0)` — player in-transit holding
- `boardside+2` — Limbo (dead players waiting for rebirth)
- `boardside+3` to `boardside+8` — Forest (6×6 internal grid)
- `boardside+9` — Cave (currently inaccessible)
- `boardside+10` — Longhouse interior (`house_coord`)

### Movement
- Commands: N, W, S, E
- `changexy()` moves player and advances round clock
- `changexyoffboard()` for forest entry
- `setxyquick()` sets round clock to 100 (marks "in transit" turn)
- Each move may trigger an NPC encounter check at the destination

---

## 2. Player State

### Core Stats
| Field | Meaning |
|-------|---------|
| `health` / `maxhealth` | Current and max HP (range roughly 10–70) |
| `strength` | Base damage contribution |
| `speed` | Attack order in combat |
| `weaponskill` / `skill` | Combat accuracy and damage |
| `defense` | Reduces incoming hits |
| `morale` | Affects decisions, gang cohesion, forest entry eligibility |
| `evaluation` | Derived reputation used for gang standing |
| `affiliation` | 0 = unaffiliated; 1+ = gang number |
| `personality` | 0–99; shapes NPC dialog tone |
| `generation` | Rebirth counter |

### Position and Timing
- `x`, `y` — current grid position
- `last_x`, `last_y` — where player was last turn
- `potential_x`, `potential_y` — where player is heading
- `round_clock` — 0–20 = active; 100 = in transit; 20 = round done
- `just_arrived` — controls player-screen display on arrival

### Status Flags
| Flag | Effect |
|------|--------|
| `is_zombie()` | Can only eat dead bodies; blocked from most social actions |
| `has_symbol()` | Forehead carved; resurrection as zombie on death |
| `is_cursed()` | Curse status (exact effect TBD — see §14) |
| `is_poisoned()` | Health drains each turn; cured at pond or with full bladder |
| `poison_clock` | Countdown to death from poison |
| `is_blessed()` | Immune to zombie sense |
| `soul_claimed()` | Devil contract active; special rebirth path |
| `brains_hunger` | Zombie hunger meter |
| `hidden()` | Player not visible to others |

### Relationships
- `friend[i]` — opinion of army member `i`: −1 = unknown, 0–99 = known (see §4)
- `perceived_pets[]` — pets the player is aware of
- `know_knows[]` — rumor knowledge tracking

### Food / Healing
- `amount_of_meat` — carried food; eating restores health

---

## 3. NPC System

### The Army Array
All entities — player, NPCs, pets, extra zombies — live in `army[]`:

```
army[0 .. humansize-2]          = NPCs
army[humansize-1]               = player (index stored as `user`)
army[humansize .. armysize-1]   = pets (dogs, cats)
army[armysize ..]               = extra zombies (from locker)
```

`humansize` defaults to ~51. Pets start at `humansize`; extra zombies at `humansize + extrazombiesize`.

### NPC Generation
Each NPC is generated with randomized stats (strength, speed, health, weaponskill, morale, personality) and a name. They start at `(−10, −10)` and enter the board via spawn logic.

### Gangs
- `affiliation` field: 0 = unaffiliated, 1+ = gang ID
- `highestgang` tracks how many gangs exist
- Gang members move as a group; only the leader controls direction
- Leaders chosen by `aLeaderForThese()` / `aLeaderInEveryGang()`
- Leader challenges: any member whose `weaponskill` exceeds the leader's by ≥10 may contest
- Player can join a gang (JOIN command) or lead an unaffiliated group

### NPC Behavior Tiers
Controlled by `encountertext` (friendship value drives dialog tier):

| Friendship range | NPC disposition |
|-----------------|----------------|
| −1 | Unknown (first meeting) |
| 0–12 | Friendly / acquainted |
| 13–31 | Neutral |
| 32–81 | Unfriendly |
| 82–99 | Hostile |

During encounters NPCs may: propose JOIN, offer to TEACH weaponskill, tell a JOKE, share a RUMOR, TAUNT, or start FRICTION that escalates to a fight.

### Stat Knowledge
`statKnowledge[][]` and `statUncertainty[][]` track what the player knows about NPC stats. The display shows uncertain estimates until confirmed by interaction.

---

## 4. Social System

### Greeting
- GREET triggers `Encounter()` for the named NPC
- On first meeting: names are learned
- Response dialog is determined by friendship value
- Player can greet: named NPC, "stranger", "dog", "cat", "undead"

### Friendship
- `changefriend(i, delta)` adjusts opinion; `setfriend(i, value)` sets directly
- Shared experiences (teaching, jokes, eating together) raise friendship
- Attacks, taunts, rejection lower it
- Friendship affects: encounter outcome, gang willingness, teaching availability

### Teaching
- NPCs with higher `weaponskill` than the player can offer to teach
- Requires sufficient friendliness
- Raises player's `weaponskill`

### Jokes
- 12 jokes stored in `jokes[][]` (lines 4167–4214)
- A player "hears" a joke from an NPC; can retell it in later encounters
- Telling a joke: boosts morale, improves friendship
- Each joke tracked as heard/unheard per NPC

### Biography / Memory
- `bio_tome[armysize][500]` stores interaction records per NPC (lines 330, 4247)
- Records: JOIN proposals, attacks, rejections, unfriendly encounters, etc.
- Indexed by generation
- Player reads bio entries via the REMEMBER command (line 5985)

### Forgiveness
- Planned: forgiven NPCs should like player more (`// TODO` at line 23290)
- Currently forgiveness is acknowledged but friend boost not implemented

---

## 5. Rumor System

### Structure
- `rumors[]` array — 100 slots
- Each rumor has: type tag, originator, subject, truth flag, turn born

### Rumor Types
| Type | Content |
|------|---------|
| `"gregor"` | Location of Gregor or Ardina |
| `"house"` | Location of the longhouse |
| `"pig"` | Pig sighting |
| `"forest"` | Something about the forest |
| `"cave"` | Something about the cave |
| `"kills"` | Reputation kill count for a named NPC |

### Propagation
- Rumors start via `startRumor()` at fixed `turnclock` milestones: 20, 40, 50, 75, 90
- NPCs exchange rumors when they encounter each other (lines 7024–7071)
- `rumorIsHeardBy()`, `rumorIsKnownBy()`, `rumorKnowsKnows()` track knowledge
- A rumor about the player spreads if their reputation is noteworthy

### Player Interaction
- Player hears rumors during NPC encounters
- REMEMBER menu shows biography and rumor summaries
- Player cannot directly plant false rumors (no mechanism implemented)

---

## 6. Combat System

### Initiating Combat
- Player issues ATTACK command (with target name)
- Combat type is determined by relationship:

| Condition | Combat type |
|-----------|------------|
| Player vs unaffiliated NPC | `Duel()` |
| Player vs different gang | `prepBattle()` (gang vs gang) |
| Player vs same gang member | `civilWar()` |
| Player vs beast | `Beast()` |

### Turn Order
- `battle_order[][][]` determines who acts when (line 264)
- Ordered by `speed` stat
- Can flee via `runWithoutLeader()` (line 241)

### Damage
- Hit roll (d20) vs `defense + moraleadj − att_speed + 3` threshold
- Damage based on `weaponskill` + `strength` + weapon bonus
- Shields reduce incoming damage
- Swords grant critical hit chance

### Results Tracking
- `action_hits[][]`, `action_kills[][]`, `action_lowhealth[][]` — outcomes
- `action_runs[]` — who fled
- `kill_record[][]`, `dead_record[][]` — persistent kill history
- `scar[][]` — battle scars from repeated damage to same target

### Poison
- Applied by snake bites (non-red variant — **known anomaly**)
- `poison_clock` counts down; at 0 the player dies
- Cures: drink from pond (marker 6), or fill bladder and drink

### Beasts — Overworld
Spawn on movement across the main board:

| Beast | Spawn chance | Notes |
|-------|-------------|-------|
| Rat (Wererat) | 12% | Two attacks (bite + scratch) |
| Pig | 6% | |
| Snake | 6% | Bite attack; red variant cosmetic only |
| Skeleton | 3% | Only after `turnclock` 90 (`skeletons_continue` flag) |
| Devil | 3% | Contract / soul-claimed mechanic |

### Beasts — Forest
| Beast | Spawn chance |
|-------|-------------|
| Spider | 16% |
| Creeper | 16% |
| Gnome | 16% |
| Tree | 6% |
| Rattish man | 10% |
| Butterfly | 6% |
| Bluebird | 10% |
| Wizard | 6% — teaches spells / incantations |

### Dragon
- Always a transformative or fatal encounter
- Tracked via dragon transit location `(−2, −2)`

---

## 7. Pet System

### Pet Types and Array Position
- Dogs and cats live in `army[humansize .. armysize-1]`
- Each has a `species` field ("dog" or "cat")
- Names generated or left as species only
- `status: implemented`

### Acquiring a Pet
- Pets exist on the board from game start
- Player meets a pet by moving to its location and greeting/interacting
- Pet sets `new_master(player)` when bond forms
- Player adds pet to `perceived_pets[]`

### Pet Loyalty and Following
- Loyalty tracked by `getloyalty()` (1–10 scale)
- Modified by `getfriend(master)` value and `getrounds_with_master()`
- Activity memory: "food", "dig" entries improve loyalty
- **Follow probability** on each master movement:
  - Dogs: `loyalty * 1.5` (easier to follow)
  - Cats: `loyalty * 0.25` (much harder)
  - Distance from master also reduces probability

### Pet Behaviors in Play
- Dogs assist in burial (strength × 3 / speed boost; dig memory improves depth)
- Pets participate in gang battles when at same location as master
- Pets mourn dead companions via `petsMourn()`

### Pet Death
- Pet health tracked independently
- Pets can be killed in battle
- On death: master grieves, `lose_perception_of_pet()` called
- Bond reset; pet no longer follows

### Known Gaps
- Pets do not follow master into the forest (`status: planned`, lines 3271–3272)
- Pets without a master do not exit the forest (`status: planned`, line 65)
- Mourning behavior ("mourning pets!!!") partially implemented (`status: partial`)

---

## 8. Zombie System

### Becoming a Zombie
1. An NPC or the player has their forehead carved (`has_symbol()` = true)
2. On death, if burial depth < 30, `rise_to_seek_brains()` activates
3. Character is reborn as a zombie rather than a normal rebirth

### Zombie State
- Narration at resurrection varies by burial depth (lines 3823–3832)
- `brains_hunger` starts negative (very hungry)
- Zombie cannot: GREET, TEACH, DRINK, GO INSIDE, BURY, LOOT

### Zombie Behavior
- Must find and EAT dead bodies (brains)
- `zombieBrainShare()` handles multiple zombies splitting a meal
- `ground_meat` global tracks whether a meal is currently available on the board
- Eating satisfies hunger temporarily

### Skeleton Waves
- At `turnclock` 90: `skeletons_continue` flag enables skeleton spawns
- New zombies spawned from the locker at marker locations each turn
- At 20+ active zombies: special message fires; at 40+: escalated message

### Zombie Senses
- Stake (item, planted at non-bog locations) attracts and senses zombies
- `is_blessed()` player is immune to zombie-sense detection

---

## 9. The Forest

### Entry Requirements
- Player must be at `forest_entry_x, forest_entry_y` (adjacent to the board edge facing the forest)
- Direction command must match `forest_direction`
- `morale > 35` required
- `health > maxhealth − 3` required
- If gang is chanting (`chantIsOn()`), forest entry may be forced (lines 3159–3188)

### Forest Layout
- 6×6 internal grid at coordinates `boardside+3` to `boardside+8`
- Player tracks perceived position separately from actual position
- Map builds up as exploration proceeds (lines 4596–4619)
- Exit location known once discovered: `getforest_exit_perceived_x/y`

### Forest Encounters
Beast encounters fire on each move (see §6 forest table). No NPC encounters occur inside the forest.

### Navigating Out
- Player must reach the exit perceived coordinates
- Successful exit triggers celebration sequence (lines 1070–1142)
- Gang celebration / chanting fires on exit (lines 1115–1140)

### Forest Limitations
- Spyglass (LOOK) disabled: "Your spyglass is no good in the forest." (`status: planned`)
- No zombie/curse access
- Cave cannot be entered from forest
- Pets do not follow in (`status: planned`)

---

## 10. Death and Rebirth

### Dying
- `health <= 0` sets `just_died()` flag
- Body automatically placed (often sinks in bog at marker 5)
- Burial depth randomized by encounter context
- `number_dead` counter incremented

### Three Rebirth Paths

#### Normal Rebirth
- Triggered when: `number_dead >= 10` OR `resurrection_clock() <= 0`
- Requirements: not soul-claimed, not recently carved, or burial too deep for zombie rise
- New player generated via constructor VI (line 3748)
- Stats reset; `generation` incremented
- Rebirth narrative by generation:
  - Gen 1: First-death narrative (specific)
  - Gen 2: "woozy"
  - Gen 3: "foggy memories"
  - Gen 4+: "here we are again"

#### Zombie Rebirth
- Triggered when: `has_symbol()` is true AND burial depth < 30
- Character rises as a zombie (see §8)
- Narrative varies by how shallow the burial was

#### Soul-Claimed Rebirth
- Triggered when: devil contract (`soul_claimed()`) is active
- `return_for_devils_work()` activates
- Devil stalks player via `getdevil_target()`
- Exact additional mechanics TBD

### High Scores
- Top 10 scores tracked (lines 392–399)
- Name, initials, and generation stored
- Displayed on game over

---

## 11. Items and Inventory

### Item Categories
| Slot | Item | Find location | Find chance |
|------|------|--------------|-------------|
| `items[0]` | Spyglass | Marker 4 (ruins) | 2% |
| `items[1]` | Wooden stake | Any non-bog location | 30% |
| `items[2]` | Sword (×3) | Various | 60% |
| `items[3]` | Apple bomb (×30) | Various | 50% |
| `items[4]` | Bottle (×14) | Various | — |

### Item Use

**Spyglass** — LOOK command  
- Reveals distant NPCs, dragon, longhouse from current position  
- Disabled inside forest  

**Wooden Stake** — PLANT command  
- Cannot be planted in bog (marker 5)  
- Once planted: attracts and reveals zombies  
- Sense radius for detecting undead  

**Sword**  
- Adds damage bonus in combat  
- Blocks leader challenge (cannot be challenged while armed)  

**Apple Bomb**  
- Requires stained fingers (red dust from marker 2) to light fuse  
- Thrown at target; fuse burns before detonating  
- `status: partial` — "broken apples" TODO at line 5124  

**Bottle**  
- Contents: "beer" (morale boost), "pond water" (poison cure), "fount water" (leadership bonus), "empty"  
- Filled at pond (marker 6) or fount (marker 7)  
- Shared with allies during encounters  

### Ownership
- `getowner()` returns holder index or −1 (unowned / on ground)
- `gets_picked_up(i)` assigns to army member `i`
- `gets_dropped()` and `gets_lost()` return item to ground

---

## 12. Special Locations and Events

### Longhouse (Marker 0)
- Interior accessible when NPC or player enters from overworld
- Interior at `house_coord` (boardside+10)
- Gregor is the keeper; cooks meat when `meat_is_cooking` is set
- NPCs visit when morale < 30 or >40 turns since last visit
- Zombies are repelled by noise (cannot enter)
- **Planned:** "basement of the house!" (`status: stub`)

### Bog (Marker 5)
- Bodies sink gradually: "BLURP!" messages indicate descent
- Bodies auto-bury to depth 50 over time
- Ground meat location: ant trail → fissure detection chain
- Stake cannot be planted (too soft)

### Reddish Rocks / Sparks (Marker 2)
- Player can stain fingers with red dust (`stain_fingers()`)
- Stained fingers + rubbing → spark discovery (`notice_sparks()`)
- Sparks are the only way to light apple bomb fuses

### Pond (Marker 6) and Fount (Marker 7)
- Pond: cure for poison; fill bottle with antidote water
- Fount: leadership stat bonus on drinking; shareable via bottle

### Ruins (Marker 4)
- Spyglass spawn point
- Multi-item pickup location

### Cave Entrance (Marker 8)
- Fissure in rocks accessible after manipulation sequence
- Currently: cave grinds shut before player can enter
- **Planned full system (see §14)**

### Dragon
- Roams the board
- Encounter is always transformative or fatal
- Transit via `(−2, −2)`

### Gregor and Ardina
- Named NPCs with fixed narrative roles
- Their location is the subject of the "gregor" rumor type
- Gregor operates the longhouse; role in full game unclear beyond cooking

### Chanting
- `chantIsOn()` detects if a gang has become forest-obsessed
- Chanting gang members repeat "FOREST! FOREST!" each turn
- Leader must actively defy chant to redirect movement
- Can be disrupted via `chantFail()`

### Weather
- `weather` is either "sun" or "gloom"
- Affects NPC mood and dialog
- `weather_report[j]` tracks NPC exposure history

### Time Events
| Turnclock | Event |
|-----------|-------|
| 20 | First round of rumors starts |
| 40 | Second rumor wave |
| 50 | Third rumor wave |
| 75 | Fourth rumor wave |
| 90 | Skeleton spawning begins (`skeletons_continue`) |
| 80–120 | Apples begin to rot |
| 60+ | Bladders fill / burst |

---

## 13. Win / Loss Conditions

### Loss
- No formal loss state. The player always rebirts (see §10).
- QUIT command ends the session and displays high scores.

### Win
- **No implemented victory condition.**
- The game is currently open-ended: play continues until the player quits or chooses to stop.
- `play_on = false` terminates the main loop; reached only via QUIT or a hard crash.

### Planned Victory
- Stub references (lines 148–149): "demon gives you great powers if you complete your mission"
- Related to the Cave of Lives and the final boss encounter (see §14)
- Blessing system mentioned but not implemented

---

## 14. Incomplete and Planned Systems

### Cave of Lives (`status: partial`)
- Entrance at marker 8 (fissure in rocks)
- Current behavior: "the rock grinds shut before you can decide what to do" (deliberately sealed)
- `caveOfLives()` method exists (~line 20047) with substantial implementation:
  - Past-lives doors
  - Darkness and laughter effects
- **Planned but not implemented:** battle against all previous Rufuses, final battle against a boss (line 66)
- This is likely intended to be the victory condition

### Broken Apples (`status: partial`)
- Apple items decay and drop correctly
- TODO at line 5124: `// todo: restore broken apples.`
- The "broken" apple state transition is unfinished

### Spyglass in Forest (`status: planned`)
- Currently disabled with message: "Your spyglass is no good in the forest."
- Line 94 notes this as a planned feature
- Expected behavior: spyglass should work with reduced or different range

### Pets in Forest (`status: planned`)
- Pets do not follow master into the forest (lines 3271–3272)
- Pets without a master do not exit
- Lines 65 and 115 note this as pending work

### `delmeDrinkText()` (`status: stub`)
- Method body entirely commented out; never called (~line 23490)
- Either restore drinking-related narration or delete the method

### Forgiven NPCs (`status: partial`)
- TODO at line 23290: "forgiven NPCs should like you more"
- Forgiveness is acknowledged in game logic but friendship boost not applied

### Lars Account System (`status: stub`)
- Referenced in TODO: "if you have an account with lars then all the cave doors are locked"
- No `lars` NPC or account mechanic currently exists

### Basement of the Longhouse (`status: stub`)
- Referenced in TODO: "basement of the house!"
- No implementation

### Visitor from Pond (`status: stub`)
- Referenced in TODO list
- No implementation

### Curse System (`status: partial`)
- `is_cursed()` flag exists and is checked
- Exact curse effects and acquisition not fully wired
- TODO notes: "organize curse" 

### Purpose Conversations (`status: planned`)
- TODO at line 97: NPC-to-NPC conversations about purpose / motivation
- Currently NPCs only exchange rumors and greetings with each other

### Rumors About Forest Exits (`status: planned`)
- TODO at line 96: spread rumors about who successfully exited the forest
- No rumor type for forest exit currently exists

### Rumor of a Cur (`status: partial`)
- TODO at line 16710: "rumor of a cur should be cleared before and after main action loop"
- Stale cur-rumor state can persist incorrectly

### Deserter / NPC Gang Exit (`status: partial`)
- Dev notes flag "deserter problems"
- NPCs leaving gangs may leave incorrect relationship state

### High Score Calculation (`status: partial`)
- Dev notes flag a high score tracking anomaly
- Exact issue uninvestigated

---

## Known Behavior Anomalies (Uninvestigated)

From dev notes at the top of Gangland.java — these have been observed but not yet traced:

- Snake (non-red variant) incorrectly poisoning player
- "Another walking corpse joins" appearing during beast (snake) battles
- Dog grief scene triggering when dog is not at that location
- Too many "aiees" from the same NPC in a single encounter
- Zombie/beast battle pulling player in from a different location
- Getting stuck at specific turn counts (1189 noted)
- High score tracking issue (see §14)

---

*This document is derived from static analysis of Gangland.java (~24,172 lines). Line numbers are approximate and shift as the file evolves. Cross-reference with ISSUES.md for bug-tracking status.*
