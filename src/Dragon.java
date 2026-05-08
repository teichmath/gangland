import java.lang.reflect.Array;

public class Dragon extends Beast {

	int dragons_x;
	int dragons_y;
	boolean just_arrived;
	boolean asleep;
	
	//constructor
	public Dragon(Dice birth) {
		this.name = "dragon";
		this.sound_of_pain = "ROAAAAR!";
		this.array_pos = -1;
		this.kill_sounds = new String[3];
		this.kill_sounds[0] = "\"AIIEEEeeeee....\"";
		this.kill_sounds[1] = " * SNAP ! * ";
		this.kill_sounds[2] = "\" gurgle ...\"";
		this.kill_sounds_for_dogs = new String[3];
		this.kill_sounds_for_dogs[0] = "\"rrrooorrrhhh....\"";
		this.kill_sounds_for_dogs[1] = " * CRACK * ";
		this.kill_sounds_for_dogs[2] = "\"glarg glug...  \"";		
		this.kill_sounds_for_cats = new String[3];
		this.kill_sounds_for_cats[0] = "\"rowwrrreee....\"";
		this.kill_sounds_for_cats[1] = " * CRUNCH * ";
		this.kill_sounds_for_cats[2] = "\"REEEorwroowlll...  meep... glrrg..\"";
		this.singular_victory_message = "The dragon feasts on the remains...";
		this.plural_victory_message = "The dragons feast on the remains...";
		this.type_for_battle = "beast";
		this.att_speed = new int[5];
		this.att_speed[0] = birth.throwdice(2,10);
		this.att_speed[1] = birth.throwdice(3,3);
		this.att_speed[2] = birth.throwdice(3,3);
		this.att_speed[3] = birth.throwdice(1,10);
		this.att_speed[4] = birth.throwdice(2,4);
		this.number_of_attacks = 5;
		this.strength = birth.throwdice(5,6);
		this.health = birth.throwdice(10,5) + 50;
		this.maxhealth = this.health;
		this.defense = 8 + this.att_speed[1];
		this.potential_wound = 0;
		if (this.strength < 8) {
			this.strengthadj = 0;
		}
		else this.strengthadj = this.strength - 7;
		this.skill_bonus_from_hit = 4;
		this.dragons_x = -1;
		this.dragons_y = -1;
		this.just_arrived = true;
		this.asleep = false;
		this.value = 180;
		this.run_chance = 1;
	}
	
	public int getx() {
		return this.dragons_x;
	}
	
	public int gety() {
		return this.dragons_y;
	}
	
	public boolean just_arrived() {
		if (this.just_arrived) return true;
		else return false;
	}
	
	public boolean asleep() {
		if (this.asleep) return true;
		else return false;
	}
	
	public void changex(int delta) {
		this.dragons_x = this.dragons_x + delta;
	}

	public void changey(int delta) {
		this.dragons_y = this.dragons_y + delta;
	}

	public void setx(int newx) {
		this.dragons_x = newx;
	}

	public void sety(int newy) {
		this.dragons_y = newy;
	}

	public void arrive() {
		this.just_arrived = true;
	}
	
	public void stay_put() {
		this.just_arrived = false;
	}
	
	public void go_to_sleep() {
		this.asleep = true;
	}
	
	public void wake_up() {
		this.asleep = false;
	}

	//dragon attacks
	public Attack attack_group(Being[] toast, String[] bnames, int textlevel, Dice breath, String dragon_index, boolean target_present) {
		int number_of_victims = Array.getLength(toast);
		int damage = 0;
		int[] all_damage = new int[number_of_victims];
		String[] all_dialogue = new String[number_of_victims];
		int hitroll = breath.throwdice(1,20);
		boolean[] go_ahead_toast = new boolean[number_of_victims];
		boolean human_victim = false;
		for(int i = 0; i < number_of_victims; i++){
			go_ahead_toast[i] = true;
			all_damage[i] = 0;
			all_dialogue[i] = "";
			if(toast[i].getspecies().contains("human")) human_victim = true;
		}
		for(int i = 0; i < number_of_victims - 1; i++){
			for(int j = i + 1; j < number_of_victims; j++){
				if(toast[i].get_array_position() == toast[j].get_array_position()) go_ahead_toast[j] = false;
			}
		} 		
		for(int i = 0; i < number_of_victims; i++){
			if(go_ahead_toast[i]) {
				int tohit = toast[i].getdefense() + toast[i].getmoraleadj() - this.att_speed[0] + 5;
				if(tohit <= 1) tohit = 2;
				if(hitroll >= tohit || hitroll == 20) {
					Wait.Blip();
					damage = breath.throwdice(2,8);
					switch (textlevel) {
						case 0: break;
						case 1: 
							if(human_victim) all_dialogue[i] = ("The dragon breathes death!\n\"YEEAAAAAGH!\"");
							else all_dialogue[i] = ("The dragon breathes death!");
							break;
						case 2:
						case 3:
							all_dialogue[i] = ("The dragon's foul breath toasts "+bnames[i]+" for "+damage+" points!");
							break;
						default:
							System.out.println("Wha?");
					}				
					all_damage[i] = damage;
				}
				else {
					switch (textlevel) {
						case 0: break;
						case 1: 
							if(human_victim) {
								if(toast[i].gethealth() > 0) all_dialogue[i] = ("\"HA! YOU MISSED!\"");
								else all_dialogue[i] = "\"ha...  you missed...  bluuuuurgk\"";
							}
							break;
						case 2:
						case 3:
							all_dialogue[i] = ("The dragon's breath singes the hairs off "+bnames[i]+"'s leg!");
							break;
						default:
							System.out.println("Wha?");
					}				
					all_damage[i] = 0;
				}
			}
			else all_damage[i] = 0;
		}
		return (new Attack(all_damage, all_dialogue)); 
		
	}
	
