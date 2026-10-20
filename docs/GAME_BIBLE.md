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
Nine named locations are anchored to specific grid coordinates.

| # | Name | Notable feature |
|---|------|-----------------|
| 0 | Longhouse | Stone building; Gregor's base; gang gathering point |
| 1 | Stand of evergreens | Dense trees |
| 2 | Cairn of reddish rocks | Red dust → sparks for apple bombs |
| 3 | Tall grass area | Cosmetic only — no mechanical cover effect (`status: stub`) |
| 4 | Ruins | Spyglass spawn |
| 5 | Bog | Bodies sink; stake cannot be planted |
| 6 | Fallen tree / pond | Insect encounters; water; poison antidote |
| 7 | Rocky slope / fount | Water; drinking teleports player back here (see §12) |
| 8 | Steep rocks / fissure | Cave entrance; ant trail discovery when meat is present |

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
| `personality` | 1–100 (rolled 1d100); shapes NPC dialog tone |
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
| `is_poisoned()` | Health drains each turn; cured at pond or by drinking antidote water |
| `poison_clock` | Countdown to death from poison |
| `is_blessed()` | Permanent buff granted when all undead are eliminated; grants poison immunity and a gang power-up (see §12) |
| `soul_claimed()` | Devil contract active; special rebirth path |
| `brains_hunger` | Zombie hunger meter |
| `hidden()` | Character not visible to others; NPCs and pets will not encounter or detect them, and they are excluded from gang battles at that location |

### Relationships
- `friend[i]` — opinion of army member `i`: −1 = unknown, 0–99 = known (see §4); 100 or more = grudge: set to 100 toward someone who attacks, deserts the gang, or runs out on a duel, and 200 toward battle runaways (`hateTheRunners`). A grudge unlocks the "Deserter!" greeting and forgiveness
- `perceived_pets[]` — pets the player is aware of
- `know_knows[]` — rumor knowledge tracking

