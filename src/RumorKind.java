//The kinds of rumor that are born from something people see. Each kind keeps here what it says and how it spreads;
//each rumor itself (who has heard it, who knows who knows) is a Rumor, whose tag begins with its kind's tag.
//
//In the texts, <name> and <target> become the names of the people involved (as they were at the time), and <listener>
//the one being told. you_tell is printed when the player tells it; told is what anyone else says, aloud to the player
//(and what overheard mumbling picks its words from); gregor_tells, if not null, is Gregor's own version.
//
//To add a kind: add an entry here, and start it with Gangland.witnessRumorAt() where it happens.

public enum RumorKind {

	//the meat experiment worked: the rock at the steep rocks opened a crack. Replaces "meat helps you find the cave".
	CRACK("crack", false, false, false, "cave",
		"You chat with <listener>, and you end up telling him what you know of the crack in the steep rocks, which you assume is the long rumored cave entrance.",
		"You've probably heard about that cave, the one that could lead us out one day? Well someone found it, near the steep rocks- but the door's too heavy to open. Anyway, if you want to find the door, you just drop meat near it and wait.",
		null),

	DRAGON_SLAIN("dragonslain", true, false, false, null,
		"You chat with <listener>, and you end up mentioning to him that <name> mightily slew the dragon.",
		"Did you hear about that <name>- yep, he slew the dragon! It's true! Of course, there could always be another one.",
		null),

	//someone back from death by a deal with Lars, come after the gangfellow he deserted (<target>)
	LARS_RETURN("larsreturn", true, true, false, null,
		"You chat with <listener>, and you end up telling him the astonishing tale of <name>, who somehow came back from the dead and attacked his former friend, <target>.",
		"Did you hear about that <name>? He was killed surely, but then came back from the dead- not as a walking corpse, but fully in the pink! And it gets far weirder: he sought out his old comrade <target>, yet far from it being a happy reunion, <name> actually attacked him!",
		null),

	//the forest: the first one seen coming out alive is news; after that, only someone who went in alone, or who came out the
	//only one left of the gang he went in with
	FOREST_EXIT("forestexit", true, false, false, null,
		"You chat with <listener>, and you end up telling him that <name> was seen coming out of the forest- a rare survivor of that evil place.",
		"Did you hear- deadly though the forest be, that <name> went in and was seen to come back out alive- a neat trick!",
		null),

	FOREST_ALONE("forestalone", true, true, false, null,
		"[TEXT NEEDED: you tell <listener> that <name> went into the forest alone and came out alive]",
		"[TEXT NEEDED: the rumor that <name> went into the forest alone and came out alive]",
		null),

	FOREST_LAST("forestlast", true, true, false, null,
		"[TEXT NEEDED: you tell <listener> that <name>'s gang went into the forest and only he came out]",
		"[TEXT NEEDED: the rumor that <name>'s gang went into the forest and only he came out]",
		null),

	BLESSING("blessing", true, false, false, null,
		"You chat with <listener>, and you end up telling him that <name> buried the last of the cursed walking corpses, and was rewarded with some kind of special blessing.",
		"Did you hear about that <name>- he was digging graves when a radiant light fell on him, and it must have been some kind of blessing, for apparently he's even stronger now in battle.",
		null),

	//the first sight of the undead: told before any other rumor
	UNDEAD("undead", false, false, true, null,
		"You tell <listener> about the terrible curse of the walking corpses (just in case <listener> didn't know already).",
		"Oh it's terrible- the dead are walking among us! Mind you don't become one o them yourself! Oh, how can we make it through this?",
		null),

	//one fighter beat five or more, single-handed
	BIG_FIGHT("bigfight", true, true, false, null,
		"[TEXT NEEDED: you tell <listener> how <name> beat five or more single-handed]",
		"[TEXT NEEDED: the rumor that <name> beat five or more single-handed]",
		null);

	final String tag;
	//it's about someone (named in it, never told to him); it needs a witness besides him
	final boolean about_someone;
	//a separate rumor for each person (and each life), rather than one for the whole game
	final boolean per_person;
	final boolean spreads_first;
	//the tag of an older rumor that anyone who knows this one stops passing on, or null
	final String replaces;
	final String you_tell;
	final String told;
	final String gregor_tells;

	RumorKind(String tag, boolean about_someone, boolean per_person, boolean spreads_first, String replaces, String you_tell, String told, String gregor_tells) {
		this.tag = tag;
		this.about_someone = about_someone;
		this.per_person = per_person;
		this.spreads_first = spreads_first;
		this.replaces = replaces;
		this.you_tell = you_tell;
		this.told = told;
		this.gregor_tells = gregor_tells;
	}

	//the kind of a rumor, from its tag, or null for the older rumors (gregor, house, pig, forest, cave, kills)
	public static RumorKind of(String rumor_tag) {
		for(RumorKind k : values()) {
			if(rumor_tag.equals(k.tag) || rumor_tag.startsWith(k.tag + " ")) return k;
		}
		return null;
	}

	//the tag for a rumor of this kind about subject (one per person and life, where per_person)
	public String tagFor(int subject, int generation) {
		if(this.per_person) return this.tag + " " + subject + " " + generation;
		return this.tag;
	}

	public static String fill(String text, String name, String target, String listener) {
		return text.replace("<name>", name).replace("<target>", target).replace("<listener>", listener);
	}
}
