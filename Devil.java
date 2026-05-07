
public class Devil extends Beast {

	//constructor
	public Devil(Dice birth, int mirror) {
		this.name = "devil";
		this.name_plural = "devils";
		this.array_pos = mirror;
		this.sound_of_pain = "SNARL!";
		this.kill_sounds = new String[3];
		this.kill_sounds[0] = "\"Aaaaaiee....\"";
		this.kill_sounds[1] = "\"Blluuugghhh...\"";
		this.kill_sounds[2] = "\"You win, wretched demon... gurgle...\"";
		this.kill_sounds_for_dogs = new String[3];
		this.kill_sounds_for_dogs[0] = "\"rrr.... growwlll... bluuuh.\"";
		this.kill_sounds_for_dogs[1] = "\"grrrrrrr  * \"";
		this.kill_sounds_for_dogs[2] = "\"whimper...  growrorrrr... gurgle...*\"";		
		this.kill_sounds_for_cats = new String[3];
		this.kill_sounds_for_cats[0] = "\"eeerrrowwrrrwwrrreee....\"";
		this.kill_sounds_for_cats[1] = " * PLOP * ";
		this.kill_sounds_for_cats[2] = "\"meww...  meep... glrrg..\"";
		this.singular_victory_message = "The demon is too weak to claim the souls of the dead, and so it rattles away, \nmuttering and shrieking.";
		this.plural_victory_message = "The demons are too weak to claim the souls of the dead, and so they rattle away, \nmuttering and shrieking.";
		this.type_for_battle = "beast";
		this.att_speed = new int[5];
		this.att_speed[0] = birth.throwdice(2,5);
		this.att_speed[1] = birth.throwdice(2,2);
		this.att_speed[2] = birth.throwdice(2,3);
		this.att_speed[3] = 0;
		this.att_speed[4] = 0;
		this.number_of_attacks = 3;
		this.strength = birth.throwdice(4,3);
		this.health = birth.throwdice(3,9) + 3;
		this.maxhealth = this.health;
		this.defense = 14;
		this.potential_wound = 0;
		this.strengthadj = 0;
		this.skill_bonus_from_hit = 3;
		if(this.strength >= 10) this.strengthadj = this.strength - 10;
		if(this.strength <= 6) this.strengthadj = this.strength - 7;
		this.value = 40;
		this.run_chance = 3;
	}		

	//devil attacks
	public Attack attack_one(Being victim, String bname, int textlevel, Dice pitchfork, String devil_name, boolean target_present) {
		int hitroll = pitchfork.throwdice(1,20);
		if(victim.gethealth() <= 0) hitroll = 20;
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[0];
		int damage = 0;
		String message = "";
		if(tohit <= 1) tohit = 2;
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = pitchfork.throwdice(2,4) + this.strengthadj;
			if(damage < 1) damage = 1;
		}
		if(damage > 0) {
			switch (textlevel) {
				case 0: break;
				case 1: 
					message = "Ppssshhhhh... ";
					if(victim.gethealth() > 0) {
						if(victim.getspecies().contains("cat")) message += "\"REER!\"";
						if(victim.getspecies().contains("dog")) message += "\"YELP!\"";
						if(victim.getspecies().contains("human")) message += "\"Yaarr!\"";
					}
					break;
				case 2:
				case 3:
					message = capitalFirst(devil_name)+" sticks "+bname+" with its pitchfork for "+damage+" points!";
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
					message = capitalFirst(devil_name)+" misses "+bname+" with its pitchfork!";
					break;
				default:
					System.out.println("Wha?");
			}
		}
		
		if(!target_present) {
			damage = 0;
			message = "crackle  sppspspssshhhhh";
		}
		