### Food / Healing
- `amount_of_meat` — carried food; eating restores health
- Sources: longhouse kitchen (cut from Gregor's cooking meat), ground meat found on the board. Killing beasts does **not** drop meat.

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
- Player can join a gang (JOIN command) or lead an unaffiliated group

### Leadership Selection
The key leadership stat is `getevaluation()` — a composite score (weaponskill×40% + speed×30% + strength×20% + maxhealth×10%), not weaponskill alone.

**Initial / post-death selection — `leaderCaucus()` (~lines 17999–18186):**
- 2-member gang: the two duel (`leaderTwoMan()`)
- 3+ members: the member with the highest evaluation is proposed
  - If their evaluation is clearly dominant and health >5: they declare unopposed
  - If two members are close: the rest vote; any member with `getfriend(proposed) > 10` can object; one objection → duel between them
  - If all candidates have health ≤5: healthiest leads by default
- `aLeaderInEveryGang()` scans for leaderless gangs and calls `leaderCaucus()` for each — this runs after any leader death, so succession is automatic

**Leader challenges (~lines 7317–7332, 10358–10668):**
- Triggered when a member's `getevaluation()` exceeds the current leader's AND they share the same gang; can also be initiated manually via the CHALLENGE command
- Each challenger gets 3 attempts per opponent (`challenges_left_from_player`, ~line 10829)
- The challenge is **combat** (a duel via `leaderChallenge()`), not a dialogue roll

**Taking Umbrage (~lines 10414–10668):**
- Before a challenger reaches the leader, gang members who are friends with the leader (and not friends with the challenger) may step forward to defend
- Condition (~line 10429): `getfriend(challenger) > 10 && getfriend(leader) < 10`, OR `getfriend(leader) == 0`
- The leader asks the gang: *"What say you? Do you take umbrage to this challenge?"*
- Challenger must defeat each umbrage taker in sequential duels before fighting the leader; if exhausted by then, the challenge fails

### Gang Merging (~lines 8771–8970)
- Condition: average personality difference between the two gangs is ≤10 (~line 8853)
- Neither gang can have already proposed a merge to the other recently
- One gang proposes; the other accepts or rejects (player can participate)
- **New leader determination:** the two leaders duel; winner leads the merged gang. If one leader has health ≤5 the stronger assumes leadership by default; if both are weak, coin flip
- All members are reassigned to a new gang number (`highestgang++`)

### Civil War (~lines 12185–12589)
- Triggered when gang members with incompatible friendship/personality values meet
- Members choose sides based on friend scores: anyone whose friendship score places them closer to one side joins it
- The player is explicitly asked which side to join if present
- **Gang structure outcome:** the gang does **not** formally split. Losers have `affiliation` set to 0 (expelled, unaffiliated); survivors remain in the same gang. There is no mechanism for a losing faction to form a new gang.
- Third parties joining a civil war do not change their affiliation during the battle — they fight for a person, not a new gang

### Gang Desertion
The in-game Help text states: *"Once you join a gang, you can never desert it. If you do, and your old gangfellows find you, they are sworn to fight you (en masse) to the death."*

**What is implemented (~lines 6447–6485, 23070–23080, 18668–18913):**
- `desertAGang(user)` can be called and sets the player's `affiliation` to 0 (~line 23079)
- All former gang members have their `friend` value set to ~100 (sworn enemy threshold) on desertion (~line 23074)
- Former gang members actively hunt the deserter: when they encounter them, extensive "deserter quest" dialogue fires and combat begins (~lines 18723–18913)
- A `desertion_into_forest` flag handles the edge case where desertion happens while the gang is in the forest (~line 6447)

**Gaps vs. the help text:**
- The "en masse" framing overstates it slightly — gang members hunt individually rather than all gathering to attack simultaneously, though multiple members can participate
- The permanence is real in spirit: desertion cannot be undone and the enmity flag cannot be cleared through normal forgiveness mechanics

**Desertion tracking note:** the `deserter` state feeds into the forgiveness mechanic (see §4) — if the player later has friendship ≥100 with a former gangfellow, a one-time normalization fires that reduces the inflated friendship value.

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

---

## 4. Social System

### Greeting
- GREET triggers `Encounter()` for the named NPC
- On first meeting: names are learned
- Response dialog is determined by friendship value
- Player can greet: named NPC, "stranger", "dog", "cat", "undead"
- The dragon can be greeted; it wakes (if sleeping) and combat begins (see §12)

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
- 12 jokes stored in `jokes[][]` (~lines 4167–4214)
- **There is no timer-based release.** Jokes spread exclusively through NPC encounters: when an NPC tells a joke to another (`hears_joke()`, ~lines 7766–7878), both learn it. If the listener is in a gang, all gang members present also learn it.
- Telling a joke: boosts morale, improves friendship with the listener
- If the listener already knows the joke, the interaction still updates mutual-knowledge tracking but has no additional effect
- Each NPC tracks which jokes they know via `knows_joke()`

### Biography / Memory
- `bio_tome[armysize][500]` stores interaction records per NPC (~lines 330, 4247), indexed by generation
- Records are activated by events: encounters, battles, JOIN proposals, rejections, etc.
- On rebirth, biographies are degraded (~line 3775) — memory fades with each generation
- Player reads bio entries via REMEMBER command (~line 5982), which calls `readBio()` (~lines 20946–20990)
- **What the player sees:** a narrative of how they met the person, colored by recency ("just met", "met recently", "met"). Zombie players receive only very vague memory.
- Only entries where `bio_tome[subject][generation].is_activated()` is true are shown

### Forgiveness
- Forgiveness is a coded but narrow mechanic (~lines 8677–8683), not a general system
- It triggers when: the player encounters a deserter AND has friendship ≥100 with them AND the deserter is not their devil target
- Effect: the deserter's friendship value is **reduced by 50** (removing the excessive-loyalty penalty from the desertion event), and relief is noted in the encounter text
- This is not a reward — it normalizes the relationship. The TODO at line 23290 ("forgiven NPCs should like you more") refers to a separate, unimplemented mechanic where the player proactively forgives someone hostile

### Ground Writings
- Player can write a message on the ground (free-text prompt: "What do you want to write?")
- Writings are stored in `writings[][][]` (~line 330, 4246) at the current location
- They degrade randomly over time (~line 3559–3560)
- **Writings do not create rumors**, with one exception: if the text contains the word "gregor", a gregor-location rumor is triggered (~line 4648)
- **NPCs cannot write messages** — this is a player-only action

### Asking NPCs About Others
There is **no open-ended "ask [NPC] about [name]" system.** Only specific fixed topics can be queried (~lines 7239–7298, 8348–8401):

**Asking about Gregor:**
- Available as a dialogue option if the player knows the gregor rumor
- The NPC's response depends on their friendship level with the player:
  - Very friendly: gives a direction to Gregor's location
  - Moderately friendly: may share the location or admit ignorance
  - Unfriendly/hostile: dismissive or hostile response
- A successful exchange propagates the gregor rumor to the NPC if they didn't already know it (`rumorIsHeardBy()`, `rumorKnowsKnowsMutual()`)

**Asking about a cat or dog (by species, not by name):**
- If the NPC has had prolonged contact with a pet and knows its master, they can identify it (~lines 8499–8500)
- Otherwise responses range from "I don't know" to warnings if the pet is a zombie

**Other fixed rumor topics** (house, pig) can surface through NPC conversations but are not directly queryable by the player in the same way.

---

## 5. Rumor System

### Structure
- `rumors[]` array — 100 slots
- Each rumor has: type tag, originator, subject, truth flag, turn born

### Rumor Types
| Type | Content |
|------|---------|
| `"gregor"` | Location of Gregor |
| `"house"` | Location of the longhouse |
| `"pig"` | Pig sighting |
| `"forest"` | Something about the forest |
| `"cave"` | Something about the cave |
| `"kills"` | Reputation kill count for a named NPC |

### Propagation
- Rumors start via `startRumor()` at fixed `turnclock` milestones: 20, 40, 50, 75, 90
- NPCs exchange rumors when they encounter each other (~lines 7024–7071)
- `rumorIsHeardBy()`, `rumorIsKnownBy()`, `rumorKnowsKnows()` track knowledge
- A rumor about the player spreads if their reputation is noteworthy
- Writing the word "gregor" on the ground also starts a gregor rumor

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
- `battle_order[][][]` determines who acts when (~line 264)
- Ordered by `speed` stat
- Can flee via `runWithoutLeader()` (~line 241)

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
Two sources exist (~lines 15058–15061, 22731–22747):
- **Snake bite** — poison type "A" (~line 15060); applied by the non-red snake variant (**known anomaly** — red snakes should not poison but may)
- **Spider bite** — poison type "B" (~line 15058); spiders in the forest can also poison the player

Both apply 5d10 initial damage on poison application, or add 5d5 if the player is already poisoned. `poison_clock` counts down; at 0 the player dies.

Cures: drink from pond (marker 6), or drink antidote (pond water) from a bladder/bottle.

### Beasts — Overworld
Spawn on movement across the main board:

| Beast | Spawn chance | Notes |
|-------|-------------|-------|
| Rat | 12% | Two attacks (bite + scratch) |
| Pig | 6% | Killing a pig yields a bladder (see §11) |
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
- Roams the board; has a sleep/wake state
- **Greeting the dragon** wakes it (if asleep) and immediately starts combat — different flavor text from a direct attack but the same outcome
- **Outcomes:** player wins → dragon dies, player may extract teeth; player loses → sent to Limbo; player flees → standard flee mechanics
- **There is no transformation mechanic** — the dragon does not change the player's species or apply a curse. The word "transformative" in earlier descriptions was incorrect.
- Transit via `(−2, −2)`

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
- `hidden()` pets will not encounter others and are excluded from battles
- **Cats vs. beasts:** cats have a species-check code path when fighting beasts (~line 12846), but no explicit damage bonus against rats or any other specific creature was found. Neither cats nor dogs have confirmed special combat bonuses against particular enemy types.

### When a Master Dies — Pet Mourning
`petsMourn()` and `petMourns()` are both implemented (~lines 17914–17986):
- If the pet and master are at the same location and the player is present, mourning text is shown
- The pet transfers its loyalty to the master's slayer (`setfriend(slayer, ...)`, ~line 17934)
- The pet leaves the master and comes out of hiding (~lines 17935–17936)
- The player's morale is reduced by 20–40 depending on how long they knew the pet (~lines 17954–17986)
- **Not implemented:** graveyard visits, lasting morale debuffs, mourning ceremonies

### Pet Death
- Pet health tracked independently; pets can be killed in battle
- On death: `lose_perception_of_pet()` called, bond resets
- The player loses morale (see mourning above)

### Pets and the Forest
`status: implemented` (pet movement loop in `main()`, ~lines 3277–3460; `moveAlongForestCommuters()`)
- **Going in:** a pet on the forest entrance when its master steps in decides by loyalty alone: 8+ always follows, 4–7 follows with a loyalty×10% chance, 3 or less never does. The general follow rules above don't apply to this step. A pet that stays behind takes an ordinary step instead; if the master is you or in your gang, you see "*X stands back, solemnly watching you enter the forest.*"
- **Following later:** once the master is inside, the pet heads for the entrance under the normal follow rules, and each turn it stands on the entrance it gets the same loyalty test.
- **Inside:** a pet whose master is also inside follows the master anywhere in the forest, toward the square the master is on or moving to. It shares its master's forest map (`perceive_forest_anew`); a pet that got in without its master inside (e.g. running from a fight) gets its own (`enterForest`).
- **Coming out:** a pet in the forest whose master isn't (the master left, or died), or that has no master, is put back on the entrance square on its next move. In practice a pet leaves one turn after its master.
- The pet loop corrects each pet's forest flag from its position every turn (`checkAndCorrectInTheForest`), and checks the master by position (`inForestGrid`), since the flag lags after leaving.

---

## 8. Zombie System

### Becoming a Zombie
1. An NPC or the player has their forehead carved (`has_symbol()` = true)
2. On death, if burial depth < 30, `rise_to_seek_brains()` activates
3. Character is reborn as a zombie rather than a normal rebirth

### Zombie State
- Narration at resurrection varies by burial depth (~lines 3823–3832)
- `brains_hunger` starts negative (very hungry)
- Zombie cannot: GREET, TEACH, DRINK, GO INSIDE, BURY, LOOT

### Zombie Behavior
- Must find and EAT dead bodies (brains)
- `ground_meat` global tracks whether a meal is currently on the board
- Eating increments `brains_hunger` toward zero

### Laid to Rest by Eating
A zombie whose hunger for brains is satisfied is laid to rest: it dies for good, and does not become human again.
- Each meal raises `brains_hunger`; when it reaches ≥0 the zombie is satisfied
- `zombieRests()` is called, which calls `army[zombie].end_zombie()`. That clears the zombie flags and sets health to 0.
- For the player, the text reads: "At last, your hunger for brains is satisfied. Your soul can rest." The player then lies dead and is reborn as the next generation, as after any death. Bystanders see "The walking corpse exhales loudly and collapses to the ground. He is motionless."
- This is the **primary in-game way out of zombie status**. Bog sinking is the alternative, and it only happens by accident.

### Brain Squabble Rules
When multiple zombies compete for the same meal (`zombieBrainShare()`, ~lines 9305–9443):
1. **Even distribution phase:** Meals are divided round-robin starting from `first_diner`. Each zombie eats until satisfied or the meal runs out.
2. **Competition phase:** Any zombie still hungry after even sharing enters a brawl. Turn order is randomised by dice. Each turn, a zombie either:
   - Eats the meal they already have in hand, or
   - Grabs a victim (meal) that no other zombie is currently holding
3. Zombies can punch, kick, and shove each other during competition, dealing damage
4. A zombie exits the brawl once `brains_hunger >= 0`

### Skeleton Waves
- At `turnclock` 90: `skeletons_continue` flag enables skeleton spawns
- New zombies spawned from the locker at marker locations each turn
- At 20+ active zombies: special message fires; at 40+: escalated message

### Zombie Senses
- Stake (item, planted at non-bog locations) attracts and senses zombies
- `is_blessed()` player is immune to zombie-sense detection

### Escaping Zombie Status
Two paths exist:
1. **Eat enough brains** (primary path; see "Laid to Rest by Eating" above): `brains_hunger` reaches ≥0 → `end_zombie()` is called → the zombie dies and rests; a player is then reborn. `end_zombie()` also clears the `has_symbol()` carving (`zombie_potential`), and a reborn character always starts uncarved.
2. **Bog sinking** (accidental, permanent): if a zombie is buried to depth ≥50 in the bog, they disappear entirely and do not return. The chain ends for that character.

There is no ritual, item, or NPC interaction that removes the `has_symbol()` carving from a living character. It lasts for the rest of that life: a carved character who dies and is buried shallowly rises as a zombie. A deep burial prevents that, resting after eating clears the carving, and the next life starts without one.

---

## 9. The Forest

### Entry Requirements
- Player must be at `forest_entry_x, forest_entry_y` (adjacent to the board edge facing the forest)
- Direction command must match `forest_direction`
- `morale > 35` required
- `health > maxhealth − 3` required

### Forced Entry via Gang Chanting
A gang can build into a forest-obsessed chant that pressures the player toward the forest (~lines 3159–3189, 21575–21664):

**Conditions for chant to start:**
- Gang not already chanting, not currently in forest
- Last longhouse visit ≥40 turns ago
- `party_evaluation` ≥100
- Gang average morale >34
- Gang average `forest_entry_clock` >40

**What happens when chanting:**
- Gang members cry "FOREST! FOREST!" and refuse movement commands away from the forest entry point
- The player is pushed toward `forest_entry_x/y` coordinates each turn

**Can the player resist?**
Yes, but it takes two attempts (~lines 3165–3187):
1. First attempt to move away: gang members object; movement is blocked, dialogue fires
2. Second attempt (player sets `attempt_to_defy_forest_chant = true`): movement succeeds and `chantFail()` is called

**`chantFail()` (~lines 21667–21682):**
- Triggered by: player defying chant on second attempt; insufficient time since longhouse visit during chant attempt; a greeted gang member refusing to join the chant
- Effect: morale of all healthy gang members drops to 20; gang is removed from `chanting_gangs`; if user is present with >2 gang members, text reads "A hush falls over the gang..."

### Forest Layout
- 6×6 internal grid at coordinates `boardside+3` to `boardside+8`
- Player tracks perceived position separately from actual position
- Map builds up as exploration proceeds (~lines 4596–4619)
- Exit location known once discovered: `getforest_exit_perceived_x/y`

### Forest Encounters
Beast encounters fire on each move (see §6 forest table). No NPC encounters occur inside the forest.

### Navigating Out
- Player must reach the exit perceived coordinates
- Successful exit triggers celebration sequence (~lines 1070–1142)
- Gang celebration / chanting fires on exit (~lines 1115–1140)

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
- New player generated via constructor VI (~line 3748)
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
- The only escape from this chain is sinking into the bog (see §8)

#### Soul-Claimed Rebirth
- Triggered when: devil contract (`soul_claimed()`) is active
- `return_for_devils_work()` activates
- Devil stalks player via `getdevil_target()`
- Exact additional mechanics TBD

### High Scores
- Top 10 scores tracked (~lines 392–399)
- Name, initials, and generation stored
- Displayed on game over

---

## 11. Items and Inventory

### Item Categories (items[][] array)
| Slot | Item | Find location | Find chance |
|------|------|--------------|-------------|
| `items[0]` | Spyglass | Marker 4 (ruins) | 2% |
| `items[1]` | Wooden stake | Any non-bog location | 30% |
| `items[2]` | Sword (×3) | Various | 60% |
| `items[3]` | Apple bomb (×30) | Various | 50% |
| `items[4]` | Bottle (×14) | Various | — |

### Pig Bladders (separate from items[][] array)
- Bladders are tracked per-character via methods on Human/Pet: `getnumber_of_bladders()`, `fill_bladder(liquid)`, `lose_bladder(content)`, `pick_up_bladder(turnclock)`
- **Obtained by killing a pig** — after a pig battle, `getPigBladder()` retrieves one from the body (~lines 17373–17380)
- Used as liquid containers: can hold beer, pond water, or fount water
- Bladders burst after roughly 60 turns of holding liquid (~lines 3594–3625) — they are consumable unlike bottles
- Drinking from a filled bladder can cure poison (pond water) or raise morale (beer)

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
- A gnomish weapon: an apple with a fuse in it, made by the gnomes ("must be one of those crazy gnomish weapons")
- Where they turn up: each turn there's a 1-in-4 chance one appears on a random square of the forest (the forest is 6×6), and a 1-in-6 chance one appears at the forest entrance (`dropNewApple()`). At most 20 of the 30 apple-bomb slots are ever used, and no one has a carrying limit beyond that.
- Requires stained fingers (red dust from marker 2) to light fuse
- Thrown at target; fuse burns before detonating
- `status: partial` — "broken apples" TODO at ~line 5124

**Bottle**
- Contents: "beer" (sets morale to 40), "pond water" (poison cure), "fount water" (see fount entry in §12), "empty"
- Filled at pond (marker 6) or fount (marker 7)
- Shared with allies during encounters; unlike bladders, bottles do not burst

### Ownership
- `getowner()` returns holder index or −1 (unowned / on ground)
- `gets_picked_up(i)` assigns to army member `i`
- `gets_dropped()` and `gets_lost()` return item to ground

### Personal Effects (display-only, not pickupable)
When looting a generation-1 human body, `personalEffects()` (~line 22721) is called and displays one of 7 flavor items selected by `player_number`. These are **never added to inventory** — the method only calls `masterStringOut()` and has no pickup handler. They are relics from the modern world, implying the game's setting is post-collapse:

| Effect | Description |
|--------|-------------|
| Paper money | A crumpled "dollar bill" |
| Photo ID | A hard rectangle with a picture of the deceased on it |
| Receipt | A scrap of paper — a purchase receipt for an "energy drink" |
| Tiny black box | A sealed box with rune-like markings: one looks like an open lock, one a closed lock. "Meh, probably useless." |
| Shopping list | Hand-written: *vitamins  cereal  tpaste  bulb 60W  dep. check  send info to prof* |
| Love letter | *"I wish I hadn't said that before you had to leave..."* |
| Headphones | A bright red cord with a hard knob on each end |

None of these are connected to any other mechanic. The black box in particular has no relation to the devil, cave, or any quest system — it is pure atmosphere. Whether any of them should become interactable (the black box especially) is an open design question.

---

## 12. Special Locations and Events

### Longhouse (Marker 0)
- Interior accessible when NPC or player enters from overworld
- Interior at `house_coord` (boardside+10)
- Gregor is the keeper; `meat_is_cooking` flag set randomly (1-in-3 chance each turn unless 20+ undead are active)
- Players can cut meat from the cooking supply and carry it
- NPCs visit when morale < 30 or >40 turns since last visit
- Zombies are repelled by noise (cannot enter)
- **Graffiti wall:** The longhouse interior contains a wall (`gregors_wall`) where player names are scratched in (~lines 367, 4073, 18973, 19195–19198). When visiting, the player can add their name. This is purely flavor — the wall has no mechanical effect on gameplay.
- **Planned:** "basement of the house!" (`status: stub`)

### Tall Grass (Marker 3)
- Display text: "The grass is a little taller here"
- **No mechanical effect** — there is no cover, visibility, attack-roll, or movement modifier tied to this location
- `status: stub` — intended as cover but not implemented

### Bog (Marker 5)
- Bodies sink gradually: "BLURP!" messages indicate descent; auto-buried to depth 50 over time
- Stake cannot be planted (too soft)
- The bog has no ant trail — that mechanic is at marker 8 (see below)

### Reddish Rocks / Sparks (Marker 2)
- Player can stain fingers with red dust (`stain_fingers()`)
- Stained fingers + rubbing → spark discovery (`notice_sparks()`)
- **Sparks have unlimited uses** — `has_stained_fingers()` is a permanent flag with no counter. The real constraint is apple availability, not spark uses.

### Pond (Marker 6)
- Cure for poison: drink here or fill a bottle/bladder with pond water
- Fills containers for later use

### Fount (Marker 7)
- **Fount water does not grant a leadership or stat bonus**
- Drinking fount water **teleports the drinker back to the fount** (`waterWarp()`, ~lines 23018–23068) — even if they are elsewhere on the board
- Drinking at the fount itself just says "Very refreshing... but that's about it"
- This makes it useful as a repositioning tool, not a buff

### Ruins (Marker 4)
- Spyglass spawn point
- Multi-item pickup location

### Cave Entrance (Marker 8) — and the Ant Trail
The fissure and the ant trail are both here, not at the bog (~lines 5456–5495):

**Ant trail discovery:**
- Requires: player is at marker 8 AND meat the player dropped there has lain for ≥2 turns (`ground_meat` counts turns since the drop, not pieces; one piece is enough). The meat is wiped, and the ants and fissure forgotten, as soon as the player arrives anywhere, so the player must stay put while waiting
- Examining the meat reveals: "The meat on the ground is covered with ants" → `you_see_ants = true`
- From there, the player can follow the ants and discover the fissure → `you_see_fissure = true`

**Opening the fissure:**
- With fissure knowledge: reliable
- Without: 1-in-10 dice roll

**Relationship between marker 5 and marker 8:** their grid positions are placed independently — the bog does not need to be near the fissure. There is a related rumor that "meat helps you find the cave" (~line 22567), which refers to meat appearing at marker 8 triggering the ant trail, not meat at the bog.

**Cave interior:** currently grinds shut before the player can enter. See §14.

### Dragon
- Roams the board; has sleep/wake state
- Can be greeted (wakes it, then combat) or attacked directly — same combat outcome
- Player wins: dragon dies; teeth can be extracted if player has the right job (~lines 17360–17368)
- Player loses: sent to Limbo
- **No transformation, curse, or species-change mechanic exists in current code**

### Blessing
- Triggered when the total active undead count reaches 0 — i.e., the player (and allies) have eliminated all skeletons (~lines 23126–23191)
- Message count milestones: at 30 undead remaining, at 13, and at 0 (triggers Blessing)
- `Blessing(user)` is called on the player: skin glows, gang receives `zombie_slayer_power_up()`, morale and dialogue effects fire
- `is_blessed()` is **permanent for that generation** — it does not wear off
- Effects: immune to poison (~line 15109); gang power-up applied

### Gregor
- Named NPC with a fixed narrative role (Ardina was his counterpart in the female version of the game, now archived: see `docs/archive/female-version.md`)
- Their location is the subject of the "gregor" rumor type
- Gregor operates the longhouse

### Weather
- `weather` is binary: "sun" or "gloom"
- **What sets it:** weather is "gloom" when `skeletons_continue` is true (undead ≥1 active after turnclock 90); otherwise "sun". It is not random or time-based.
- **When it appears in text:** during NPC encounters, if an NPC's `weather_report` counter exceeds 3 (incremented each turn), they make a weather comment and the counter resets (~lines 7489–7503). Dialog varies: "sun" → hopes it stays nice; "gloom" → hopes it clears; a cold/strange remark otherwise.
- **Mechanical effect:** none — weather changes only NPC dialog, not combat, movement, or any stat
- Clearing of all undead (Blessing) also clears the weather flag (~line 23152)

### Time Events

**At a fixed turn of the game** (`turnclock`):

| Turn | Event |
|------|-------|
| 0 | The Gregor, house and pig-bladder rumors exist from the start (Gregor knows them) |
| 40 | The one-time "For the first time, you enter a duel... you have almost no idea of your own abilities, or your opponent's..." message is cancelled if the player hasn't fought a duel yet |
| 50 | The forest rumor starts |
| 75 | The cave rumor starts (Gregor is the only one who knows it at first) |
| 90 | Skeleton spawning begins (`skeletons_continue`); weather → "gloom" |

**After a certain age** (turns since the thing came into being):

| Age | Event |
|-----|-------|
| 30+ | An apple bomb lying on the ground, owned by nobody, rots |
| 80+ | An apple bomb carried by an NPC rots |
| 120+ | An apple bomb carried by the player rots ("An apple in your pocket has turned to rotten mush.") |
| 60+ | A bladder bursts if it has anything in it |

An apple bomb's age counts from when it turns up in the world (`dropNewApple()`), not from when it's picked up. A bladder's counts from when it was obtained.

---

## 13. Win / Loss Conditions

### Loss
- No formal loss state. The player always rebirths (see §10).
- QUIT command ends the session and displays high scores.

### Win
- **No implemented victory condition.**
- The game is currently open-ended: play continues until the player quits or chooses to stop.
- `play_on = false` terminates the main loop; reached only via QUIT or a hard crash.

### Partial Victory: Eliminating All Undead
- If all undead are destroyed, `Blessing` fires — this is the closest thing to an in-game victory milestone
- The player receives a permanent buff for the rest of that generation

### Planned Victory
- Stub references (~lines 148–149): "demon gives you great powers if you complete your mission"
- Related to the Cave of Lives and the final boss encounter (see §14)
- This remains the most likely intended win condition

---

## 14. Incomplete and Planned Systems

Unfinished systems, planned features and design ideas are tracked in `IDEAS.md`. The old dev-note anomalies were all traced and resolved; see "Known Behavior Anomalies" in `ISSUES.md`.

---

*This document is derived from static analysis of Gangland.java (~24,172 lines). Line numbers are approximate and shift as the file evolves. Cross-reference with ISSUES.md for bug-tracking status.*
