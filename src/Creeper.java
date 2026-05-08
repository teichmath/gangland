
public class Creeper extends Beast {

	//constructor
	public Creeper(Dice birth, int mirror) {
		this.name = "creeper";
		this.name_plural = "creepers";
		this.array_pos = mirror;
		this.sound_of_pain = "SQUISH";
		this.kill_sounds = new String[3];
		this.kill_sounds[0] = "\"UUaauuh... how disgusting... \"";
		this.kill_sounds[1] = "\"Uuuuuuuuugh... ";
		this.kill_sounds[2] = "\"Blechhhhhh...\"";
		this.kill_sounds_for_dogs = new String[3];
		this.kill_sounds_for_dogs[0] = "\"rrrechhh... blecchh... rooorrrhhh....\"";
		this.kill_sounds_for_dogs[1] = "\"whine barf bluuuuurrrrg  * \"";
		this.kill_sounds_for_dogs[2] = "\"arf blarf bleechhh  bluuuuuhhhhh...  \"";		
		this.kill_sounds_for_cats = new String[3];
		this.kill_sounds_for_cats[0] = "\"rowwr ruuggghhh  bluuggh....\"";
		this.kill_sounds_for_cats[1] = "\"reeech meowrch blech mewwww * \"";
		this.kill_sounds_for_cats[2] = "\"meww...  meep... glrrg..\"";
		this.singular_victory_message = "The creeper's mandibles remove the parts that it deems most edible and nutrious. Its many legs carry it on its way after several horrible moments.";
		this.plural_victory_message = "The creepers have a horrid feast while lurching about on their many legs and twitching their mandibles and antennae.";
		this.type_for_battle = "beast";
		this.att_speed = new int[5];
		this.att_speed[0] = birth.throwdice(2,3);
		this.att_speed[1] = birth.throwdice(2,4);
		this.att_speed[2] = 0;
		this.att_speed[3] = 0;
		this.att_speed[4] = 0;
		this.number_of_attacks = 2;
		this.strength = birth.throwdice(8,3);
		this.health = birth.throwdice(5,6) + 10;
		this.maxhealth = this.health;
		this.defense = 18;
		this.potential_wound = 0;
		this.strengthadj = 0;
		if(this.strength >= 21) this.strengthadj = this.strength - 20;
		if(this.strength <= 11) this.strengthadj = this.strength - 12;
		this.skill_bonus_from_hit = 5;
		this.value = 40;
		this.run_chance = 0;
	}		

	//creeper attacks
	public Attack attack_one(Being victim, String bname, int textlevel, Dice slobber, String creeper_name, boolean target_present) {
		int hitroll = slobber.throwdice(1,20);
		if(victim.gethealth() <= 0) hitroll = 20;
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[0];
		int damage = 0;
		String dialogue = "";
		if(tohit <= 1) tohit = 2;
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = slobber.throwdice(2,6);
			if(damage < 1) damage = 1;
			switch (textlevel) {
				case 0: break;
				case 1: 
					if(victim.gethealth() > 0) {
						if(victim.getspecies().compareToIgnoreCase("human") == 0) dialogue = ("\"Yaaaugh!\"");
						else dialogue = "GLORP     BLORP";
					}
					else dialogue = "GLORP    BLORP";
					break;
				case 2:
				case 3:
					dialogue = capitalFirst(creeper_name)+" slobbers all over "+bname+" for "+damage+" points!";
					break;
				default:
					System.out.println("Wha?");
			}				
		}
		else {
			switch (textlevel) {
				case 0: break;
				case 1: 
					dialogue = "SMACK ** DROOL";
					break;
				case 2:
				case 3:
					dialogue = capitalFirst(creeper_name)+"'s mouth lunges for "+bname+", who dodges the terrible slobber!";
					break;
				default:
					System.out.println("Wha?");
			}
		} 
		
		if(!target_present) {
			damage = 0;
			dialogue = "GLORP    BLORP";
		}
				
		return(new Attack(damage, dialogue));
	}
	
	public Attack attack_two(Being victim, String bname, int textlevel, Dice bash, String creeper_name, boolean target_present) {
		int hitroll = bash.throwdice(1,20);
		if(victim.gethealth() <= 0) hitroll = 20;
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[1];
		if(tohit <= 1) tohit = 2;
		int damage = 0;
		String dialogue = "";
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = bash.throwdice(2,4);
			switch (textlevel) {
				case 0: break;
				case 1: 
					dialogue = "BASH!";
					break;
				case 2:
				case 3:
					dialogue = capitalFirst(bname)+" gets bashed head on by "+creeper_name+", for "+damage+" points!";
					break;
				default:
					System.out.println("Wha?");
			}				
		}
		else {
			switch (textlevel) {
				case 0: break;
				case 1:
					dialogue = "WHOOSH!";
					break;
				case 2:
				case 3:
					dialogue = capitalFirst(creeper_name)+" coils and throws its body at "+bname+", who dodges!";
					break;
				default:
					System.out.println("Wha?");
			}
		}

		if(!target_present) {
			damage = 0;
			dialogue = "  DROOL";
		}		
		 
		return(new Attack(damage, dialogue));
	}
}

