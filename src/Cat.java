
public class Cat extends Pet {

	int damage_multiplier;
	int number_of_attacks_multiplier;
	
	
	//constructors
	public Cat(Dice birth, int armysize, int humansize, int boardside, int array_pos) {
		
		this(birth, 1, armysize, humansize, boardside, array_pos);
		
	}
	
	public Cat(Dice birth, int generation, int armysize, int humansize, int boardside, int mirror) {
		super();
		this.name = "none";
		this.humansize = humansize;
		this.boardside = boardside;
		this.masters_name = "none";
		this.masters_number = -1;
		this.species = "cat";
		this.speed = birth.throwdice(2,4);
		this.strength = birth.throwdice(3,6);
		this.health = birth.throwdice(2,10) + 10;
		this.maxhealth = this.health;
		this.defense = 8 + this.speed;
		this.personality = birth.throwdice(1,100);
		this.loyalty = birth.throwdice(1,8);
		this.array_pos = mirror;
		this.affiliation = 0;
		this.potential_wound = 0;
		this.age = 0;
		this.former_affiliations = new int[100];
		this.friend = new int[armysize];
		this.encounters_this_round = new int[armysize];
		this.morale = 20;
		this.weaponskill = 0;
		this.generation = generation;
		this.slayer = "None yet";
		this.last_foe = "None yet";
		this.x_position = birth.throwdice(1, boardside);
		this.y_position = birth.throwdice(1, boardside);
		this.last_x_position = this.x_position;
		this.last_y_position = this.y_position;
		this.potential_x_position = 0;
		this.potential_y_position = 0;
		this.round_clock = 0;
		this.hide = false;
		this.freshly_dead = false;
		this.just_arrived = true;
		this.just_hatched = true;
		this.knows_gregor = false;
		this.zombie = false;
		this.blessed = false;
		this.cursed = false;
		this.sword_drawn = false;
		this.recent_burial_attempt = false;
		this.in_the_forest = false;
		this.poisoned = false;
		this.poison_damage_this_round = false;
		this.was_mourned = false;
		this.recent_hit_clock = 0;
		this.house_clock = -1;
		this.gregor_clock = 0;
		this.forest_entry_clock = 0;
		this.kills = 0;
		this.gang_we_asked_to_merge = 0;
		this.burial_meter = 30;
		this.last_longhouse_visit = 0;
		this.poison_clock = 0;
		this.rounds_with_master = 0;
		this.rounds_by_masters_side = 0;
		this.rounds_away_from_master = 0;
		this.master_calls = 0;
		this.damage_multiplier = 1;
		this.number_of_attacks_multiplier = 1;
		this.poison_type = "none";
		this.poison_status = "okay";
		this.type_for_battle = "player";
		this.attack_verb = "scratches";
		this.miss_verb = "bats";
		this.zombie_meetings = new boolean[armysize];
		this.loyalty_message = new boolean[this.humansize];
		this.forest_perceived = new boolean[6][6];
		this.forest_entry_perceived = new int[2];
		this.forest_exit_perceived = new int[2];
		this.time_of_meeting = new int[armysize];
		this.soundeffect = new String[5];
		this.forest_spirit_credits = 0;
		for(int i=0; i<2; i++) {
			this.forest_entry_perceived[i] = 0;
			this.forest_exit_perceived[i] = 0;
		}
		for(int i=0; i<100; i++){
			this.former_affiliations[i] = 0;
		}
		for(int i=0; i<6; i++) {
			for(int j=0; j<6; j++) {
				forest_perceived[i][j] = false;
			}
		}
		for(int i=0; i<armysize; i++) {
			this.friend[i] = -1;
			this.time_of_meeting[i] = -1;
			this.encounters_this_round[i] = 0;
			this.zombie_meetings[i] = false;
			if(i < this.humansize) loyalty_message[i] = false;
		}
		soundeffect[0] = "RRRROOWWRR!";
		soundeffect[1] = "SWIPE!";
		soundeffect[2] = "BITE!";
		soundeffect[3] = "RRRIP ROWR!!";
		soundeffect[4] = "HISSSSS RREEEOWWOROWR!";
	}


	//methods for Cats	
	
	public void high_battle(String btype) {
		this.morale = 40;
		if(btype.compareToIgnoreCase("rat")==0) {
			this.damage_multiplier = 3;
			this.number_of_attacks_multiplier = 4;
		}
		if(btype.compareToIgnoreCase("rattish man")==0) {
			this.damage_multiplier = 3;
			this.number_of_attacks_multiplier = 2;
		}

	}
	
	public void endhigh_battle() {
		this.damage_multiplier = 1;
		this.number_of_attacks_multiplier = 1;
	}

	public int getnumber_of_attacks() {
		int number_to_return = 1 * number_of_attacks_multiplier;
		if(this.in_the_forest) number_to_return += 3;
		return number_to_return;
	}

	public int getreturn_to_master_adj() {
		return (int)(100 - this.rounds_away_from_master - 3*this.loyalty - this.master_calls*4 - this.rounds_with_master*this.loyalty/10);
	}

	//cat attacks
	
	public int attack(Being t1, Being t2, String aname, String bname, Dice swing) {
		
		int attacker_bonus;
		if(this.morale == 0) attacker_bonus = -3;
		else if(this.morale >= 1 && this.morale <= 3) attacker_bonus = -2;
		else if(this.morale >= 4 && this.morale <= 8) attacker_bonus = -1;
		else if(this.morale >= 32 && this.morale <= 36) attacker_bonus = 1;
		else if(this.morale >= 37 && this.morale <= 39) attacker_bonus = 2;
		else if(this.morale == 40) attacker_bonus = 3;
		else attacker_bonus = 0;
		attacker_bonus = attacker_bonus + (int)(this.weaponskill/10);
		int hitroll = swing.throwdice(1,20);
		int tohit;
		if(t1 == null) tohit = t2.getdefense() - attacker_bonus - this.getspeedadj(0);
		else tohit = t1.getdefense() + t1.getmoraleadj() - attacker_bonus - this.getspeedadj(0);
		if(tohit <= 1) tohit = 2;
		if(hitroll >= tohit || hitroll == 20) {
//			Wait.Blip();
			int damage = swing.throwdice(1,6) + getstrengthadj() + attacker_bonus;
			damage = damage * damage_multiplier;
			if (damage <= 0) damage = 1;
			if(this.sword_drawn) damage = damage + swing.throwdice(6, 3);
			if(t1 == null) this.weaponskill = this.weaponskill + t2.getskill_bonus_from_hit();
			else this.weaponskill++;
			
			return damage;
			
		}
		else {
			return 0;
		} 
		
	}
	
	
}