	public Attack attack_two(Being victim, String bname, int textlevel, Dice claw, String whocares, boolean target_present) {
		int hitroll = claw.throwdice(1,20);
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[1] + 5;
		if(tohit <= 1) tohit = 2;
		int damage = 0;
		String dialogue = "";
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = claw.throwdice(1,5) + this.strengthadj;
			switch (textlevel) {
				case 0: break;
				case 1: 
					if(victim.gethealth() > 0) {
						if(victim.getspecies().contains("cat")) dialogue = (" ***SHRED***\n\"EEEERREER!\"");
						if(victim.getspecies().contains("dog")) dialogue = (" ***SHRED***\n\"YIIIIEIEIEI!\"");
						if(victim.getspecies().contains("human")) dialogue = (" ***SHRED***\n\"GLAARRG!\"");
					}
					else dialogue = (" ***SHRED*** ");
					break;
				case 2:
				case 3:
					dialogue = ("The dragon's claw rips into "+bname+" for "+damage+" points!");
					break;
				default:
					System.out.println("Wha?");
			}				
		}
		else {
			switch (textlevel) {
				case 0: break;
				case 1: 
					if(victim.getspecies().contains("human")) {
						if(victim.gethealth() > 0) dialogue = ("\"HA! YOU MISSED!\"");
						else dialogue = "\"ha...  you missed...  bluuuuurgk\"";
					}
					break;
				case 2:
				case 3:
					dialogue = ("The dragon swipes at "+bname+", but misses!");
					break;
				default:
					System.out.println("Wha?");
			}
		}
		
		if(!target_present) {
			damage = 0;
			dialogue = "SWIPE";
		}
		 
		return(new Attack(damage, dialogue));
	}
	
	public Attack attack_three(Being victim, String bname, int textlevel, Dice claw, String whocares, boolean target_present) {
		//another claw attack.
		return(this.attack_two(victim, bname, textlevel, claw, whocares, target_present));
	}
	
	public Attack attack_four(Being victim, String bname, int textlevel, Dice gaze, String whocares, boolean target_present) {
		int damage = 0;
		int wait_a = 1;
		String dialogue_a = "";
		String dialogue_b = "";
		String dialogue_c = "";
		if(textlevel > 1) {
			dialogue_a = ("The dragon meets "+bname+"'s eyes...");
		}
		int hitroll = gaze.throwdice(1,20);
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[3];
		if(tohit <= 1) tohit = 2;
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = gaze.throwdice(3,5);
			if(textlevel > 1) {
				dialogue_b = (capitalFirst(bname)+"'s soul is stung by the dark gaze!   ("+damage+" points of damage)");
				if(victim.gethealth() > 0) dialogue_c = (bname+" loses all hope!");
				else dialogue_c = "In his dying moments, "+bname+" loses all hope... what a cruel dragon!";
			}
			if(textlevel == 1) {
				if(victim.gethealth() > 0) {
					if(victim.getspecies().contains("cat")) dialogue_a = ("\"Roowwrrr...  Meeeoworororororrrrrrrr....\"");
					if(victim.getspecies().contains("dog")) dialogue_a = ("*whine whine whimper whine* \"arf arf arrrrrffff...\"");
					if(victim.getspecies().contains("human")) dialogue_a = ("\"I'm done for! Have mercy!\"");
				}
				else {
					if(victim.getspecies().contains("cat")) dialogue_a = ("\"meeeoworororrrrurk*\"");
					if(victim.getspecies().contains("dog")) dialogue_a = ("whine whine whimper *clunk*");
					if(victim.getspecies().contains("human")) dialogue_a = "\"i'm done for...  please...  have merc..  blrk*\"";
				}
			}
		}
		else {
			if(textlevel > 1) {
				if(victim.gethealth() > 0) dialogue_b = ("..."+bname+" is unfazed.");
				else dialogue_b = bname+" is gone... the dragon can do him no more harm.";
			}
		}
		
		if(!target_present) {
			damage = 0;
			dialogue_c = "   SNARL ";
			dialogue_b = " SNORT   ";
			dialogue_a = "\"Come back, o tasty one!\"";
		}		
		 
		return(new Attack(damage, dialogue_a, dialogue_b, dialogue_c, wait_a));
	}

	public Attack attack_five(Being victim, String bname, int textlevel, Dice tail, String whocares, boolean target_present) {
		int hitroll = tail.throwdice(1,20);
		if(victim.gethealth() <= 0) hitroll = 20;
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[4] + 5;
		if(tohit <= 1) tohit = 2;
		int damage = 0;
		String dialogue = "";
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = tail.throwdice(2,3) + this.strengthadj;
			if(textlevel > 1) {
				if(victim.gethealth() > 0) dialogue = ("The dragon's huge tail pummels "+bname+" for "+damage+" points!");
				else dialogue = "The dragon's huge tail crashes down on "+bname+"'s body!";
			}
			if(textlevel == 1) {
				dialogue = "***WHOMP*** ";
				if(victim.gethealth() > 0) {
					if(victim.getspecies().contains("cat")) dialogue += "      \"ROEERRWR!\"";
					if(victim.getspecies().contains("dog")) dialogue += "      \"GRRARFff...\"";
					if(victim.getspecies().contains("human")) dialogue += "      \"OOOOF! GRHGR...\"";
				}
			}
		}
		else {
			if(textlevel > 1) dialogue = (bname+" ducks to miss the dragon's tail!");
			if(textlevel == 1) dialogue = ("WHOOOSH!");
		}
		if(!target_present) {
			damage = 0;
			dialogue = ("WHOOOSH!");
		}
		return (new Attack(damage, dialogue));
	}
	
}
