
public class Dog extends Pet {

	//constructors
	public Dog(Dice birth, int armysize, int humansize, int boardside, int array_pos) {
		
		this(birth, 1, armysize, humansize, boardside, array_pos);
		
	}
	
	public Dog(Dice birth, int generation, int armysize, int humansize, int boardside, int mirror) {

		this.name = "none";
		this.humansize = humansize;
		this.boardside = boardside;
		this.masters_name = "none";
		this.masters_number = -1;
		this.species = "dog";
		this.speed = birth.throwdice(2,4);
		this.strength = birth.throwdice(3,6);
		this.health = birth.throwdice(2,10) + 10;
		this.maxhealth = this.health;
		this.defense = 8 + this.speed;
		this.personality = birth.throwdice(1,100);
		this.loyalty = birth.throwdice(2,4) + 2;
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
		this.was_mourned = false;
		this.recent_burial_attempt = false;
		this.in_the_forest = false;
		this.poisoned = false;
		this.poison_damage_this_round = false;
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
		this.poison_type = "none";
		this.poison_status = "okay";
		this.type_for_battle = "player";
		this.attack_verb = "bites";
		this.miss_verb = "lunges";
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
			if(i < this.humansize) this.loyalty_message[i] = false;
		}
		soundeffect[0] = "GRRR BARK!";
		soundeffect[1] = "SNARL!";
		soundeffect[2] = "CHOMP!";
		soundeffect[3] = "RRIP GRRARR!";
		soundeffect[4] = "GNASH GRRAR GROWL!";
	}


	//methods for Dogs	
	
	public int getreturn_to_master_adj() {
		return (int)(100 - this.rounds_away_from_master - 5*this.loyalty - this.master_calls*20 - this.rounds_with_master*this.loyalty/10);
	}	
	
}
