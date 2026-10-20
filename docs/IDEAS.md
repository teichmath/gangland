# Gangland — Ideas & Lines of Development

The master list of ideas, planned changes, unfinished systems and open design decisions.

- `ISSUES.md` holds bugs: places where the game doesn't do what it's meant to, with a record of each fix.
- `GAME_BIBLE.md` describes how the game works today.
- Alex's older working notes are at the top of `src/Gangland.java`.

When a bug's fix needs a design call, it appears in both files: in ISSUES as the bug, and here as the decision.

Decisions about the game's text, look and feel are Alex's.

Status: `idea` (raised, not designed) · `decide` (needs a call before work) · `ready` (decided, can be built) · `partial` (some of it is in the game) · `done`

---

## Guiding principles

- **NPCs can do anything the player can.** Nothing the player can do, winning the game included, should be off limits to non-player characters. *(2026-10-19)*

---

## The cave, the meat and winning

- **NPCs try the meat experiment** — `idea`.
  - An NPC who has heard the cave rumor and carries meat sometimes drops it naively wherever they are, then waits around to see what happens. They have no idea where to try it.
  - If the spot happens to be the steep rocks, the ants can appear and the experiment can work: fissure found, rock opened.
  - If the player is present, there's a description and maybe dialogue from the NPC. *(Details not decided yet.)*
  - Groundwork: ground meat is one global counter, wiped whenever the player arrives anywhere. It needs to belong to a square, so meat stays put whether or not the player is there.
- **What an NPC's win looks like** — `decide` (later).
- **The Cave of Lives** — `partial`.
  - Today the fissure can be found and opened, but the rock always grinds shut.
  - `caveOfLives()` has a good deal of content (past-lives doors, darkness, laughter), but nothing calls it.
  - Planned: a battle against all previous Rufuses, then a final boss. Probably the game's win condition. *(Bible §12, Cave Entrance.)*
- **Lars accounts** — `idea`. From the old notes: "if you have an account with lars then all the cave doors are locked." There's no Lars account mechanic yet.

## Rumors, talk and the story

- **Rumors born from what people witness** — `idea`.
  - When the player or an NPC sees something unusual, it becomes a rumor that spreads, e.g. someone back from death by a deal with Lars attacking a former gangfellow, or someone slaying the dragon.
  - Includes the older planned "who made it out of the forest" rumor.
  - Today rumors come only from fixed starting points: Gregor, the house, the pig bladder, the forest and the cave, plus great fighters' kill counts.
- **Gangfellows speculate out loud** — `idea`.
  - A system of general remarks among gangfellows and NPCs: what's going on, what they're all doing here, what this place is. This absorbs the older "purpose conversations between NPCs" note.
  - The story told now in the Help menu's "Rebirth" page could come out this way instead, and that page could then go.
