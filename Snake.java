
public class Snake extends Beast {


	//constructor
	public Snake(Dice birth, int mirror) {
		boolean redsnake = (birth.throwdice(1, 50) == 1) ? true : false;
		this.name = "snake";
		if(redsnake) this.name = "red snake";
		this.name_plural = "snakes";
		this.array_pos = mirror;
		this.sound_of_pain = "HSSSSSSSS!";
		this.kill_sounds = new String[3];
		this.kill_sounds[0] = "\"uuuhhhhh....\"";
		this.kill_sounds[1] = " * WHUMP * ";
		this.kill_sounds[2] = "\"@#$*...  snakes... \"";
		this.kill_sounds_for_dogs = new String[3];
		this.kill_sounds_for_dogs[0] = "\"rrrooorrrhhh....\"";
		this.kill_sounds_for_dogs[1] = " * WHUMP * ";
		this.kill_sounds_for_dogs[2] = "\"whimper...  \"";		
		this.kill_sounds_for_cats = new String[3];
		this.kill_sounds_for_cats[0] = "\"rowwrrreee....\"";
		this.kill_sounds_for_cats[1] = " * WHUMP * ";
		this.kill_sounds_for_cats[2] = "\"meww...  meep... glrrg..\"";
		this.singular_victory_message = "The snake slithers into the grass and is gone.";
		this.plural_victory_message = "The snakes shove off variously, wanting nothing more to do with this scene or with each other.";
		this.type_for_battle = "beast";
		this.att_speed = new int[5];
		this.att_speed[0] = birth.throwdice(4,4);
		this.att_speed[1] = 0;
		this.att_speed[2] = 0;
		this.att_speed[3] = 0;
		this.att_speed[4] = 0;
		this.number_of_attacks = 1;
		this.strength = birth.throwdice(2,3);
		this.health = birth.throwdice(2,5) + 2;
		this.maxhealth = this.health;
		this.defense = 4;
		this.potential_wound = 0;
		this.strengthadj = 0;
		this.skill_bonus_from_hit = 2;
		this.value = 20;
		this.run_chance = 1;
	}

	public String getspecies() { return "snake"; }

	//snake attacks
	public Attack attack_one(Being victim, String bname, int textlevel, Dice bite, String snake_name, boolean target_present) {
		int hitroll = bite.throwdice(1,20);
		if(victim.gethealth() <= 0) hitroll = 1;
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[0] + 3;
		if(tohit <= 1) tohit = 2;
		int damage = 0;
		String dialogue = "";
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = bite.throwdice(2,6) + 1;
			switch (textlevel) {
				case 0: break;
				case 1: 
					if(victim.gethealth() > 0) {
						if(victim.getspecies().contains("cat")) dialogue = ("\"REER!\"");
						if(victim.getspecies().contains("dog")) dialogue = ("\"YIP!\"");
						if(victim.getspecies().contains("human")) dialogue = ("\"Yarrg!\"");
					}
					break;
				case 2:
				case 3:
					dialogue = capitalFirst(snake_name)+" bites "+bname+" for "+damage+" points!";
					break;
				default:
					System.out.println("Wha?");
			}				
		}
		else {
			switch (textlevel) {
				case 0: break;
				case 1: 
					dialogue = ("HSssssss....");
					break;
				case 2:
				case 3:
					if(victim.gethealth() > 0) dialogue = (capitalFirst(snake_name)+" lunges at "+bname+", but misses!");
					else dialogue = capitalFirst(snake_name)+" slinks around the body of "+bname+"...";
					break;
				default:
					System.out.println("Wha?");
			}
		} 
		if(!target_present) {
			damage = 0;
			dialogue = "HSssssss....";
		} 
		return (new Attack(damage, dialogue));
	}
}
