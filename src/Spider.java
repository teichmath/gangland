
public class Spider extends Beast {

	//constructor
	public Spider(Dice birth, int mirror) {
		this.name = "spider";
		this.name_plural = "spiders";
		this.array_pos = mirror;
		this.sound_of_pain = "SCCREEEEEE!";
		this.kill_sounds = new String[3];
		this.kill_sounds[0] = "\"UUUhhuhhhh....\"";
		this.kill_sounds[1] = "\"Aaieeeeeee....\"";
		this.kill_sounds[2] = "\"Aaaaarrrrg....\"";
		this.kill_sounds_for_dogs = new String[3];
		this.kill_sounds_for_dogs[0] = "\"rrrooorrrhhh....\"";
		this.kill_sounds_for_dogs[1] = "\"baarroooooorrr...\"";
		this.kill_sounds_for_dogs[2] = "\"whimper whimper whimper...  \"";		
		this.kill_sounds_for_cats = new String[3];
		this.kill_sounds_for_cats[0] = "\"rowwrrreeerrrr....\"";
		this.kill_sounds_for_cats[1] = "\"meeeerrrowreeerr...\"";
		this.kill_sounds_for_cats[2] = "\"meww...  meep... glrrg..\"";
		this.singular_victory_message = "The spider rolls its prey carefully in thick ropes of webbing, then drags off the lot for consumption elsewhere. ";
		this.plural_victory_message = "The spiders roll their prey carefully in thick ropes of webbing, then drag off the lot for consumption elsewhere. ";
		this.type_for_battle = "beast";
		this.att_speed = new int[5];
		this.att_speed[0] = birth.throwdice(2,6);
		this.att_speed[1] = 0;
		this.att_speed[2] = 0;
		this.att_speed[3] = 0;
		this.att_speed[4] = 0;
		this.number_of_attacks = 1;
		this.strength = birth.throwdice(3,3);
		this.health = birth.throwdice(3,4) + 4;
		this.maxhealth = this.health;
		this.defense = 10;
		this.potential_wound = 0;
		this.strengthadj = 0;
		this.skill_bonus_from_hit = 2;
		if(this.strength >= 8) this.strengthadj = this.strength - 7;
		if(this.strength <= 4) this.strengthadj = this.strength - 5;
		this.value = 25;
		this.run_chance = 0;
	}		

	//spider attack
	public Attack attack_one(Being victim, String bname, int textlevel, Dice bite, String spider_name, boolean target_present) {
		int hitroll = bite.throwdice(1,20);
		if(victim.gethealth() <= 0) hitroll = 1;
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[0];		
		if(tohit <= 1) tohit = 2;
		int damage = 0;
		String dialogue = "";
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = bite.throwdice(2,4) + 3;
			if(damage < 1) damage = 1;
			switch (textlevel) {
				case 0: break;
				case 1: 
					dialogue = ("ssss... \"Ouch!\"");
					break;
				case 2:
				case 3:
					dialogue = (capitalFirst(spider_name)+" bites "+bname+" for "+damage+" points!");
					break;
				default:
					System.out.println("Wha?");
			}				
		}
		else {
			switch (textlevel) {
				case 0: break;
				case 1: 
					if(victim.gethealth() > 0) dialogue = ("\"Back!\"");
					else dialogue = ("ssss....   ");
					break;
				case 2:
				case 3:
					if(victim.gethealth() > 0) dialogue = (capitalFirst(spider_name)+" strikes at "+bname+", but misses!");
					else dialogue = capitalFirst(spider_name)+" prods the body of "+bname+" hungrily!";
					break;
				default:
					System.out.println("Wha?");
			}
		} 
		if(!target_present) {
			damage = 0;
			dialogue = " >click< ";
		} 
		return (new Attack(damage, dialogue));
	}
}
	
	

