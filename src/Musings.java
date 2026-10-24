//What gangfellows say aloud while they wait around together: wondering how they got here, what it all means.
//
//Each musing is a line, and either a reply written for it or null. A null reply is answered with a line from REPLIES, at random.
//Write them as spoken lines, quote marks included; the speaker's name goes in front when the player knows him.
//No musing comes up again until all of them have been heard. They're heard on the open board only, never in the forest.
//(Gangland.muse() picks them; the gangmates' friendly exchange in Encounter calls it, about one time in ten.)

public class Musings {

	static final String[][] MUSINGS = {
		{"\"So-called \'rebirth\'- I don't know if there's anything to that. When someone new comes along we always give 'em the same name as someone who just died, like they're in their next life. What if all that's rubbish?\"", "\"Howd'you mean, like there's nothing in common between any life and the next? Well each next person looks mostly like the one before him, but... you could be on to something.\""},
		{"\"Will we ever meet our past selves? If we did, would we get along?\"", "\"I doubt it.\""},
		{"\"I wonder about that Gregor- is he one of the prisoners or one of the jailers?\"", "\"Well I hate to think anything poor of Gregor, so- I won't think on that.\""},
		{"\"There's got to be a way out. Someone'll come along who can find it. Some day...\"", "\"It's a lovely thought. If you find the way, let me know. Elsewise I'll just make my living.\""},
		{"\"I think it's all a dream. It'll be okay- we'll all wake up and it'll be over.\"", null},
		{"\"We're being messed with, aren't we? There's someone really mean behind all of this, and they're just playing with us like we're toys.\"", null},
		{"\"It's all a prison, all of this, I'm sure of it- but why're we in any prison- what'd we all do?\"", null},
		{"\"I almost remember how it was before I ended up here- but then I try harder to remember even more, and then the memory just vanishes altogether. It's always just out of reach.\"", null},
		{"\"I never know whether to just accept the fact that I'm here or to commit to busting out.\"", null},
	};

	//replies for a musing that hasn't one of its own
	static final String[] REPLIES = {
		"\"Well, no one really knows, do they...\"",
		"\"Best to just carry on, eh?\"",
		"\"Don't worry too much about it.\"",
		"\"Best to not ask too many questions I find.\"",
	};
}
