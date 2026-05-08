
public class Butterfly extends Beast {

	//constructor
	public Butterfly(Dice birth, int mirror) {
		this.name = "butterfly";
		this.name_plural = "butterflies";
		this.array_pos = mirror;
		this.sound_of_pain = "RCHCHCHCH!";
		this.kill_sounds = new String[3];
		this.kill_sounds[0] = "\"AAIEEEeeeee...\"";
		this.kill_sounds[1] = "\"AAaah nooo....\"";
		this.kill_sounds[2] = "\"Make it stop... \"";
		this.kill_sounds_for_dogs = new String[3];
		this.kill_sounds_for_dogs[0] = "\"whine whine whineeeeeoorrrhhh....\"";
		this.kill_sounds_for_dogs[1] = " * FLOP * ";
		this.kill_sounds_for_dogs[2] = "\"whimper...  \"";		
		this.kill_sounds_for_cats = new String[3];
		this.kill_sounds_for_cats[0] = "\"rowrr rowrr rowwweeeeowwrrrreee....\"";
		this.kill_sounds_for_cats[1] = " * PLOP * ";
		this.kill_sounds_for_cats[2] = "\"meww...  meep... glrrg..\"";
		this.singular_victory_message = "The butterfly makes a quick meal of things, then flutters away.";
		this.plural_victory_message = "The butterflies rend their kill to ribbons, then fly away dizzily, still ravenous.";
		this.type_for_battle = "beast";
		this.att_speed = new int[5];
		this.att_speed[0] = birth.throwdice(2,6);
		this.att_speed[1] = 0;
		this.att_speed[2] = 0;
		this.att_speed[3] = 0;
		this.att_speed[4] = 0;
		this.number_of_attacks = 1;
		this.strength = birth.throwdice(3,3);
		this.health = birth.throwdice(1,4) + 2;
		this.maxhealth = this.health;
		this.defense = 18;
		this.potential_wound = 0;
		this.strengthadj = 0;
		this.skill_bonus_from_hit = 4;
		if(this.strength >= 8) this.strengthadj = this.strength - 7;
		if(this.strength <= 4) this.strengthadj = this.strength - 5;
		this.value = 25;
		this.run_chance = 0;
	}		

	//butterfly attack
	public Attack attack_one(Being victim, String bname, int textlevel, Dice singing, String butterfly_name, boolean target_present) {
		Wait.Blip();
		int damage = singing.throwdice(3,5);
		if(damage < 1) damage = 1;
		String dialogue = "";
		if(victim.getspecies().compareToIgnoreCase("human") != 0) damage = 0;
		switch (textlevel) {
			case 0: break;
			case 1: 
				dialogue = "RCHCHCHCHCH!   ";
				if(victim.gethealth() > 0 && damage > 0) {
					if(victim.getspecies().contains("cat")) dialogue += "\"MEEOWRRL!\"";
					if(victim.getspecies().contains("dog")) dialogue += "\"YIII II II II!\"";
					if(victim.getspecies().contains("human")) dialogue += "\"Aaarg!\"";
				}
				break;
			case 2:
			case 3:
				dialogue = "RCHCHCHCHCH! \n"+capitalFirst(butterfly_name)+" sings in "+bname+"'s ear... ";
				if(victim.getspecies().compareToIgnoreCase("human") == 0) {
					if(victim.gethealth() > 0) dialogue += "he is tormented for "+damage+" points!";
				}
				else dialogue += "but the song has no effect.";
				break;
			default:
				System.out.println("Wha?");
		}
		if(!target_present) {
			damage = 0;
			dialogue = "RCHCHCHCHCH!   ";
		}				
	 	
		return (new Attack(damage, dialogue));
	}
}
	
	

