
public class Skeleton extends Beast {


	//constructor
	public Skeleton(Dice birth, int mirror) {
		this.name = "skeleton";
		this.name_plural = "skeletons";
		this.array_pos = mirror;
		this.sound_of_pain = "CRACK!";
		this.kill_sounds = new String[3];
		this.kill_sounds[0] = "\"AAaah .. * \"";
		this.kill_sounds[1] = " * CRUNCH * ";
		this.kill_sounds[2] = "\" glug ...\"";
		this.kill_sounds_for_dogs = new String[3];
		this.kill_sounds_for_dogs[0] = "\"rrrooorrrhhh....\"";
		this.kill_sounds_for_dogs[1] = " * CRUNCH * ";
		this.kill_sounds_for_dogs[2] = "\"whimper...  \"";		
		this.kill_sounds_for_cats = new String[3];
		this.kill_sounds_for_cats[0] = "\"rowwrrreee....\"";
		this.kill_sounds_for_cats[1] = " * CRUNCH * ";
		this.kill_sounds_for_cats[2] = "\"meww...  meep... glrrg..\"";
		this.singular_victory_message = "The skeleton carves a symbol in the flesh of the dead, then silently marches away.";
		this.plural_victory_message = "The skeletons carve symbols in the flesh of the dead, then silently march away."; 
		this.type_for_battle = "beast";
		this.att_speed = new int[5];
		this.att_speed[0] = birth.throwdice(2,4);
		this.att_speed[1] = birth.throwdice(2,4);
		this.att_speed[2] = 0;
		this.att_speed[3] = 0;
		this.att_speed[4] = 0;
		this.number_of_attacks = 2;
		this.strength = birth.throwdice(7,3);
		this.health = birth.throwdice(3,6) + 2;
		this.maxhealth = this.health;
		this.defense = 12;
		this.potential_wound = 0;
		this.strengthadj = 0;
		if(this.strength >= 15) this.strengthadj = this.strength - 14;
		if(this.strength <= 9) this.strengthadj = this.strength - 9;
		this.skill_bonus_from_hit = 3;
		this.value = 30;
		this.run_chance = 0;
	}		

	//skeleton attacks
	public Attack attack_one(Being victim, String bname, int textlevel, Dice sword, String skeleton_name, boolean target_present) {
		int hitroll = sword.throwdice(1,20);
		if(victim.gethealth() <= 0) hitroll = 20;
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[0];
		if(tohit <= 1) tohit = 2;
		int damage = 0;
		String dialogue = "";
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = sword.throwdice(2,5) + this.strengthadj;
			if(damage < 1) damage = 1;
			switch (textlevel) {
				case 0: break;
				case 1: 
					if(victim.gethealth() > 0) {
						if(victim.getspecies().contains("cat")) dialogue = ("\"REERAAOWR!\"");
						if(victim.getspecies().contains("dog")) dialogue = ("\"YELP!\"");
						if(victim.getspecies().contains("human")) dialogue = ("\"Graaar!\"");
					}
					else dialogue = "SLASH";
					break;
				case 2:
				case 3:
					dialogue = (capitalFirst(skeleton_name)+" slashes "+bname+" for "+damage+" points!");
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
					dialogue = (capitalFirst(skeleton_name)+" swings his scabard at "+bname+", but misses!");
					break;
				default:
					System.out.println("Wha?");
			}
		} 
		if(!target_present) {
			damage = 0;
			dialogue = "SWING";
		} 
		return (new Attack(damage, dialogue));
	}
	
	public Attack attack_two(Being victim, String bname, int textlevel, Dice bone, String skeleton_name, boolean target_present) {
		int hitroll = bone.throwdice(1,20);
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[1];
		if(tohit <= 1) tohit = 2;
		int damage = 0;
		String dialogue = "";
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = bone.throwdice(1,3);
			switch (textlevel) {
				case 0: break;
				case 1: 
					dialogue = ("Clunk!");
					break;
				case 2:
				case 3:
					dialogue = (capitalFirst(bname)+" gets hit by a flying bone for "+damage+" points!");
					break;
				default:
					System.out.println("Wha?");
			}				
		}
		else {
			switch (textlevel) {
				case 0: break;
				case 1:
					dialogue = ("Clatter!");
					break;
				case 2:
				case 3:
					dialogue = ("A bone flies off one of the skeletons and almost hits "+bname+"!");
					break;
				default:
					System.out.println("Wha?");
			}
		} 
		if(!target_present) {
			damage = 0;
			dialogue = ("Clatter!");
		} 
		return (new Attack(damage, dialogue));
	}
}

