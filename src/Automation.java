import java.util.*;

// Machine-readable state dump and invariant checks, printed before each input prompt
// when GANGLAND_AUTOMATION=1. Everything here is read-only: it must never change game
// state or roll dice, or seeded runs would stop replaying identically.
public class Automation {

	// Each (rule, being) violation is reported once per process, so a lasting bad
	// state doesn't flood the log at every prompt.
	static final Set<String> reported = new HashSet<String>();

	// Pet index -> turn its master was first seen dead while still bound to it.
	static final Map<Integer, Integer> masterDeadSince = new HashMap<Integer, Integer>();

	static void beforePrompt(Gangland g) {
		try {
			if(g.army == null || g.user < 0 || g.user >= g.army.length || g.army[g.user] == null) return;
			System.out.println("<<STATE " + state(g) + ">>");
			checkInvariants(g);
		}
		catch (Exception e) {
			System.out.println("<<AUTOMATION_ERROR " + e + ">>");
		}
	}

	// ---------------------------------------------------------------- state

	static String state(Gangland g) {
		Being u = g.army[g.user];
		StringBuilder sb = new StringBuilder("{");
		sb.append("\"turn\":").append(g.turnclock);
		sb.append(",\"third\":").append(g.third_of_turn);

		sb.append(",\"player\":{");
		sb.append("\"name\":").append(str(u.getname()));
		sb.append(",\"x\":").append(u.getx()).append(",\"y\":").append(u.gety());
		sb.append(",\"where\":").append(str(where(g, u.getx(), u.gety())));
		sb.append(",\"health\":").append(u.gethealth());
		sb.append(",\"maxhealth\":").append(u.getmaxhealth());
		sb.append(",\"strength\":").append(u.getstrength());
		sb.append(",\"speed\":").append(u.getspeed());
		sb.append(",\"weaponskill\":").append(u.getweaponskill());
		sb.append(",\"morale\":").append(u.getmorale());
		sb.append(",\"gang\":").append(u.getaffiliation());
		sb.append(",\"leader\":").append(u.is_he_leader());
		sb.append(",\"generation\":").append(u.getgeneration());
		sb.append(",\"age\":").append(u.getage());
		sb.append(",\"kills\":").append(u.getkills());
		sb.append(",\"meat\":").append(u.getamount_of_meat());
		sb.append(",\"zombie\":").append(u.is_zombie());
		sb.append(",\"poisoned\":").append(u.is_poisoned());
		sb.append(",\"symbol\":").append(u.has_symbol());
		sb.append(",\"cursed\":").append(u.is_cursed());
		sb.append(",\"blessed\":").append(u.is_blessed());
		// living people the player holds a grudge against (opinion 100+: deserted the player's gang, or worse)
		sb.append(",\"grudges\":[");
		boolean firstGrudge = true;
		for(int i = 0; i < g.humansize; i++) {
			if(i == g.user || g.army[i] == null || g.army[i].gethealth() <= 0 || u.getfriend(i) < 100) continue;
			if(!firstGrudge) sb.append(",");
			sb.append(str(g.army[i].getname()));
			firstGrudge = false;
		}
		sb.append("]");
		sb.append("}");

		// Items the player carries, as name -> count.
		Map<String, Integer> carried = new TreeMap<String, Integer>();
		if(g.items != null) {
			for(Tool[] kind : g.items) {
				if(kind == null) continue;
				for(Tool t : kind) {
					if(t != null && t.getowner() == g.user) carried.merge(t.getname(), 1, Integer::sum);
				}
			}
		}
		sb.append(",\"items\":{");
		boolean first = true;
		for(Map.Entry<String, Integer> e : carried.entrySet()) {
			if(!first) sb.append(",");
			sb.append(str(e.getKey())).append(":").append(e.getValue());
			first = false;
		}
		sb.append("}");

		// Everyone else alive at the player's location (including hidden ones, flagged). Off-board spots such as the
		// in-transit (0,0) are shared with unused beast slots, so nobody is "here" while the player is off the map.
		sb.append(",\"here\":[");
		first = true;
		boolean placed = !where(g, u.getx(), u.gety()).equals("offboard");
		int alive = 0, dead = 0;
		for(int i = 0; i < g.army.length; i++) {
			Being b = g.army[i];
			if(b == null) continue;
			if(b.gethealth() > 0) alive++; else dead++;
			if(i == g.user || b.gethealth() <= 0) continue;
			if(!placed || b.getx() != u.getx() || b.gety() != u.gety()) continue;
			if(!first) sb.append(",");
			sb.append("{\"id\":").append(i);
			sb.append(",\"name\":").append(str(b.getname()));
			sb.append(",\"species\":").append(str(b.getspecies()));
			sb.append(",\"health\":").append(b.gethealth());
			sb.append(",\"gang\":").append(b.getaffiliation());
			sb.append(",\"zombie\":").append(b.is_zombie());
			sb.append(",\"hidden\":").append(b.hidden());
			sb.append("}");
			first = false;
		}
		sb.append("]");

		sb.append(",\"world\":{\"alive\":").append(alive);
		sb.append(",\"dead\":").append(dead);
		sb.append(",\"gangs\":").append(g.highestgang);
		sb.append(",\"graves\":").append(g.next_grave);
		sb.append(",\"dragon_alive\":").append(g.dragon_alive);
		// active undead, as Gangland.countUndead("active") counts them (that method can print, so it isn't called here)
		int undead = 0;
		for(int i = 0; i < g.armysize; i++) {
			if(g.army[i] != null && g.army[i].is_zombie() && g.army[i].how_buried() > 0) undead++;
		}
		sb.append(",\"undead\":").append(undead);
		// NPCs waiting on a meat experiment, and pieces of meat on the ground
		int experimenting = 0;
		if(g.meat_experiment_turn != null) for(int t : g.meat_experiment_turn) if(t > -1) experimenting++;
		sb.append(",\"meat_experiments\":").append(experimenting);
		sb.append(",\"ground_meat\":").append(g.ground_meat == null ? 0 : g.ground_meat.size());
		sb.append("}}");
		return sb.toString();
	}

