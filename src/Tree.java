
public class Tree extends Beast {


	//constructor
	public Tree(Dice birth, int mirror) {
		this.name = "tree";
		this.name_plural = "trees";
		this.array_pos = mirror;
		this.sound_of_pain = "CRACK!";
		this.kill_sounds = new String[3];
		this.kill_sounds[0] = "\"AAAaaa .. * \"";
		this.kill_sounds[1] = " * CRUNCH * ";
		this.kill_sounds[2] = "\" glug ...\"";
		this.kill_sounds_for_dogs = new String[3];
		this.kill_sounds_for_dogs[0] = "\"rrrooorrrhhh....\"";
		this.kill_sounds_for_dogs[1] = " * CRUNCH * ";
		this.kill_sounds_for_dogs[2] = "\"whimper... wheeze...  \"";		
		this.kill_sounds_for_cats = new String[3];
		this.kill_sounds_for_cats[0] = "\"rowwrrreee....\"";
		this.kill_sounds_for_cats[1] = " * CRUNCH * ";
		this.kill_sounds_for_cats[2] = "\"meww...  meww... mewghkk...\"";
		this.singular_victory_message = "The tree's limbs slowly relax... there is a thud, the dead hitting the ground... the branches drift, slower and slower, to their former places.";
		this.plural_victory_message = singular_victory_message; 
		this.att_speed = new int[5];
		this.att_speed[0] = birth.throwdice(3,4);
		this.att_speed[1] = birth.throwdice(3,4);
		this.att_speed[2] = birth.throwdice(2,3);
		this.att_speed[3] = 0;
		this.att_speed[4] = 0;
		this.number_of_attacks = 3;
		this.strength = birth.throwdice(7,4);
		this.health = birth.throwdice(6,6) + 2;
		this.maxhealth = this.health;
		this.defense = 5;
		this.potential_wound = 0;
		this.strengthadj = 0;
		this.type_for_battle = "beast";
		if(this.strength >= 21) this.strengthadj = this.strength - 20;
		if(this.strength <= 11) this.strengthadj = this.strength - 12;
		this.skill_bonus_from_hit = 1;
		this.value = 20;
		this.run_chance = 0;
	}		

	//tree attacks
	public Attack attack_one(Being victim, String bname, int textlevel, Dice slash, String tree_name, boolean target_present) {
		int hitroll = slash.throwdice(1,20);
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[0];
		if(tohit <= 1) tohit = 2;
		int damage = 0;
		String dialogue = "";
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = slash.throwdice(2,5) + this.strengthadj;
			if(damage < 1) damage = 1;
			switch (textlevel) {
				case 0: break;
				case 1: 
					if(victim.gethealth() > 0) dialogue = ("\"Yarg!\"");
					else dialogue = "SLASH!";
					break;
				case 2:
				case 3:
					dialogue = ("A branch of "+tree_name+" slashes "+bname+" for "+damage+" points!");
					break;
				default:
					System.out.println("Wha?");
			}				
		}
		else {
			switch (textlevel) {
				case 0: break;
				case 1: 
					dialogue = ("SWISH!");
					break;
				case 2:
				case 3:
					if(victim.gethealth() > 0) dialogue = ("A branch of "+tree_name+" swings wildly at "+bname+", who ducks!");
					else dialogue = "Twigs and leaves fall on the body of "+bname+"...";
					break;
				default:
					System.out.println("Wha?");
			}
		} 
		return (new Attack(damage, dialogue));
	}
	
	public Attack attack_two(Being victim, String bname, int textlevel, Dice bash, String tree_name, boolean target_present) {
		int hitroll = bash.throwdice(1,20);
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[1];
		if(tohit <= 1) tohit = 2;
		int damage = 0;
		String dialogue = "";
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = bash.throwdice(1,3);
			switch (textlevel) {
				case 0: break;
				case 1: 
					dialogue = ("WHACK!");
					break;
				case 2:
				case 3:
					dialogue = ("A branch of "+tree_name+" knocks "+bname+" on the head for "+damage+" points!");
					break;
				default:
					System.out.println("Wha?");
			}				
		}
		else {
			switch (textlevel) {
				case 0: break;
				case 1:
					dialogue = ("SWOOSH!");
					break;
				case 2:
				case 3:
					if(victim.gethealth() > 0) dialogue = (capitalFirst(bname)+" jumps out of the way of a swinging branch!");
					else dialogue = "A swinging branch misses "+bname+", but it's too late for him to be spared...";
					break;
				default:
					System.out.println("Wha?");
			}
		} 
		return (new Attack(damage, dialogue));
	}

	public Attack attack_three(Being victim, String bname, int textlevel, Dice hug, String tree_name, boolean target_present) {
		int hitroll = hug.throwdice(1,20);
		if(victim.gethealth() > 0) hitroll = 20;
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[2];
		if(tohit <= 1) tohit = 2;
		int damage = 0;
		String dialogue = "";
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = hug.throwdice(3,5) + this.strengthadj;
			if(damage < 1) damage = 1;
			switch (textlevel) {
				case 0: break;
				case 1: 
					dialogue = "CRUSH   ";
					if(victim.gethealth() > 0) dialogue += "\"Aaaaaa!\"";
					break;
				case 2:
				case 3:
					dialogue = ("The limbs of "+tree_name+" hug "+bname+" in a crushing grip for "+damage+" points!");
					break;
				default:
					System.out.println("Wha?");
			}				
		}
		else {
			switch (textlevel) {
				case 0: break;
				case 1: 
					dialogue = ("GROAN");
					break;
				case 2:
				case 3:
					dialogue = (capitalFirst(bname)+" fights his way out of a tangle of branches!");
					break;
				default:
					System.out.println("Wha?");
			}
		} 
		if(!target_present) {
			damage = 0;
			dialogue = "  grrooaaan  ";
		} 
		return (new Attack(damage, dialogue));
	}
	

}

