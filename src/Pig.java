
public class Pig extends Beast {

	
	//constructor
	public Pig(Dice birth, int mirror) {
		this.name = "pig";
		this.name_plural = "pigs";
		this.array_pos = mirror;
		this.sound_of_pain = "WHEEEH!";
		this.kill_sounds = new String[3];
		this.kill_sounds[0] = "\"AIIEEEeeeee....\"";
		this.kill_sounds[1] = " * Whump * ";
		this.kill_sounds[2] = "\" gurgle ...\"";
		this.kill_sounds_for_dogs = new String[3];
		this.kill_sounds_for_dogs[0] = "\"rrrooorrrhhh....\"";
		this.kill_sounds_for_dogs[1] = " * WHUMP * ";
		this.kill_sounds_for_dogs[2] = "\"whimper...  \"";		
		this.kill_sounds_for_cats = new String[3];
		this.kill_sounds_for_cats[0] = "\"rowwrrreee....\"";
		this.kill_sounds_for_cats[1] = " * WHUMP * ";
		this.kill_sounds_for_cats[2] = "\"meww...  meep... glrrg..\"";
		this.singular_victory_message = "The swine devours the dead with ferocity, leaving bones and scattered bits. After a spell, she lumbers away.";
		this.plural_victory_message = "The swines jostle each other in devouring the fresh meat. In time, they lumber off, snorting with contentment.";
		this.type_for_battle = "beast";
		this.att_speed = new int[5];
		this.att_speed[0] = birth.throwdice(1,4);
		this.att_speed[1] = birth.throwdice(2,4);
		this.att_speed[2] = 0;
		this.att_speed[3] = 0;
		this.att_speed[4] = 0;
		this.number_of_attacks = 2;
		this.strength = birth.throwdice(4,4);
		this.health = birth.throwdice(2,6) + 2;
		this.maxhealth = this.health;
		this.defense = 4;
		this.potential_wound = 0;
		this.strengthadj = 0;
		if(this.strength >= 12) this.strengthadj = this.strength - 12;
		if(this.strength <= 6) this.strengthadj = this.strength - 7;
		this.skill_bonus_from_hit = 2;
		this.value = 15;
		this.run_chance = 1;
	}		

	//pig attacks
	public Attack attack_one(Being victim, String bname, int textlevel, Dice bite, String pig_name, boolean target_present) {
		int hitroll = bite.throwdice(1,20);
		if(victim.gethealth() <= 0) hitroll = 20;
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[0] + 3;
		if(tohit <= 1) tohit = 2;
		int damage = 0;
		String dialogue = "";
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = bite.throwdice(2,4) + this.strengthadj;
			if(damage < 1) damage = 1;
			switch (textlevel) {
				case 0: break;
				case 1: 
					dialogue = "CHOMP";
					break;
				case 2:
				case 3:
					dialogue = capitalFirst(pig_name)+" bites "+bname+" for "+damage+" points!";
					break;
				default:
					System.out.println("Wha?");
			}				
		}
		else {
			switch (textlevel) {
				case 0: break;
				case 1: 
					dialogue = "Whheeeee! Snort!";
					break;
				case 2:
				case 3:
					dialogue = capitalFirst(pig_name)+" lunges at "+bname+", but misses!";
					break;
				default:
					System.out.println("Wha?");
			}
		} 
		if(!target_present) {
			damage = 0;
			dialogue = "Whheeeee! Snort!";
		} 
		return (new Attack(damage, dialogue));
	}
	
	public Attack attack_two(Being victim, String bname, int textlevel, Dice kick, String pig_name, boolean target_present) {
		int hitroll = kick.throwdice(1,20);
		if(victim.gethealth() <= 0) hitroll = 20;
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[1] + 3;
		if(tohit <= 1) tohit = 2;
		int damage = 0;
		String dialogue = "";
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = kick.throwdice(1,5) + this.strengthadj;
			if(damage < 1) damage = 1;
			switch (textlevel) {
				case 0: break;
				case 1: 
					if(victim.gethealth() > 0) {
						if(victim.getspecies().contains("cat")) dialogue = "\"REEOOOR!\"";
						if(victim.getspecies().contains("dog")) dialogue = "\"ROOOF!\"";
						if(victim.getspecies().contains("human")) dialogue = "\"Ooof!\"";
					}
					else dialogue = "CRUNCH";
					break;
				case 2:
				case 3:
					dialogue = capitalFirst(pig_name)+" kicks "+bname+" for "+damage+" points!";
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
					dialogue = capitalFirst(pig_name)+" kicks at "+bname+", but misses!";
					break;
				default:
					System.out.println("Wha?");
			}
		} 
		if(!target_present) {
			damage = 0;
			dialogue = "CRUNCH";
		} 
		return (new Attack(damage, dialogue));
	}
}