	// Names the coordinate regions listed under "Off-Board Coordinates" in the game bible.
	static String where(Gangland g, int x, int y) {
		int bs = g.boardside;
		if(x >= 1 && x <= bs && y >= 1 && y <= bs) return "board";
		if(x == -5 && y == -6) return "offboard";  // hateTheRunners marks handled runaways this way
		if(x != y && !(inForest(bs, x) && inForest(bs, y))) return "mixed";
		if(x == bs + 2) return "limbo";
		if(inForest(bs, x)) return "forest";
		if(x == bs + 9) return "cave";
		if(x == g.house_coord) return "house";
		if(x == -11 || x == -10 || x == -5 || x == -2 || x == -1 || x == 0) return "offboard";
		return "unknown";
	}

	static boolean inForest(int bs, int c) {
		return c >= bs + 3 && c <= bs + 8;
	}

	// ----------------------------------------------------------- invariants

	static void checkInvariants(Gangland g) {
		Being[] army = g.army;
		Map<String, Integer> firstWithName = new HashMap<String, Integer>();

		for(int i = 0; i < army.length; i++) {
			Being b = army[i];
			if(b == null) continue;
			boolean alive = b.gethealth() > 0;

			if(alive && b.gethealth() > b.getmaxhealth())
				report("health_over_max", i, b, b.gethealth() + " > " + b.getmaxhealth());

			if(alive && b.getmaxhealth() <= 0)
				report("maxhealth_nonpositive", i, b, "maxhealth " + b.getmaxhealth());

			// Slots from armysize up are temporary battle beasts with leftover positions.
			if(alive && i < g.armysize) {
				String w = where(g, b.getx(), b.gety());
				if(w.equals("mixed") || w.equals("unknown"))
					report("bad_position", i, b, "(" + b.getx() + "," + b.gety() + ") " + w);
			}

			// getNumberFromName returns the first match among all named beings, dead or
			// alive, so a shared name silently points rumors/pets at the wrong person.
			if(b.has_name() && b.getname() != null) {
				String key = b.getname().toLowerCase();
				Integer other = firstWithName.get(key);
				if(other == null) firstWithName.put(key, i);
				else report("duplicate_name", i, b, "same name as #" + other);
			}

			if(b instanceof Player) {
				// Rolled as 1d100.
				if(b.getpersonality() < 1 || b.getpersonality() > 100)
					report("personality_out_of_range", i, b, "personality " + b.getpersonality());

				if(b.getaffiliation() < 0 || b.getaffiliation() > g.highestgang)
					report("gang_out_of_range", i, b, "gang " + b.getaffiliation() + ", highestgang " + g.highestgang);

				// -1 = never met, 0-99 = opinion, 100+ = grudge (100, or 200 for deserters).
				for(int j = 0; j < army.length; j++) {
					int f = b.getfriend(j);
					if(f < -1 || f > 200) {
						report("friend_out_of_range", i, b, "opinion of #" + j + " is " + f);
						break;
					}
				}
			}

			if(b instanceof Pet) {
				int m = b.getmasters_number();
				if(m < -1 || m >= army.length)
					report("pet_master_invalid", i, b, "master #" + m);
				// petsMourn() releases pets of dead masters once per turn, so only flag it
				// when a pet is still bound to a dead master two turns later.
				else if(m >= 0 && army[m] != null && army[m].gethealth() <= 0 && alive) {
					Integer since = masterDeadSince.get(i);
					if(since == null) masterDeadSince.put(i, g.turnclock);
					else if(g.turnclock - since >= 2)
						report("pet_master_dead", i, b, "master #" + m + " " + army[m].getname() + " dead since turn " + since);
				}
				else masterDeadSince.remove(i);

				if(b.getloyalty() < 0 || b.getloyalty() > 10)
					report("pet_loyalty_out_of_range", i, b, "loyalty " + b.getloyalty());
			}
		}

		if(g.items != null) {
			for(Tool[] kind : g.items) {
				if(kind == null) continue;
				for(int t = 0; t < kind.length; t++) {
					Tool tool = kind[t];
					if(tool == null) continue;
					int o = tool.getowner();
					if(o < -1 || o >= army.length)
						reportTool("tool_owner_invalid", tool, t, "owner #" + o);
				}
			}
		}
	}

