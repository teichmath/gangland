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
| `is_poisoned()` | Health drains each turn; cured at pond or by drinking antidote water |
| `poison_clock` | Countdown to death from poison |
| `is_blessed()` | Permanent buff granted when all undead are eliminated; grants poison immunity and a gang power-up (see §12) |
| `soul_claimed()` | Devil contract active; special rebirth path |
| `brains_hunger` | Zombie hunger meter |
| `hidden()` | Character not visible to others; NPCs and pets will not encounter or detect them, and they are excluded from gang battles at that location |

### Relationships
- `friend[i]` — opinion of army member `i`: −1 = unknown, 0–99 = known (see §4)
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

**Asking about Gregor (or Ardina):**
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
| `"gregor"` | Location of Gregor or Ardina |
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
| Rat (Wererat) | 12% | Two attacks (bite + scratch) |
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

### Known Gaps
- Pets do not follow master into the forest (`status: planned`, ~lines 3271–3272)
- Pets without a master do not exit the forest (`status: planned`, ~line 65)

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

### Recovering Humanity by Eating
A zombie can **fully recover** their humanity by eating enough brains (~lines 9589–9612, 23206–23218):
- Each meal raises `brains_hunger`; when it reaches ≥0 the zombie is satisfied
- `zombieRests()` is called, which immediately calls `army[zombie].end_zombie()`
- The character is no longer `is_zombie()`, no longer needs brains, and the text reads: "At last, your hunger for brains is satisfied. Your soul can rest."
- This is the **primary in-game escape from zombie status** — far more reliable than waiting for bog sinking, which only happens accidentally

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
1. **Eat enough brains** (primary path — see "Recovering Humanity" above): `brains_hunger` reaches ≥0 → `end_zombie()` is called → character is human again. The `has_symbol()` carving remains, so if they die again with a shallow burial they can rise as a zombie once more, but at least the current zombie run ends.
2. **Bog sinking** (accidental, permanent): if a zombie is buried to depth ≥50 in the bog, they disappear entirely and do not return. The chain ends for that character.

There is no ritual, item, or NPC interaction that removes the `has_symbol()` carving itself — that is permanent until death and deep burial.

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
- Requires: player is at marker 8 AND ≥2 pieces of meat are on the ground at that location
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

### Gregor and Ardina
- Named NPCs with fixed narrative roles
- Their location is the subject of the "gregor" rumor type
- Gregor operates the longhouse

### Weather
- `weather` is binary: "sun" or "gloom"
- **What sets it:** weather is "gloom" when `skeletons_continue` is true (undead ≥1 active after turnclock 90); otherwise "sun". It is not random or time-based.
- **When it appears in text:** during NPC encounters, if an NPC's `weather_report` counter exceeds 3 (incremented each turn), they make a weather comment and the counter resets (~lines 7489–7503). Dialog varies: "sun" → hopes it stays nice; "gloom" → hopes it clears; a cold/strange remark otherwise.
- **Mechanical effect:** none — weather changes only NPC dialog, not combat, movement, or any stat
- Clearing of all undead (Blessing) also clears the weather flag (~line 23152)

### Time Events
| Turnclock | Event |
|-----------|-------|
| 20 | First round of rumors starts |
| 40 | Second rumor wave |
| 50 | Third rumor wave |
| 75 | Fourth rumor wave |
| 90 | Skeleton spawning begins (`skeletons_continue`); weather → "gloom" |
| 80–120 | Apples begin to rot |
| 60+ | Bladders burst if carrying liquid |

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

### Cave of Lives (`status: partial`)
- Entrance at marker 8 (fissure in rocks; discovered via ant trail at bog)
- Current behavior: "the rock grinds shut before you can decide what to do" (deliberately sealed)
- `caveOfLives()` method exists (~line 20047) with substantial implementation:
  - Past-lives doors, darkness, laughter effects
- **Planned but not implemented:** battle against all previous Rufuses, final battle against a boss (~line 66)
- This is the most likely intended win condition for the game

### Broken Apples (`status: partial`)
- Apple items decay and drop correctly
- TODO at ~line 5124: `// todo: restore broken apples.`
- The "broken" apple state transition is unfinished

### Spyglass in Forest (`status: planned`)
- Currently disabled with message: "Your spyglass is no good in the forest."
- ~Line 94 notes this as a planned feature

### Pets in Forest (`status: planned`)
- Pets do not follow master into the forest (~lines 3271–3272)
- Pets without a master do not exit
- ~Lines 65 and 115 note this as pending work

### Tall Grass Cover (`status: stub`)
- Display text exists but no mechanical cover effect is implemented
- Expected behavior: grass should reduce visibility or attack accuracy

### Forgiven-NPC Friendship Boost (`status: planned`)
- The current forgiveness mechanic (see §4) normalizes a deserter's friendship by reducing it 50 points from an inflated value
- TODO at ~line 23290: a separate system where the player proactively forgives a hostile NPC and gains a friendship reward — not yet implemented

### `delmeDrinkText()` (`status: stub`)
- Method body entirely commented out; never called (~line 23490)
- Either restore drinking-related narration or delete the method

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

### Zombie Cure for `has_symbol()` (`status: planned`)
- A zombie run can be ended by eating enough brains (humanity restored) or by bog sinking (see §8)
- However, `has_symbol()` — the forehead carving — **cannot be removed**; the character is at risk of rising again as a zombie on their next death with a shallow burial
- A ritual or mechanic to permanently remove the carving should be designed and implemented

### Purpose Conversations (`status: planned`)
- TODO at ~line 97: NPC-to-NPC conversations about purpose / motivation
- Currently NPCs only exchange rumors and greetings with each other

### Rumors About Forest Exits (`status: planned`)
- TODO at ~line 96: spread rumors about who successfully exited the forest
- No rumor type for forest exit currently exists

### Rumor of a Cur (`status: partial`)
- TODO at ~line 16710: "rumor of a cur should be cleared before and after main action loop"
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
