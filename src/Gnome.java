
public class Gnome extends Beast {


	//constructor
	public Gnome(Dice birth, int mirror) {
		this.name = "gnome";
		this.name_plural = "gnomes";
		this.array_pos = mirror;
		this.sound_of_pain = "\"Yee! Yi!\"";
		this.kill_sounds = new String[3];
		this.kill_sounds[0] = "\"Aaarrr .. * \"";
		this.kill_sounds[1] = "\"UUhhhhh .. \"";
		this.kill_sounds[2] = "\"Blug ...\"";
		this.kill_sounds_for_dogs = new String[3];
		this.kill_sounds_for_dogs[0] = "\"rrrooorrrhhh....\"";
		this.kill_sounds_for_dogs[1] = " * WHUMP * ";
		this.kill_sounds_for_dogs[2] = "\"whimper...  \"";		
		this.kill_sounds_for_cats = new String[3];
		this.kill_sounds_for_cats[0] = "\"rowwrrreee....\"";
		this.kill_sounds_for_cats[1] = " * WHUMP * ";
		this.kill_sounds_for_cats[2] = "\"meww...  meep... glrrg..\"";
		this.singular_victory_message = "The gnome laughs hysterically at having survived the battle, then scampers up a tree.";
		this.plural_victory_message = "The gnomes laugh and throw acorns at each other before skipping away."; 
		this.type_for_battle = "beast";
		this.att_speed = new int[5];
		this.att_speed[0] = birth.throwdice(3,4);
		this.att_speed[1] = birth.throwdice(3,4);
		this.att_speed[2] = birth.throwdice(1,8);
		this.att_speed[3] = birth.throwdice(1,12);
		this.att_speed[4] = 0;
		this.number_of_attacks = 4;
		this.strength = birth.throwdice(1,3);
		this.health = birth.throwdice(1,6) + 6;
		this.maxhealth = this.health;
		this.defense = 12;
		this.potential_wound = 0;
		this.strengthadj = 0;
		this.skill_bonus_from_hit = 3;
		this.value = 30;
		this.run_chance = 5;
	}		