	// ------------------------------------------------- event checks (called from game code)

	// At the start of every battle, each human or pet fighter should be on the battle's square.
	// (Beasts are placed with setxy, which leaves them in transit, so they're skipped.)
	static void checkBattleStart(Gangland g) {
		try {
			for(int row = 0; row < 2; row++) {
				for(int col = 0; col < g.number_of_columns; col++) {
					int f = g.war[row][col];
					if(f < 0 || f >= g.armysize || g.out_of_the_fight[row][col]) continue;
					Being b = g.army[f];
					if(!b.ishere(g.battle_x, g.battle_y))
						reportEvent("fighter_not_at_battle", "being=" + f + " name=" + b.getname() + (f == g.user ? " (you)" : "")
							+ " at (" + b.getx() + "," + b.gety() + "), battle at (" + g.battle_x + "," + g.battle_y + ")"
							+ (g.they_fight_beasts ? " vs " + g.beast_type : ""));
				}
			}
		}
		catch (Exception e) {
			System.out.println("<<AUTOMATION_ERROR " + e + ">>");
		}
	}

	// A scene the player watches (e.g. a pet grieving over its master) needs the pet, the player and the
	// scene really on one valid square: not two beings that merely share the in-transit (0,0), and not a hidden pet.
	static void checkWitnessed(Gangland g, String rule, int pet, int other) {
		Being p = g.army[pet], u = g.army[g.user], o = g.army[other];
		String w = where(g, p.getx(), p.gety());
		//a hiding pet may come out to grieve (petMourns() brings it out of hiding), so hidden is allowed there
		boolean ok = p.ishere(u.getx(), u.gety()) && (w.equals("board") || w.equals("forest")) && (!p.hidden() || rule.equals("pet_grief_not_there"));
		if(!ok)
			reportEvent(rule, "pet=" + pet + " " + p.getname() + " at (" + p.getx() + "," + p.gety() + ") " + w + (p.hidden() ? " hidden" : "")
				+ ", you at (" + u.getx() + "," + u.gety() + "), #" + other + " " + o.getname() + " at (" + o.getx() + "," + o.gety() + ")");
	}

	// Messages that shouldn't appear in a given situation.
	static void checkText(Gangland g, String text) {
		if(text == null) return;
		if(g.they_fight_beasts && (text.contains("walking corpse joins") || text.contains("walking corpses wander") || text.contains("corpses are swarming")))
			reportEvent("corpse_join_in_beast_fight", "\"" + text.trim() + "\" during a fight with " + g.beast_type);
	}

	// Event reports aren't deduplicated: each one is a separate occurrence.
	static void reportEvent(String rule, String detail) {
		System.out.println("<<INVARIANT " + rule + " | " + detail + ">>");
	}

	static void report(String rule, int i, Being b, String detail) {
		if(!reported.add(rule + "#" + i)) return;
		System.out.println("<<INVARIANT " + rule + " being=" + i + " name=" + b.getname() + " | " + detail + ">>");
	}

	static void reportTool(String rule, Tool t, int index, String detail) {
		if(!reported.add(rule + "#" + t.getname() + index)) return;
		System.out.println("<<INVARIANT " + rule + " tool=" + t.getname() + "[" + index + "] | " + detail + ">>");
	}

	static String str(String s) {
		if(s == null) return "null";
		StringBuilder sb = new StringBuilder("\"");
		for(char c : s.toCharArray()) {
			if(c == '"' || c == '\\') sb.append('\\').append(c);
			else if(c < 0x20) sb.append(String.format("\\u%04x", (int) c));
			else sb.append(c);
		}
		return sb.append('"').toString();
	}
}
