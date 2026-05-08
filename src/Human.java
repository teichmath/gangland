import java.util.ArrayList;
import java.io.*;

public class Human extends Player {


	String name_without_number;
	int[] incantation;
	int turns_as_leader;
	int devil_target;
	int one_who_seeks_me;
	int devils_clock;
	int shells;
	int meat;
	int lars_account;
	int[] leader_challenges_by_player;
	ArrayList<String> bladders;
	ArrayList<Integer> bladder_start_times;
	ArrayList<Integer> perceived_pets;
	ArrayList<Integer> perceived_pet_absences;
	ArrayList<String> perceived_pet_names;
	boolean got_a_job;
	boolean finished_job;
	boolean has_teeth;
	boolean[][] knows_joke;
	boolean[] read_writings_this_round;
	boolean soul_claimed;
	boolean devils_clock_running;
	boolean stay_put;
	boolean life_reused;
	boolean stained_fingers;
	boolean sparks;
	
	/*constructor guide
	
	I.		String	Dice	
	II.		String	Dice	int	lars		int		int		int		int		int		boolean
	III.	String	Dice	int	lars		int		int		int		int		int		boolean		int	gen
	IV.		String	Dice	int	lars		int		int		int		int		int		boolean		boolean extra_zombie
	V.		String	Dice	boolean	user	int		int		int		int		int		boolean		int lars
	VI.		String	Dice	int	lars		int		int		int		int		int		boolean		int gen					boolean		boolean
			name	birth					mirror	jokes	humsze	armysze	brdsid	cursed								user	    extra_zombie
	*/