		return (new Attack(damage, message));
	}				

	
	
	public Attack attack_two(Being victim, String bname, int textlevel, Dice fireball, String devil_name, boolean target_present) {
		int hitroll = fireball.throwdice(1,20);
		if(victim.gethealth() <= 0) hitroll = 20;
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[1];
		if(tohit <= 1) tohit = 2;
		int damage = 0;
		String dialogue_a = "";
		String dialogue_b = "";
		if(textlevel > 1) {
			if(victim.getspecies().compareToIgnoreCase("human") == 0) {	
				dialogue_a = capitalFirst(devil_name)+" spits a fireball on "+bname+"'s shoes!";
			}
			else {
				dialogue_a = capitalFirst(devil_name)+" spits a fireball on "+bname+"'s paws!";
			}
		}
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = fireball.throwdice(1,4);
			switch (textlevel) {
				case 0: break;
				case 1: 
					if(victim.gethealth() > 0) {
						if(victim.getspecies().compareToIgnoreCase("human") == 0) dialogue_a = "\"Hot! Hot! Hot!\"";
						if(victim.getspecies().compareToIgnoreCase("cat") == 0) dialogue_a = "\"Reer! Reer! Reeeeer!\"";
						if(victim.getspecies().compareToIgnoreCase("dog") == 0) dialogue_a = "\"Yip! Yip! Yipe!\"";
					}
					else dialogue_a = "WHOOSH  *roast crackle*";
					break;
				case 2:
				case 3:
					if(victim.getspecies().compareToIgnoreCase("human") == 0) {
						if(victim.gethealth() > 0) dialogue_b = capitalFirst(bname)+" dances and gets burned for "+damage+" points!";
						else dialogue_b = capitalFirst(bname)+"'s feet get roasted!";
						break;
					}
					else {
						if(victim.gethealth() > 0) dialogue_b = capitalFirst(bname)+" rolls around and gets burned for "+damage+" points!";
						else dialogue_b = capitalFirst(bname)+"'s body gets roasted!";
						break;
					} 
				default:
					System.out.println("Wha?");
			}				
		}
		else {
			switch (textlevel) {
				case 0: break;
				case 1:
					dialogue_a = "WHAM! WHAM!";
					break;
				case 2:
				case 3:
					if(victim.getspecies().compareToIgnoreCase("human") == 0) dialogue_b = capitalFirst(bname)+" quickly stomps out the flames!"; 
					else dialogue_b = capitalFirst(bname)+" quickly rolls to put out the flames!";
					break;
				default:
					System.out.println("Wha?");
			}
		} 
		
		if(!target_present) {
			damage = 0;
			dialogue_a = "SWIPE";
			dialogue_b = "  STAB";
		}
		
		return(new Attack(damage, dialogue_a, dialogue_b));
	}

	public Attack attack_three(Being victim, String bname, int textlevel, Dice laughter, String devil_name, boolean target_present) {
		int hitroll = laughter.throwdice(1,20);
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[2];
		if(tohit <= 1) tohit = 2;
		int damage = 0;
		String dialogue_a = "";
		String dialogue_b = "";
		String dialogue_c = "";
		if(textlevel > 1) {
			dialogue_a = capitalFirst(devil_name)+" laughs horribly!";
		}
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = victim.attack_player(victim, victim.getname(), laughter);
			if(damage < 2) damage = 2;
			if(victim.getspecies().compareToIgnoreCase("human") == 0) {			
				switch (textlevel) {
					case 0: break;
					case 1: 
						dialogue_a = "\"HA HA HA... GAAAAH!\"";
						break;
					case 2:
					case 3:
						if(victim.gethealth() > 0) dialogue_b = capitalFirst(bname)+" laughs with the devil... and then STABS HIMSELF for "+damage+" points!";
						else dialogue_b = "With his last breath, "+bname+" laughs horribly with the devil!";
						break;
					default:
						System.out.println("Wha?");
				}				
			}
			else {
				if(victim.check_masters_health() > 0 && victim.gethealth() > 0) {
					if(laughter.throwdice(1, 10) < 8) {
						damage = damage * -1;
					}
				}						
			
				if(victim.getspecies().compareToIgnoreCase("dog") == 0) {			
					switch (textlevel) {
						case 0: break;
						case 1: 
							dialogue_a = "\"RRRR... RRRrrrAAHA HA HA!\"";
							break;
						case 2:
						case 3:
							if(victim.gethealth() > 0) {
								dialogue_a = "\"RRRR... RRRrrrAAHA HA HA!\"";
								dialogue_b = capitalFirst(bname)+"'s growls become an unnatural dog laugh!";
								if(damage < 0) dialogue_c = capitalFirst(bname)+" viciously BITES ITS MASTER for "+damage*-1+" points!";
								else dialogue_c = capitalFirst(bname)+" twists around and BITES ITSELF for "+damage+" points!";
							}
							else dialogue_b = capitalFirst(bname)+"'s lips curl into a horrible dog grin...";
							break;
						default:
							System.out.println("Wha?");
					}				
				}
				if(victim.getspecies().compareToIgnoreCase("cat") == 0) {			
					switch (textlevel) {
						case 0: break;
						case 1: 
							dialogue_a = "\"EEEEEEEEEE!\"";
							break;
						case 2:
						case 3:
							if(victim.gethealth() > 0) {
								dialogue_a = "\"EEEEEEEEEE!\"";
								dialogue_b = capitalFirst(bname)+"'s eyes bug out, and it shrieks like a banshee!";
								if(damage < 0) dialogue_c = capitalFirst(bname)+" ATTACKS ITS MASTER for "+damage*-1+" points!";
								else dialogue_c = capitalFirst(bname)+" claws at ITS OWN FACE for "+damage+" points!";
							}
							else dialogue_b = capitalFirst(bname)+" collapses with a dying howl...";
							break;
						default:
							System.out.println("Wha?");
					}				
				}
			}
		}			
		else {
			if(victim.getspecies().compareToIgnoreCase("human") == 0) {	
				switch (textlevel) {
					case 0: break;
					case 1:
						dialogue_a = "\"Away, servants of the profane!\"";
						break;
					case 2:
					case 3:
						dialogue_b = victim.getname()+" covers his ears!";
						break;
					default:
						System.out.println("Wha?");
				}
			}
			if(victim.getspecies().compareToIgnoreCase("dog") == 0) {	
				switch (textlevel) {
					case 0: break;
					case 1:
						dialogue_a = "B A R K   B A R K  B A R K   B A R K  ! !";
						break;
					case 2:
					case 3:
						dialogue_a = "B A R K   B A R K  B A R K   B A R K  ! !";
						dialogue_b = capitalFirst(bname)+" barks LOUD, so as not to hear the demon's laugh!";
						break;
					default:
						System.out.println("Wha?");
				}
			}
			if(victim.getspecies().compareToIgnoreCase("cat") == 0) {	
				switch (textlevel) {
					case 0: break;
					case 1:
						dialogue_a = "REEEEEEEOOOOOWWWWWRRR ! !";
						break;
					case 2:
					case 3:
						dialogue_a = "REEEEEEEOOOOOWWWWWRRR ! !";
						dialogue_b = capitalFirst(bname)+" howls LOUD, so as not to hear the demon's laugh!";
						break;
					default:
						System.out.println("Wha?");
				}
			}		
		}
		
		if(!target_present) {
			damage = 0;
			dialogue_a = "HA  HA  HA  HA";
			dialogue_b = "  HA  HA  HA   HAAAAAAAAAA";

		}
		
		if(damage < 0) damage = damage * -1;
		 
		return (new Attack(damage, dialogue_a, dialogue_b, dialogue_c));
	}
	
}
	