- **Jokes** — `idea`. When you suggest "joke" and don't know any, the other person is asked whether they know one and may tell it, or says "No, me neither." Someone who'd have turned you down anyway ("to the ash-heap…", "not in the mood") still just does that. The old notes also call for new jokes and a dialogue rewrite.
- **New gang-greeting exchange** — `ready` (Alex's wording). It replaces "We shall grow strong…" / "And fresh meat…" with:
  "We shall grow strong in the fight together, <name>!" / "We shall be feared high and low, wide and side, <name>!"
- **The "here's to us" toast** — `idea`. The current toast is borrowed; Alex will write an original.
- **Visitor from the pond** — `idea`. Named in the old notes only.

## Zombies, the undead and the symbol

- **Escape from the zombie state: the spark in the bog** — `idea`.
  - Once the red-rock dust is known (even from a past life), the player can snap their fingers for a spark at any time.
  - In the bog, the spark lights a small fire from the bog gas. A living player knows to stay clear, so nothing happens.
  - A zombie player who stays put smells burning after a couple of beats, then sees their feet have burned off unfelt, topples into the mud, and is reborn.
  - Prompted by getting stuck as a zombie in a world with 82 undead. Related: a zombie buried deep in the bog already sinks for good, but only by accident. Note: today the stained fingers and knowing about sparks (`knows_sparks`) reset with each new life, so "even from a past life" means carrying that knowledge over.
- **A way to remove the carved symbol** — `idea`. Once carved, a living character keeps the symbol for the rest of that life, so dying with a shallow burial means rising as a zombie. A ritual or other mechanic could remove it while they're alive. (Resting after eating clears it, and each new life starts uncarved.)
- **The living band together as the undead multiply** — `idea`. Past some number of undead, the living are less likely to fight, taunt and challenge each other (still possible, just rarer). Conversation doesn't get any friendlier.
- **A specific response when a zombie player attacks a cat** — `idea`. Today it's the generic "same awful state of hunger and decomposition" line.
- **The brain face-off** — `decide`. The player's Eat/Shove/Kick/Punch pick is currently ignored and replaced by a random roll (ISSUES S8). Fixing it needs a decision on what each choice does, e.g. whether "Eat" while someone else holds the brains means trying to snatch them.
- **"All is quiet on the battlefield"** — `decide`. It shows when an undead player eats and is laid to rest, although nobody died in the battle (ISSUES S9). Suppress it in that case? Replace it with something?

## Gangs

- **Explain abandonment the first time** — `idea`.
  - Today, every time a non-leader gang member picks a direction, the game asks "Are you sure you want to abandon the gang?" (or the leader's name), with no explanation.
  - The first time only, explain that walking off alone counts as abandoning the gang, then ask Y/N.
- **Leader challenge outcomes** — `decide`. A double death is silent, and the "too tired" and "stunned" outcomes can't happen (ISSUES C19).
- **Forgiven NPCs like you more** — `idea`. A way for the player to forgive a hostile NPC and gain friendship for it. (Today's forgiveness only takes a deserter's grudge back down.)
- **NPC gangs less likely to enter the forest** — `idea`. From the old notes.
- **Deserter state** — check. The old notes flag "deserter problems" when NPCs leave gangs. The bots now reach the deserter scenes often and haven't turned anything up, but nobody has looked closely.

## The world

- **Hiding in tall grass** — `partial`. Tall grass is described, but gives no cover. Idea: hide in it, e.g. from the dragon; it might also reduce visibility or attack accuracy.
- **A stash of apple bombs in the forest** — `idea`. Apple bombs are made by the gnomes. A stash of them somewhere in the forest can be discovered somehow, with a limit on how many each character can carry. Today single apple bombs turn up loose on random forest squares and at the forest entrance, and there's no carrying limit beyond the 20 in play.
- **The spyglass in the forest** — `idea`. It's switched off there today ("Your spyglass is no good in the forest.").
- **The longhouse basement** — `idea`. Named in the old notes only.
- **Writing in the bog** — `ready` (Alex's wording). It shouldn't work: "You start writing in the oozing mud, but it quickly globs back together, leaving no trace."
- **Remembering named pets after rebirth** — `idea`. On first seeing a dog or cat named in a past life: e.g. "A dog named Flappy is here- somehow you remember Flappy."
- **Broken apples** — `partial`. Apples decay and drop, but the "broken apple" state is unfinished (`//todo: restore broken apples.`).
- **The curse** — `partial`. The cursed flag exists and is checked, but what a curse does and how you get one aren't fully wired (old note: "organize curse").

## Features to retire

- **The biography/memory feature** — `decide`. Consider archiving it: the per-person interaction records and the REMEMBER command that reads them.
- **`delmeDrinkText()`** — `idea`. Its body is commented out and nothing calls it; restore it or delete it.

## Seeing who comes and goes

- **Strangers leaving** — `decide`. Only beings you know get a departure line. Strangers, stray cats included, leave silently. Cases that felt wrong in play:
  - a grieving cat that vanished right after its master's death;
  - a cat that rose as a zombie and walked off unseen.

  Announce strangers' departures, or only after something dramatic?

## Commands and the interface

- **Ambiguous targets** — `idea`. With two strangers here, "attack" lists them, as stranger (a) and stranger (b). The player can then answer `a`, `b`, `attack a` or `a a`. Today "Whom are you attacking?" takes only a name, so answering "attack stranger" fails.
- **Battle input hint** — `idea`. Instead of "2 are still fighting…", give a hint like "type attack a, attack b, a a, or a b".

## Code cleanup

- **"Rumor of a cur" state** — `idea`. Old note: it should be cleared before and after the main action loop; stale state may persist.

## Testing tools

- **Bot tweaks** — `idea`. Have bots forgive through Join, pick the Undead at Gregor's, and play longer gang games.
- **A "hunter" bot style** — `idea`. A bot that goes after the undead and the Blessing quest, which bots almost never reach today.