	//constructors
	//I.
	public Human(String name, Dice birth) {
		this(name, birth, 0, 50, 0, 50, 50, 10, false, 1, false, false);
	}
	//II.
	public Human(String name, Dice birth, int lars, int mirror, int jokes, int humansize, int armysize, int boardside, boolean cursed) {
		this(name, birth, lars, mirror, jokes, humansize, armysize, boardside, cursed, 1, false, false);
	}
	//III.
	public Human(String name, Dice birth, int lars, int mirror, int jokes, int humansize, int armysize, int boardside, boolean cursed, int generation) {
		this(name, birth, lars, mirror, jokes, humansize, armysize, boardside, cursed, generation, false, false);
	}
	//IV.
	public Human(String name, Dice birth, int lars, int mirror, int jokes, int humansize, int armysize, int boardside, boolean cursed, boolean extra_zombie) {
		this(name, birth, lars, mirror, jokes, humansize, armysize, boardside, cursed, 1, false, extra_zombie);
	}
	//V.
	public Human(String name, Dice birth, boolean user, int mirror, int jokes, int humansize, int armysize, int boardside, boolean cursed, int lars) {
		this(name, birth, lars, mirror, jokes, humansize, armysize, boardside, cursed, 1, user, false);
	}
	//VI.
	public Human(String name, Dice birth, int lars, int mirror, int jokes, int humansize, int armysize, int boardside, boolean cursed, int generation, boolean is_the_user, boolean extrazombie) {
		this.species = "human";
		this.boardside = boardside;
		this.name = name;
		this.name_without_number = name;
		if(generation != 1) this.name = name+" "+generation;
		this.speed = birth.throwdice(2,4);
		this.strength = birth.throwdice(3,6);
		this.health = birth.throwdice(2,10) + 10;
		if(cursed) {
			this.speed -= 4;
			this.strength -= 8;
			this.health -= 10;
			if(this.speed < 2 && this.speed >=-1) this.speed = 2;
			if(this.speed < -1) this.speed = 1;
			if(this.strength < 2) this.strength = 2;
			if(this.health < 5) this.health = 5;
		}
		this.maxhealth = this.health;
		this.defense = 8 + this.speed;
		this.personality = birth.throwdice(1,100);
		this.loyalty = birth.throwdice(1,10);
		this.array_pos = mirror;
		this.affiliation = 0;
		this.potential_affiliation = 0;
		this.potential_wound = 0;
		this.age = 0;
		this.turns_as_leader = 0;
		this.former_affiliations = new int[100];
		this.friend = new int[armysize];
		this.encounters_this_round = new int[armysize];
		this.read_writings_this_round = new boolean[11];
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
		if(extrazombie) {
			this.x_position = -10;
			this.y_position = -10;
			this.last_x_position = this.x_position;
			this.last_y_position = this.y_position;
			this.potential_x_position = -10;
			this.potential_y_position = -10;
		}
		this.round_clock = 0;
		this.leader = false;
		this.freshly_dead = false;
		this.just_arrived = true;
		this.just_hatched = true;
		if(this.generation == 1) just_hatched = false;
		this.got_a_job = false;
		this.finished_job = false;
		this.has_teeth = false;
		this.sword_drawn = false;
		this.stained_fingers = false;
		this.sparks = false;
		this.zombie_potential = false;
		this.zombie = false;
		this.blessed = false;
		this.cursed = cursed;
		this.soul_claimed = false;
		this.devils_clock_running = false;
		this.recent_burial_attempt = false;
		this.in_the_forest = false;
		this.left_forest_need_message = false;
		this.poisoned = false;
		this.was_mourned = false;
		this.stay_put = false;
		this.life_reused = false;
		this.recent_eat_brains_clock = 0;
		this.brains_hunger = 0;
		this.recent_hit_clock = 0;
		this.house_clock = -1;
		this.gregor_clock = 0;
		this.devils_clock = 0;
		this.resurrection_clock = 20;
		this.forest_entry_clock = 0;
		this.kills = 0;
		this.gang_we_asked_to_merge = 0;
		this.burial_meter = 30;
		this.last_longhouse_visit = 0;
		this.devil_target = -1;
		this.one_who_seeks_me = -1;
		this.shells = 0;
		this.meat = 0;
		this.lars_account = lars;
		this.bladders =  new ArrayList<String>();
		this.bladder_start_times =  new ArrayList<Integer>();
		this.perceived_pets = new ArrayList<Integer>();
		this.perceived_pet_absences = new ArrayList<Integer>();
		this.perceived_pet_names = new ArrayList<String>();
		this.poison_clock = 0;
		this.poison_type = "none";
		this.poison_status = "okay";
		this.poison_damage_this_round = false;
		this.type_for_battle = "player";
		this.attack_verb = "hits";
		this.miss_verb = "swings";
		this.bird_report = "";
		this.zombie_meetings = new boolean[armysize];
		this.knows_joke = new boolean[jokes][armysize];
		this.forest_perceived = new boolean[6][6];
		this.forest_entry_perceived = new int[2];
		this.forest_exit_perceived = new int[2];
		this.incantation = new int[3];
		this.time_of_meeting = new int[armysize];
		this.leader_challenges_by_player = new int[humansize];
		this.soundeffect = new String[5];
		this.forest_spirit_credits = 0;
		for(int i=0; i<3; i++) {
			if(i < 2) {
				this.forest_entry_perceived[i] = 0;
				this.forest_exit_perceived[i] = 0;
			}
			this.incantation[i] = -1;
		}
		for(int i=0; i<100; i++){
			this.former_affiliations[i] = 0;
		}
		for(int i=0; i<6; i++) {
			for(int j=0; j<6; j++) {
				forest_perceived[i][j] = false;
			}
			if(i < 5) soundeffect[i] = "";
		}
		for(int i=0; i<armysize; i++) {
			this.friend[i] = -1;
			this.encounters_this_round[i] = 0;
			this.time_of_meeting[i] = -1;
			this.zombie_meetings[i] = false;
			if(i < 11) this.read_writings_this_round[i] = false;
			for(int j=0; j<jokes; j++){
				this.knows_joke[j][i] = false;
			}
			if(i < humansize) this.leader_challenges_by_player[i] = 0;
		}
		if(!is_the_user) {
			int joke_you_know = birth.throwdice(1, jokes+3);
			if(joke_you_know <= jokes) this.knows_joke[joke_you_know-1][mirror] = true;
		}
		if(mirror < armysize) this.friend[mirror] = 0;

	}


	//methods for Humans	
	
	//getters
	
	public String getname_without_number() {
		return this.name_without_number;
	}
	
	public int getnumber_of_attacks() {
		int noa = (int)Math.floor(this.weaponskill/100) + 1;
		return noa;
	}	
	
	public int getincantation(int word) {
		return this.incantation[word];
	}
	
	public int getdevils_clock() {
		return this.devils_clock;
	}
	
	public int getturns_as_leader() {
		return this.turns_as_leader;
	}
	
	public int getdevil_target() {
		return this.devil_target;
	}
	
	public int getone_who_seeks_me() {	
		return this.one_who_seeks_me;
	}
	
	public int getnumber_of_shells() {
		return this.shells;
	} 
	
	public int getamount_of_meat() {
		return this.meat;
	}

	public int getnumber_of_bladders() {
		return this.bladders.size();
	}
	
