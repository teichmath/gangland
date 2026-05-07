
public class Rattishman extends Beast {


	//constructor
	public Rattishman(Dice birth, int mirror) {
		this.name = "rattish man";
		this.name_plural = "rattish men";
		this.array_pos = mirror;
		this.sound_of_pain = "SREEEEK!";
		this.kill_sounds = new String[3];
		this.kill_sounds[0] = "\"YAAaah .. * \"";
		this.kill_sounds[1] = " * THWACK *   \"uuuhhh...\"";
		this.kill_sounds[2] = "\" aiieee ...\"";
		this.kill_sounds_for_dogs = new String[3];
		this.kill_sounds_for_dogs[0] = "\"RRGAR BARK Brkgkh. . . *\"";
		this.kill_sounds_for_dogs[1] = " * THOK *  \"ROwrororrrr... \"";
		this.kill_sounds_for_dogs[2] = "\"whimper...  \"";		
		this.kill_sounds_for_cats = new String[3];
		this.kill_sounds_for_cats[0] = "\"REEROWR Rrrghkck. . . *\"";
		this.kill_sounds_for_cats[1] = " * WHACK * \"Rowreeee....\"";
		this.kill_sounds_for_cats[2] = "\"meww...  meep... glrrg..\"";
		this.singular_victory_message = "The rattish man leans down with his blade and takes a finger and a nose before leaving.";
		this.plural_victory_message = "The rattish men cut body parts for horrid trophies. It is a relief to see them go."; 
		this.type_for_battle = "beast";
		this.att_speed = new int[5];
		this.att_speed[0] = birth.throwdice(2,5);
		this.att_speed[1] = birth.throwdice(2,5);
		this.att_speed[2] = 0;
		this.att_speed[3] = 0;
		this.att_speed[4] = 0;
		this.number_of_attacks = 2;
		this.strength = birth.throwdice(7,3);
		this.health = birth.throwdice(3,8) + 10;
		this.maxhealth = this.health;
		this.defense = 14;
		this.potential_wound = 0;
		this.strengthadj = 0;
		if(this.strength >= 15) this.strengthadj = this.strength - 14;
		if(this.strength <= 9) this.strengthadj = this.strength - 9;
		this.skill_bonus_from_hit = 4;
		this.value = 30;
		this.run_chance = 3;
	}		

	//rattish man attacks
	public Attack attack_one(Being victim, String bname, int textlevel, Dice sword, String rman_name, boolean target_present) {
		int hitroll = sword.throwdice(1,20);
		if(victim.gethealth() <= 0) hitroll = 20;
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[0];
		if(tohit <= 1) tohit = 2;
		int damage = 0;
		String dialogue = "";
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = sword.throwdice(3,4) + this.strengthadj;
			if(damage < 1) damage = 1;
			switch (textlevel) {
				case 0: break;
				case 1: 
					if(victim.gethealth() > 0) {
						if(victim.getspecies().contains("cat")) dialogue = ("\"REERAOOWR!\"");
						if(victim.getspecies().contains("dog")) dialogue = ("\"YELP!\"");
						if(victim.getspecies().contains("human")) dialogue = ("\"Gaaarrr!\"");
					}
					else dialogue = "STAB";
					break;
				case 2:
				case 3:
					dialogue = (capitalFirst(rman_name)+" stabs "+bname+" for "+damage+" points!");
					break;
				default:
					System.out.println("Wha?");
			}				
		}
		else {
			switch (textlevel) {
				case 0: break;
				case 1: 
					dialogue = ("RREEEE!");
					break;
				case 2:
				case 3:
					dialogue = (capitalFirst(rman_name)+" thrusts his dagger at "+bname+", but misses!");
					break;
				default:
					System.out.println("Wha?");
			}
		}
		if(!target_present) {
			damage = 0;
			dialogue = "STAB";
		}  
		return (new Attack(damage, dialogue));
	}
	
	public Attack attack_two(Being victim, String bname, int textlevel, Dice hook, String rman_name, boolean target_present) {
		int hitroll = hook.throwdice(1,20);
		if(victim.gethealth() <= 0) hitroll = 20;
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[1];
		if(tohit <= 1) tohit = 2;
		int damage = 0;
		String dialogue = "";
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = hook.throwdice(2,3);
			switch (textlevel) {
				case 0: break;
				case 1: 
					if(victim.gethealth() > 0) {
						if(victim.getspecies().contains("cat")) dialogue = ("\"REEAAR!\"");
						if(victim.getspecies().contains("dog")) dialogue = ("\"BARK!\"");
						if(victim.getspecies().contains("human")) dialogue = ("\"Yeeow!\"");
					}
					else dialogue = "*RRRRIPPP*";
					break;
				case 2:
				case 3:
					dialogue = (capitalFirst(rman_name)+" slashes "+bname+" with a hook of sharpened bone for "+damage+" points!");
					break;
				default:
					System.out.println("Wha?");
			}				
		}
		else {
			switch (textlevel) {
				case 0: break;
				case 1:
					dialogue = ("SLASH!");
					break;
				case 2:
				case 3:
					dialogue = (capitalFirst(rman_name)+" misses "+bname+" with his bone hook!");
					break;
				default:
					System.out.println("Wha?");
			}
		} 
		if(!target_present) {
			damage = 0;
			dialogue = "SLASH!";
		}  
		return (new Attack(damage, dialogue));
	}
}