	//gnome attacks
	public Attack attack_one(Being victim, String bname, int textlevel, Dice dart, String gnome_name, boolean target_present) {
		int hitroll = dart.throwdice(1,20);
		if(victim.gethealth() <= 0) hitroll = 20;
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[0];
		int damage = 0;
		String dialogue = "";
		if(tohit <= 1) tohit = 2;
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = dart.throwdice(1,6);
			if(damage < 1) damage = 1;
			switch (textlevel) {
				case 0: break;
				case 1: 
					if(victim.gethealth() > 0) {
						if(victim.getspecies().contains("cat")) dialogue = ("\"REEEEARHISSSS!\"");
						if(victim.getspecies().contains("dog")) dialogue = ("\"YIPE!\"");
						if(victim.getspecies().contains("human")) dialogue = ("\"Yowch!\"");
					}
					else dialogue = "THUMP";
					break;
				case 2:
				case 3:
					dialogue = (capitalFirst(gnome_name)+" shoots a wooden dart... it sticks "+bname+" for "+damage+" points!");
					break;
				default:
					System.out.println("Wha?");
			}				
		}
		else {
			switch (textlevel) {
				case 0: break;
				case 1: 
					dialogue = ("Spitoo!");
					break;
				case 2:
				case 3:
					dialogue = (capitalFirst(gnome_name)+" shoots a dart that whizzes past "+bname+"!");
					break;
				default:
					System.out.println("Wha?");
			}
		}
		if(!target_present) {
			damage = 0;
			dialogue = "Spitoo!";
		} 
		return (new Attack(damage, dialogue));
	}
	
	public Attack attack_two(Being victim, String bname, int textlevel, Dice kick, String gnome_name, boolean target_present) {
		int hitroll = kick.throwdice(1,20);
		int tohit = victim.getdefense() + victim.getmoraleadj() - this.att_speed[1];
		int damage = 0;
		String dialogue = "";
		if(tohit <= 1) tohit = 2;
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = kick.throwdice(1,2);
			switch (textlevel) {
				case 0: break;
				case 1: 
					if(victim.gethealth() > 0) {
						if(victim.getspecies().contains("cat")) dialogue = ("\"ROWLRRREEER!\"");
						if(victim.getspecies().contains("dog")) dialogue = ("\"GRRAARF!\"");
						if(victim.getspecies().contains("human")) dialogue = ("\"Gaar!\"");
					}
					else dialogue = "POIT";
					break;
				case 2:
				case 3:
					String kick_message = "";
					if(victim.getspecies().compareToIgnoreCase("human") == 0) {
						kick_message = capitalFirst(gnome_name)+" runs up to "+bname+" and kicks him in the shin for "+damage+" point";
						if(damage == 1) kick_message = kick_message + "!";
						else kick_message = kick_message + "s!";
					}
					if(victim.getspecies().compareToIgnoreCase("cat") == 0) {
						kick_message = capitalFirst(gnome_name)+" runs up to "+bname+" and drop kicks him for "+damage+" point";
						if(damage == 1) kick_message = kick_message + "!";
						else kick_message = kick_message + "s!";
					}
					if(victim.getspecies().compareToIgnoreCase("dog") == 0) {
						kick_message = capitalFirst(gnome_name)+" runs up to "+bname+" and jumps on his head for "+damage+" point";
						if(damage == 1) kick_message = kick_message + "!";
						else kick_message = kick_message + "s!";
					}					
					dialogue = (kick_message);
					break;
				default:
					System.out.println("Wha?");
			}				
		}
		else {
			switch (textlevel) {
				case 0: break;
				case 1: 
					dialogue = ("Nyaa!");
					break;
				case 2:
				case 3:
					dialogue = (capitalFirst(gnome_name)+" runs around in a circle!");
					break;
				default:
					System.out.println("Wha?");
			}
		} 
		if(!target_present) {
			damage = 0;
			dialogue = "Nyaa!";
		} 
		return (new Attack(damage, dialogue));
	}
	
	public Attack attack_group_btype(Being victim, String bname, int number_of_victims, int textlevel, Dice bomb, String gnome_name, boolean target_present) {
		int hitroll = bomb.throwdice(1,20);
		int damage = 0;
		String dialogue_a = "";
		String dialogue_b = "";
		String dialogue_c = "";
		if(hitroll >= 10) {
			Wait.Blip();
			damage = bomb.throwdice(2,4);
			switch (textlevel) {
				case 0: break;
				case 1:
					if(victim.gethealth() > 0) {
						if(victim.getspecies().contains("cat")) {
							dialogue_a = ("\"WHISSTTlllllee....\"");
							dialogue_c = ("\"EEEREREEREEEE!\"");
						}
						if(victim.getspecies().contains("dog")) {
							dialogue_a = ("\"WHISSTTlllllee....\"");
							dialogue_c = ("\"IIIIYELPYELPYELP!\"");
						}
						if(victim.getspecies().contains("human")) {
							dialogue_a = ("\"Look out !!\"");
							dialogue_c = ("\"Yeeaaugh!\"");
						}
					}
					dialogue_b = ("* * *  !!!!  B  L  A  M  !!!!  * * *");
					break;
				case 2:
				case 3:
					dialogue_a = (capitalFirst(gnome_name)+" throws a smoking apple ...");
					dialogue_b = ("* * *  !!!!  B  L  A  M  !!!!  * * *");
					if(number_of_victims == 1) dialogue_c = (bname+" is covered in burning applesauce for "+damage+" points!");
					else dialogue_c = ("The whole party is covered in burning applesauce for "+damage+" points!");
					break;
				default:
					System.out.println("Wha?");
			}
		}
		else {
			switch (textlevel) {
				case 0: break;
				case 1: 
					if(victim.gethealth() > 0) {
						if(victim.getspecies().contains("cat")) {
							dialogue_a = ("\"WHISSTTlllllee....\"");
							dialogue_c = ("\"ROWR! FFFFFT!!\"");
						}
						if(victim.getspecies().contains("dog")) {
							dialogue_a = ("\"WHISSTTlllllee....\"");
							dialogue_c = ("\"BARK BARK BARK!\"");
						}
						if(victim.getspecies().contains("human")) {
							dialogue_a = ("\"Look out !!\"");
							dialogue_c = ("\"Hah!\"");
						}
					}
					dialogue_b = ("psshhhhhhhhhh");
					break;
				case 2:
				case 3:
					dialogue_b = (capitalFirst(gnome_name)+" throws a smoking apple ...");
					dialogue_c = ("The fuse goes out!");
					break;
				default:
					System.out.println("Wha?");
			}
		} 
		return (new Attack(damage, dialogue_a, dialogue_b, dialogue_c, 0));
	}
	
	public Attack attack_four(Being victim, String bname, int textlevel, Dice pantsdown, String gnome_name, boolean target_present) {
		int hitroll = pantsdown.throwdice(1,20);
		int damage = 0;
		String dialogue_a = "";
		String dialogue_b = "";
		if(hitroll <= 20) {
			Wait.Blip();
			if(victim.getspecies().compareToIgnoreCase("human") == 0) {
				switch (textlevel) {
					case 0: break;
					case 1:
						dialogue_a = ("\"Hey!\"");
						dialogue_b = ("\"Hee hee hee!\"");
						break;
					case 2:
					case 3:
						dialogue_a = (capitalFirst(gnome_name)+" pulls down "+bname+"'s pants!");
						break;
					default:
						System.out.println("Wha?");
				}
			}
			if(victim.getspecies().compareToIgnoreCase("dog") == 0) {
				switch (textlevel) {
					case 0: break;
					case 1:
						dialogue_a = ("\"ARRR RRARR SNARL!\"");
						dialogue_b = ("\"Hee hee hee!\"");
						break;
					case 2:
					case 3:
						dialogue_a = (capitalFirst(gnome_name)+" yanks on "+bname+"'s ears!");
						break;
					default:
						System.out.println("Wha?");
				}
			}
			if(victim.getspecies().compareToIgnoreCase("cat") == 0) {
				switch (textlevel) {
					case 0: break;
					case 1:
						dialogue_a = ("\"REEEEEOWR!\"");
						dialogue_b = ("\"Hee hee hee!\"");
						break;
					case 2:
					case 3:
						dialogue_a = (capitalFirst(gnome_name)+" swings "+bname+" by its tail!");
						break;
					default:
						System.out.println("Wha?");
				}
			}
		}
		if(!target_present) {
			damage = 0;
			dialogue_a = "Hee hee hee!";
			dialogue_b = "Whoop whoop whoop whoop";
		} 
		return (new Attack(damage, dialogue_a, dialogue_b));
	}
	
}