	public int getnumber_of_empty_bladders() {
		int nb = 0;
		for(int i = 0; i < this.bladders.size(); i++) {
			if(this.bladders.get(i).compareTo("empty")==0) nb++;
		}
		return nb;
	}
	
	public boolean hasbladder_of(String contents) {
		boolean has_it = false;
		if(this.bladders.contains(contents)) has_it = true;
		return has_it;
	}
	
	public boolean is_certain_bladder_full(int index) {
		boolean is_full = false;
		if(index < this.bladders.size()) {
			if(this.bladders.get(index).compareToIgnoreCase("empty") != 0) is_full = true;
		}
		return is_full;
	}
	
	public String getcontents_of_certain_bladder(int index) {
		String contents = "none";
		if(index < this.bladders.size()) contents = this.bladders.get(index);
		return contents;
	}
	
	public int getstart_time_of_certain_bladder(int index) {
		if(index < this.bladder_start_times.size()) return this.bladder_start_times.get(index);
		else return 0;
	}
	
	public int getnumber_of_perceived_pets() {
		return this.perceived_pets.size();
	}

	public int getcertain_perceived_pet(int index) {
		if(index < this.perceived_pets.size() && index > -1) return this.perceived_pets.get(index);
		else return -1; 
	}
	
	public boolean does_player_perceive_this_pet(int pet_number) {
		Integer pet_to_check = new Integer(pet_number);
		if(this.perceived_pets.contains(pet_to_check)) return true;
		else return false;
	}

	public boolean does_player_perceive_this_pet_by_name(String name) {
		boolean rb = false;
		for(int i = 0; i < perceived_pet_names.size(); i++) {
			if(this.perceived_pet_names.get(i).compareToIgnoreCase(name) == 0) {
				rb = true;
				break;
			}
		}
		return rb;
	}

	public int get_perceived_pet_away_time_by_pet_number(int pet_number) {
		Integer check_this_pet = new Integer(pet_number);
		if(this.perceived_pets.contains(check_this_pet)) return this.perceived_pet_absences.get(this.perceived_pets.indexOf(check_this_pet));
		else return 0;
	}

	public int get_perceived_pet_away_time_by_index(int index) {
		if(index < this.perceived_pet_absences.size() && index > -1) return this.perceived_pet_absences.get(index);
		else return 0;
	}
	
	public int getperceived_pet_index_from_name (String name) {
		int pet_index = -1;
		for(int i = 0; i < perceived_pet_names.size(); i++) {
			if(this.perceived_pet_names.get(i).compareToIgnoreCase(name) == 0) {
				pet_index = perceived_pets.get(i);
				break;
			}
		}
		return pet_index;
	}
	
	public boolean knows_joke(int joke, int player) {
		return this.knows_joke[joke][player];
	}
	
	public boolean did_he_read(int writing) {
		return (this.read_writings_this_round[writing]);
	}
	
	public int how_many_read() {
		int well_how_many = 0;
		for(int i = 0; i< 11; i++) {
			if(this.read_writings_this_round[i]) {
				well_how_many++;
			}
		}
		return(well_how_many);
	}

	public boolean is_he_leader() {
		if(this.leader) return true;
		else return false;
	}
	
	public int getchallenges_left_from_player(int player_number) {
		if(player_number > -1 && player_number < this.leader_challenges_by_player.length) return this.leader_challenges_by_player[player_number];
		else return 0;
	}
	
	public boolean has_job() {
		if(this.got_a_job) return true;
		else return false;
	}
	
	public boolean finished_job() {
		if(this.finished_job) return true;
		else return false;
	} 
	
	public boolean has_teeth() {
		if(this.has_teeth) return true;
		else return false;
	}
	
	public boolean sword_drawn() {
		if(this.sword_drawn) return true;
		else return false;
	}
	
	public boolean has_stained_fingers() {
		if(this.stained_fingers) return true;
		else return false;
	}
	
	public boolean knows_sparks() {
		if(this.sparks) return true;
		else return false;
	}
	
	public boolean soul_claimed() {
		if(this.soul_claimed) return true;
		else return false;
	}
	
	public boolean devils_clock_running() {
		if(this.devils_clock_running) return true;
		else return false;
	} 

	public boolean will_stay_put() {
		if(this.stay_put) return true;
		else return false;
	} 

	public boolean life_was_reused() {
		if(this.life_reused) return true;
		else return false;
	}
	
	public String getbird_report() {
		return this.bird_report;
	}
	

	//setters
	
	public void hears_joke(int joke, int player) {
		this.knows_joke[joke][player] = true;
	}
	
