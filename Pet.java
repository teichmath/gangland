
public class Pet extends Player {

	boolean hide;
	int masters_number;
	int rounds_with_master;
	int rounds_by_masters_side;
	int total_rounds_by_masters_side;
	int rounds_away_from_master;
	int master_calls;
	int humansize;
	int masters_health_when_last_updated;
	int return_to_master_adj;
	int memory_causing_loyalty;
	String masters_name;
	boolean[] loyalty_message;
	boolean new_master_message;
	boolean knows_gregor; //pets might need to know him for some reason, down the line.

	
	//getters

	public boolean hidden() {
		return this.hide;
	}
	
	public int check_masters_health() {
		return this.masters_health_when_last_updated;
	}

	public int getmasters_number() {
		return this.masters_number;
	}
	
	public String getmasters_name() {
		return this.masters_name;
	}

	public int getnumber_of_attacks() {
		if(this.in_the_forest) return 3;
		else return 1;
	}
	
	public int getrounds_with_master(){
		return this.rounds_with_master;
	}
	
	public int getrounds_by_masters_side(){
		return this.rounds_by_masters_side;
	}
	
	public int gettotal_rounds_by_masters_side(){
		return this.total_rounds_by_masters_side;
	}
	
	public int getrounds_away_from_master() {
		return this.rounds_away_from_master;
	}
	
	public int getnumber_of_master_calls() {
		return this.master_calls;
	}
	
	public int getreturn_to_master_adj() {
		return (100 - this.rounds_away_from_master - 6*this.loyalty - this.master_calls*20);
	}

	public int get_memory_causing_loyalty() {
		return this.memory_causing_loyalty;
	}

	public boolean has_name() {
		if(this.name.compareToIgnoreCase("none") == 0) return false;
		else return true;
	}
	
	public boolean did_loyalty_message() {
		if(this.masters_number != -1) {
			if(this.loyalty_message[this.masters_number]) return true;
			else return false;
		}
		else return false;
	}
	
	public boolean did_new_master_message() {
		if(this.new_master_message) return true;
		else return false;
	}
	
	public boolean pet_knows_gregor() {
		return this.knows_gregor;
	}


	//setters
	
	public void new_master(int mnumber, String mname, int pers) {
		this.masters_number = mnumber;
		this.masters_name = mname;
		this.rounds_with_master = 0;
		this.rounds_by_masters_side = 0;
		this.total_rounds_by_masters_side = 0;
		this.rounds_away_from_master = 0;
		this.master_calls = 0;
		if(this.friend[mnumber] == -1) this.friend[mnumber] = Math.abs(this.personality - pers);
	}
	
	public void leave_master() {
		this.masters_number = -1;
		this.masters_name = "none";
		this.rounds_with_master = 0;
		this.rounds_by_masters_side = 0;
		this.total_rounds_by_masters_side = 0;
		this.rounds_away_from_master = 0;
		this.master_calls = 0;
		this.memory_causing_loyalty = 0;
		this.new_master_message = false;
	}

	public void tick_rounds_with_master(){
		this.rounds_with_master++;
	}
	
	public void tick_rounds_by_masters_side() {
		this.rounds_by_masters_side++;
		this.total_rounds_by_masters_side++;
	}

	public void tick_rounds_away_from_master() {
		this.rounds_away_from_master++;
	}

	public void tick_number_of_master_calls() {
		this.master_calls++;
	}

	public void tick_memory_causing_loyalty() {
		this.memory_causing_loyalty--;
		if(this.memory_causing_loyalty < 0) this.memory_causing_loyalty = 0;
	}

	public void make_memory(String type) {
		if(type.compareToIgnoreCase("food") == 0) this.memory_causing_loyalty += Math.pow(2, this.loyalty);
		if(type.compareToIgnoreCase("battle") == 0) this.memory_causing_loyalty += (4 * this.loyalty);
		if(type.compareToIgnoreCase("dig") == 0) this.memory_causing_loyalty += (2 * this.loyalty);
	}

	public void go_hide() {
		this.hide = true;
	}
	
	public void come_out_from_hiding() {
		this.hide = false;
	}
	
	public void hide_switch() {
		boolean he_is_hiding = this.hide;
		if(he_is_hiding) this.hide = false;
		else this.hide = true;
	}	

	public void adjust_loyalty(int delta) {
		this.loyalty =+ delta;
	}
	
	public void do_loyalty_message() {
		if(this.masters_number != -1) this.loyalty_message[this.masters_number] = true;
	}
	
	public void do_new_master_message() {
		new_master_message = true;
	}
	
	public void reset_loyalty_messages() {
		for(int i = 0; i < this.humansize; i++) {
			this.loyalty_message[i] = false;
		}
	}

	public void update_masters_health(int new_health) {
		this.masters_health_when_last_updated = new_health;
	}

	public void rise_to_seek_brains(int new_res_clock) {
		this.zombie = true;
		this.affiliation = 0;
		this.health = this.maxhealth;
		this.round_clock = 0;
		this.leader = false;
		this.freshly_dead = false;
		this.just_arrived = true;
		this.just_hatched = true;
		this.knows_gregor = false;
		this.poisoned = false;
		this.recent_eat_brains_clock = 0;
		this.resurrection_clock = new_res_clock;
		if(this.brains_hunger > -1) this.brains_hunger = -1;
		this.burial_meter = 30;
		this.blessed = false;
	}
	
	public void eat_brains() {
		this.brains_hunger+=2;
		this.recent_eat_brains_clock = 0;
	}
	
	public void pet_sees_gregors_house() {
		this.knows_gregor = true;
	}

	public void reset_from_master_reunion_or_parting() {
		this.master_calls = 0;
		this.rounds_by_masters_side = 0;
		this.rounds_away_from_master = 0;
	}

}
