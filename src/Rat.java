
public class Rat extends Beast {

	
	//constructor
	public Rat(Dice birth, int mirror) {
		this.name = "rat";
		this.name_plural = "rats";
		this.array_pos = mirror;
		this.sound_of_pain = "SQUEEAK!";
		this.kill_sounds = new String[3];
		this.kill_sounds[0] = "\"Please, no more...\"";
		this.kill_sounds[1] = "\"Aaarrrrg... \"";
		this.kill_sounds[2] = "\"RrrAAAaaaa... ugh.\"";
		this.kill_sounds_for_dogs = new String[3];
		this.kill_sounds_for_dogs[0] = "\"whine whine arf arf rrrooorrrhhh....\"";
		this.kill_sounds_for_dogs[1] = " * FLOP * ";
		this.kill_sounds_for_dogs[2] = "\"whimper...  \"";		
		this.kill_sounds_for_cats = new String[3];
		this.kill_sounds_for_cats[0] = "\"meer mee meew mew merowwrrreee....\"";
		this.kill_sounds_for_cats[1] = " * PLOP * ";
		this.kill_sounds_for_cats[2] = "\"meww...  meep... glrrg..\"";
		this.singular_victory_message = "The rat nibbles cold flesh for a while, and then scuttles out of view.";
		this.plural_victory_message = "The rats squeak in argument over the cold flesh... once sated, they scuttle out of view.";
		this.type_for_battle = "beast";
		this.att_speed = new int[5];
		this.att_speed[0] = birth.throwdice(1,6);
		this.att_speed[1] = birth.throwdice(2,5);
		this.att_speed[2] = 0;
		this.att_speed[3] = 0;
		this.att_speed[4] = 0;
		this.number_of_attacks = 2;
		this.strength = birth.throwdice(2,3);
		this.health = birth.throwdice(1,3) + 3;
		this.maxhealth = this.health;
		this.defense = 2;
		this.potential_wound = 0;
		this.strengthadj = 0;
		this.skill_bonus_from_hit = 1;
		this.value = 10;
		this.run_chance = 3;
	}		

	//rat attacks
	public Attack attack_one(Being victim, String bname, int textlevel, Dice bite, String rat_name, boolean target_present) {
		int hitroll = bite.throwdice(1,20);
		if(victim.gethealth() <= 0) hitroll = 20;
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[0] + 3;
		if(tohit <= 1) tohit = 2;
		int damage = 0;
		String dialogue = "";
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = bite.throwdice(1,4) + this.strengthadj;
			if(damage < 1) damage = 1;
			switch (textlevel) {
				case 0: break;
				case 1: 
					if(victim.gethealth() > 0) {
						if(victim.getspecies().contains("cat")) dialogue = ("\"REER!\"");
						if(victim.getspecies().contains("dog")) dialogue = ("\"YELP!\"");
						if(victim.getspecies().contains("human")) dialogue = ("\"Ouch!\"");
					}
					else dialogue = "nibble";
					break;
				case 2:
				case 3:
					dialogue = (capitalFirst(rat_name)+" bites "+bname+" for "+damage+" points!");
					break;
				default:
					System.out.println("Wha?");
			}				
		}
		else {
			switch (textlevel) {
				case 0:
				case 1: 
					break;
				case 2:
				case 3:
					dialogue = (capitalFirst(rat_name)+" chomps at "+bname+", but misses!");
					break;
				default:
					System.out.println("Wha?");
			}
		} 
		if(!target_present) {
			damage = 0;
			dialogue = "  bite";
		} 
		return (new Attack(damage, dialogue));
	}
	
	public Attack attack_two(Being victim, String bname, int textlevel, Dice scratch, String rat_name, boolean target_present) {
		int hitroll = scratch.throwdice(1,20);
		if(victim.gethealth() <= 0) hitroll = 20;
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[1] + 3;
		if(tohit <= 1) tohit = 2;
		int damage = 0;
		String dialogue = "";
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = scratch.throwdice(1,2) + this.strengthadj;
			if(damage < 1) damage = 1;
			switch (textlevel) {
				case 0: break;
				case 1: 
					dialogue = ("** scratch **");
					break;
				case 2:
				case 3:
					dialogue = (capitalFirst(rat_name)+"'s claws scratch "+bname+" for "+damage+" points!");
					break;
				default:
					System.out.println("Wha?");
			}				
		}
		else {
			switch (textlevel) {
				case 0:
				case 1: 
					break;
				case 2:
				case 3:
					dialogue = (capitalFirst(rat_name)+" swipes at "+bname+", but misses!");
					break;
				default:
					System.out.println("Wha?");
			}
		} 
		if(!target_present) {
			damage = 0;
			dialogue = "** swipe **";
		} 
		return (new Attack(damage, dialogue));
	}
}