	public void reset_knows_joke(int player, int jokes) {
		for(int i = 0; i < jokes; i++) {
			this.knows_joke[i][player] = false;
		}
	}
	
	public void read_writing(int writing) {
		this.read_writings_this_round[writing] = true;
	}
	
	public void reset_reading() {
		for(int i = 0; i < 11; i++) {
			this.read_writings_this_round[i] = false;
		}
	}
	
	public void setbird_report(String newreport) {
		this.bird_report = newreport;
	}
	
	public void set_shells(int number_of_shells) {
		this.shells = number_of_shells;
		if(number_of_shells < 0) this.shells = 0;
	}
	
	public void pick_up_shells(int number_of_shells) {
		this.shells = this.shells + number_of_shells;
	}
	
	public void lose_shells(int number_of_shells) {
		this.shells = this.shells - number_of_shells;
		if(number_of_shells < 0) number_of_shells = 0;
	}
	
	public void lose_all_shells() {
		this.shells = 0;
	}
	
	public void pick_up_meat() {
		this.meat++;
	}
	
	public void pick_up_bladder(int clock) {
		this.bladders.add("empty");
		this.bladder_start_times.add(clock);
	}
	
	public void fill_bladder(String contents) {
		for (int i = 0; i < this.bladders.size(); i++) {
			if(this.bladders.get(i).compareTo("empty")==0) {
				this.bladders.set(i, contents);
				break;
			}
		}
	}
	
	public void lose_meat() {
		this.meat--;
		if(this.meat < 0) this.meat = 0;
	}
	
	public void lose_all_meat() {
		this.meat = 0;
	}	
	
	public void lose_bladder(String contents) {
		for (int i = 0; i < this.bladders.size(); i++) {
			if(this.bladders.get(i).compareTo(contents)==0) {
				this.bladders.remove(i);
				this.bladder_start_times.remove(i);
				break;
			}
		}
	}
	
	public void lose_certain_bladder(int index) {
		if(index < this.bladders.size()) {
			this.bladders.remove(index);
			this.bladder_start_times.remove(index);
		}
	}
	
	public void lose_all_bladders() {
		this.bladders.clear();
		this.bladder_start_times.clear();
	}
	
	public void perceive_new_pet(int pet_number, String name) {
		this.perceived_pets.add(pet_number);
		this.perceived_pet_absences.add(0);
		this.perceived_pet_names.add(name);
	}
	
	public void setname_if_perceived_pet(int pet_number, String name) {
		if(this.perceived_pets.contains(new Integer(pet_number))) {
			this.perceived_pet_names.set(this.perceived_pets.indexOf(new Integer(pet_number)), name);
		}
	}
	
	public void lose_perception_of_pet(int pet_number) {
		int pet_to_lose = 0;
		if(perceived_pets.size() > 0) {
			for(int i = 0; i < perceived_pets.size(); i++) {
				if(perceived_pets.get(i) == pet_number) {
					pet_to_lose = i;
					break;
				}
			}
			this.perceived_pets.remove(pet_to_lose);
			this.perceived_pet_absences.remove(pet_to_lose);
			this.perceived_pet_names.remove(pet_to_lose);
		}
	}
	
	public void lose_perception_of_pet_by_index(int index) {
		this.perceived_pets.remove(index);
		this.perceived_pet_absences.remove(index);
		this.perceived_pet_names.remove(index);
	}
	
	public void tick_pet_absence_perception(int index) {
		Integer pap_set = this.perceived_pet_absences.get(index);
		pap_set++;
		this.perceived_pet_absences.set(index, pap_set);
	}
	
	public void reset_pet_absence_perception(int index) {
		this.perceived_pet_absences.set(index, 0);
	}
	
	public void stain_fingers() {
		this.stained_fingers = true;
	}
	
	public void notice_sparks() {
		this.sparks = true;
	}
	
	public void devil_lays_claim() {
		this.soul_claimed = true;
	}
	
	public void devil_drops_claim() {
		this.soul_claimed = false;
	}
	
	public void setdevil_target(int target) {
		this.devil_target = target;
	}
	
	public void setone_who_seeks_me(int hunter) {	
		this.one_who_seeks_me = hunter;
	} 
	
	public void return_for_devils_work() {
		this.affiliation = 0;
		this.health = this.maxhealth;
		this.round_clock = 0;
		this.leader = false;
		change_all_leader_challenges(0);
		this.freshly_dead = false;
		this.just_arrived = true;
		this.just_hatched = true;
		this.was_mourned = false;
		this.burial_meter = 30;
		this.devils_clock_running = true;
		this.devils_clock = 60;
		this.resurrection_clock = 20;
		if(this.x_position == 12) this.x_position = this.last_x_position;
		if(this.y_position == 12) this.y_position = this.last_y_position;
		if(this.potential_x_position == 12) this.potential_x_position = 0;
		if(this.potential_y_position == 12) this.potential_y_position = 0;
		this.blessed = false;
	}
	
	public void return_for_encore() {
		this.affiliation = 0;
		this.health = this.maxhealth;
		this.round_clock = 0;
		this.leader = false;
		change_all_leader_challenges(0);
		this.freshly_dead = false;
		this.just_arrived = true;
		this.just_hatched = true;
		this.was_mourned = false;
		this.burial_meter = 30;
		this.devils_clock_running = false;
		this.resurrection_clock = 20;
		this.life_reused = true;
		this.in_the_forest = false;
		this.friend[array_pos] = 0;
	}
	
	
	public void fail_devils_challenge() {
		this.health = 0;
		this.leader = false;
		change_all_leader_challenges(0);
		this.freshly_dead = true;
		this.devils_clock_running = false;
		this.devils_clock = 0;
		this.soul_claimed = false;
		this.devil_target = -1;
		if(this.round_clock >= 100) {
			this.x_position = this.last_x_position;
			this.y_position = this.last_y_position;
			this.potential_x_position = 0;
			this.potential_y_position = 0;
			this.round_clock = 3;
		}	
	}
	
	public void succeed_devils_challenge() {
		this.devils_clock_running = false;
		this.devils_clock = 0;
		this.soul_claimed = false;
		this.devil_target = -1;
		super_power_up();
	} 
	
	public void give_job() {
		this.got_a_job = true;
	}
	
	public void job_over() {
		this.finished_job = true;
		this.got_a_job = false;
	}
	
	public void get_teeth() {
		this.has_teeth = true;
	}
	
	public void give_teeth() {
		this.has_teeth = false;
	}
	
	public void draw_sword() {
		this.sword_drawn = true;
	}
	
	public void sheathe_sword() {
		this.sword_drawn = false;
	}
	
	public void set_will_stay_put() {
		this.stay_put = true;
	}
	
	public void free_to_move_again() {
		this.stay_put = false;
	}
	
	public void rise_to_seek_brains(int new_res_clock) {
		this.zombie = true;
		this.affiliation = 0;
		this.health = this.maxhealth;
		this.round_clock = 0;
		this.leader = false;
		change_all_leader_challenges(0);
		this.freshly_dead = false;
		this.just_arrived = true;
		this.just_hatched = true;
		this.got_a_job = false;
		this.finished_job = false;
		this.has_teeth = false;
		this.poisoned = false;
		this.recent_eat_brains_clock = 0;
		this.resurrection_clock = new_res_clock;
		if(this.brains_hunger > -3) this.brains_hunger = -3;
		this.burial_meter = 30;
		this.blessed = false;
		this.incantation[0] = -1;
		this.incantation[1] = -1;
		this.incantation[2] = -1;
		this.perceived_pets.clear();
		this.perceived_pet_absences.clear();
		this.perceived_pet_names.clear();
	}
	
	public void eat_brains() {
		this.brains_hunger++;
		this.recent_eat_brains_clock = 0;
	}
		
	public void lose_time_devils_clock() {
		this.devils_clock--;
	}
	
	public void addturn_as_leader() {
		this.turns_as_leader++;
	}
	
	public void makeleader() {
		if(!this.leader) change_all_leader_challenges(3);
		this.leader = true;
	}
	
	public void abdicate() {
		this.leader = false;
		change_all_leader_challenges(0);
	}
	
	public void change_all_leader_challenges(int new_value) {
		for(int i = 0; i < this.leader_challenges_by_player.length; i++) {
			this.leader_challenges_by_player[i] = new_value;
		}
	}
	
	public void spend_challenge_chance(int player_number) {
		if(player_number > -1 && player_number < this.leader_challenges_by_player.length) {
			this.leader_challenges_by_player[player_number]--;
		}
	}
			
	public void reset_challenges_from_this_player(int player_number) {
		if(player_number > -1 && player_number < this.leader_challenges_by_player.length) {
			if(this.leader) this.leader_challenges_by_player[player_number] = 3;
			else this.leader_challenges_by_player[player_number] = 0;
		}	
	}
		
	public void setincantation(int a, int b, int c) {
		this.incantation[0] = a;
		this.incantation[1] = b;
		this.incantation[2] = c;
	}

}
