
public class Being {

	String name;
	String species;
	int health;
	int strength;
	int defense;
	int potential_wound;
	int maxhealth;
	int x_position;
	int y_position;
	int number_of_attacks;
	int poison_clock;
	int array_pos;
	boolean in_the_forest;
	boolean poisoned;
	boolean freshly_dead;
	String slayer;
	String last_foe;
	String poison_type;
	String poison_status;
	String type_for_battle;
	String attack_verb;
	String miss_verb;


	//getters
	public int get_array_position() {
		return this.array_pos;
	}

	public int getstrength() {
		return this.strength;
	}
	
	public int gethealth() {
		return this.health;
	}
	
	public int getmaxhealth() {
		return this.maxhealth;
	}
			
	public int getdefense() {
		return this.defense;
	}
	
	public String getname() {
		if(this.name.compareToIgnoreCase("none") == 0) return "A "+this.species;
		else return this.name;
	}
	
	public String getname_lower() {
		if(this.name.compareToIgnoreCase("none") == 0) return "a "+this.species;
		else return this.name;
	}
	
	public String getspecies() {
		return this.species;
	}
	
	public String getslayer() {
		return this.slayer;
	}
		
	public int getpotential_wound() {
		return this.potential_wound;
	}
	
	public String getlast_foe() {
		return this.last_foe;
	}
	
	public int getx() {
		return this.x_position;
	}
	
	public int gety() {
		return this.y_position;
	}
	
	public boolean ishere(int x, int y) {
		if(x == this.x_position && y == this.y_position) return true;
		else return false;
	}
	
	public String getpoison_type() {
		return this.poison_type;
	}
	
	public String getpoison_status() {
		return this.poison_status;
	}
	
	public int getpoison_clock() {
		return this.poison_clock;
	}
	
	public String gettype() {
		return this.type_for_battle;
	}

	public boolean just_died() {
		if(this.freshly_dead) return true;
		else return false;
	}

	public boolean is_poisoned() {
		if(this.poisoned) return true;
		else return false;
	}
	
	public boolean in_the_forest() {
		if(this.in_the_forest) return true;
		else return false;
	} 

	public int getchallenges_left_from_player(int player_number) {
		return 0;
	}
	
	//setters
	public void setarray_position(int newpos) {
		this.array_pos = newpos;
	}
	
	public void freshwound(int freshwound) {
		this.potential_wound += freshwound;
	}
	
	public void setpotential_wound(int value) {
		this.potential_wound = value;
	}
	
	public void reset_challenges_from_this_player(int player_number) {
	}
	
	public void change_all_leader_challenges(int value){
		}
	
	public void spend_challenge_chance(int value){
		}	

	//being takes damage
	public void ouch(int hit) {
		this.health = this.health - hit;
		if(this.health > this.maxhealth) this.health = this.maxhealth;
		if(this.health <= 0) this.freshly_dead = true;
		this.potential_wound = 0;
	}
	
	public void zero_health() {
		this.health = 0;
	}
	
	public void heal(int amount) {
		this.health += amount;
		if(this.health < 0) this.health = 0;
		if(this.health > this.maxhealth) this.health = this.maxhealth;
	}
	
	public void setslayer(String slayer) {
		this.slayer = slayer;
	}

	public void setlast_foe(String last_foe) {
		this.last_foe = last_foe;
	}
	
	public void become_poisoned(String type, int clock) {
		this.poisoned = true;
		this.poison_type = type;
		this.poison_clock = clock;
	}
	
	public int get_poison_damage(Dice dice) {	
		int damage = 0;
		if(this.poisoned) {
			if(this.poison_clock > 0) {
				if(this.poison_type.compareToIgnoreCase("A") == 0) {
					if(dice.throwdice(1, 3) == 1) damage = 1;
				}
				if(this.poison_type.compareToIgnoreCase("B") == 0) damage = 1;
				if(this.poison_type.compareToIgnoreCase("C") == 0) damage = 3;
				if(this.poison_type.compareToIgnoreCase("D") == 0) {
					if(this.health > 1) {
						damage = (int)(this.health/3);
					}
					else damage = 1;
				}
			}
		}
		return damage;
	}
	
	public void do_poison_damage(Dice dice) {
		int damage = get_poison_damage(dice);
		ouch(damage);
	}

	public void antidote() {
		this.poisoned = false;
		this.poison_type = "none";
		this.poison_clock = 0;
	}
	
	public void setpoison_status(String new_status) {
		this.poison_status = new_status;
	}
	
	public void lose_time_poison_clock() {
		this.poison_clock--;
	}
	
	public String capitalFirst(String phrase) {
		String cap = phrase.substring(0,1).toUpperCase() + phrase.substring(1);
		return cap;
	}	
	
	public String get_verb_for_miss() {
		return this.miss_verb;
	}
	
	public String getattack_verb() {
		return this.attack_verb;
	}
	
	
	//placeholders (general)
	
	public int getstrengthadj() {
		return 0;
	}

	public String toString() {
		return "";
	}

	public int getnumber_of_attacks() {
		return 0;
	}

	public int getspeedadj(int att) {
		return 0;
	}
	
	public int getmoraleadj() {
		return 0;
	}

	//player placeholders
	
	//getters
	
	public int getstat_for_uncertainty(int stat) {
		return 0;
	}
	
	public int getspeed() {
		return 0;
	}
	
	public int getgeneration() {
		return 0;
	}
	
	public int getpersonality() {
		return 0;
	}
	
	public int getloyalty() {
		return 0;
	}
	
	public int getaffiliation() {
		return 0;
	}

	public int getpotential_affiliation() {
		return 0;
	}
		
	public int getmorale() {
		return 0;
	}
	
	public int getformer_affiliation(int form_aff_array_place_to_look_up) {
		return 0;
	}
	
	public int getfriend(int friend_to_look_up) {
		return -1;
	}
	
	public int times_saw(int friend_to_look_up) {
		return 0;
	}
	
	public int gettime_of_meeting(int person) {
		return 0;
	}
		
	public int getweaponskill() {
		return 0;
	}
	
	public int getpotential_x() {
		return 0;
	}
	
	public int getpotential_y() {
		return 0;
	}
	
	public boolean is_going_here(int x, int y) {
		return false;
	}
	
	public boolean was_poison_damaged_this_round() {
		return false;
	}
	
	public int getlast_x() {
		return 0;
	}

	public int getlast_y() {
		return 0;
	}

	public int getround_clock() {
		return 0;
	}

	public int gethouse_clock() {
		return 0;
	}
	
	public int getgregor_clock() {
		return 0;
	}
	
	public int getage() {
		return 0;
	}
	
	public int getkills() {
		return 0;
	}
	
	public int getlast_longhouse_visit() {
		return 0;
	}
	
	public int getlast_gang() {
		return 0;
	}
	
	public int how_buried() {
		return 0;
	}
	
	public int getgang_we_asked_to_merge() {
		return 0;
	}
	
	public int getforest_entry_perceived_x() {
		return 0;
	}
	
	public int getforest_entry_perceived_y() {
		return 0;
	}

	public int getforest_exit_perceived_x() {
		return 0;
	}

	public int getforest_exit_perceived_y() {
		return 0;
	}
	
	public int getrounds_in_place() {
		return 0;
	}
	
	public boolean getforest_perceived(int x, int y) {
		return false;
	}

	public boolean[][] getwhole_forest_perceived() {
		boolean[][] stub = new boolean[1][1];
		stub[0][0] = false;
		return stub;
	}
	
	public int getnumber_of_forest_spirit_credits() {
		return 0;
	}
	
	public boolean knows_a_zombie(int player) {
		return false;
	}
	
	public boolean was_mourned() {
		return false;
	}
	
	public boolean just_arrived() {
		return false;
	}
	
	public boolean is_blessed() {
		return false;
	}
	
	public boolean is_cursed() {
		return false;
	}
	
	public int getlars_account() {
		return 0;
	}
	
	public void receive_curse() {
	}
	
	public boolean recent_burial_attempt() {
		return false;
	}
	
	public boolean need_forest_message() {
		return false;
	}

	public boolean life_was_reused() {
		return false;
	}

	
	public double getevaluation() {
		return 0;
	}
		
	public String longreport() {
		return "";
	}	
	
	public int getforest_entry_clock(){
		return 0;
	}
	
	
	//setters
	
	public void setname(String newname) {
	}
	
	public void setpersonality(int newpers) {
	}	
	
	public void super_power_up() {
	}
	
	public void setaffiliation(int newaff) {
	}
	
	public void setpotential_affiliation(int newaff) {
	}
	
	public void update_affiliation() {
	}
	
	public void setlast_longhouse_visit(int last) {
	}
	
	public void setlast_gang(int exgang) {
	}
	
	public void setmorale(int new_morale) {
	}		
	
	public void changemorale(int delta) {
	}
	
	public void driftmorale() {
	}
	
	public void tick_rounds_in_place() {
	}
	
	public void recover() {
	}
	
	public void camprecover() {
	}

	public void houserecover() {
	}
	
	public void heal_a_bit() {
	}	
	
	public void full_heal(){
	}
			
	public void battlescar(int scar) {
	}
	
	public void gainskill(int delta) {
	}
	
	public void forgetskill() {
	}
	
	public void changestrength(int delta) {
	}
	
	public void changespeed(int delta) {
	}

	public void changemaxhealth(int delta) {
	}

	public void x_change_simple(int x) {
	}

	public void y_change_simple(int y) {
	}

	public void setxy(int newx, int newy) {
	}
	
	public void setxyquick(int newx, int newy) {
	}
	
	public void redirect(int x, int y) {
	}

	public void revertxy() {
	}
		
	public void changexy(int xdelta, int ydelta) {
	}
	
	public void changexyoffboard(int xdelta, int ydelta) {
	}
	
	
	public boolean lastxy_was(int formerx, int formery) {
		return false;
	}
	
	public void setgang_we_asked_to_merge(int gangnumber) {	
	}
	
	public void setround_clock(int newtime) {
	}
	
	public void changeround_clock(int delta) {
	}
	
	public void zeroround_clock() {
	}
	
	public void sethouse_clock(int new_time) {
	}
	
	public void changehouse_clock(int delta) {
	}

	public void setgregor_clock(int new_time) {
	}
	
	public void changegregor_clock(int delta) {
	}
	
	public void getolder() {
	}
	
	public void didnt_just_arrive() {
	}
	
	public void kill_player() {
	}
	
	public void mourn_player() {
	}
	
	public void lay_player_to_rest() {
	}
	
	public void meet_zombie(int player) {
	}		
	
	public void reset_zombie_meeting(int player) {
	}
	
	public void zombie_slayer_power_up() {
	}	
	
	public void setfriend(int friend_position, int friend_new_value, int time, int codeline) {
	}
	
	public void changefriend(int friend_position, int delta) {
	}
	
	public void add_encounters(int friend, int delta) {
	}
	
	public void zeroencounters() {
	}
	
	public void settime_of_meeting(int person, int time) {
	}
	
	public void beltnotch() {
	}
	
	public void get_buried(int dirt) {
	}
	
	public void perceive_forest_anew(boolean[][] forest_perceived_incoming, int entry_point_x, int entry_point_y, int exit_point_x, int exit_point_y) {
	}
	
	public void leave_forest() {
	}		

	public void complete_leaving_forest() {
	}
	
	public void setnumber_of_forest_spirit_credits(int value) {
	}

	public void add_forest_spirit_credit() {
	}
	
	public void changeforest_entry_clock(int delta) {
	}

	public void setforest_entry_clock(int newtime) {
	}

	
	//player attack methods
	
	public int attack_player(Being target, String aname, Dice swing) {
		return 0;
	}
			
	public int attack_beast(Being target, String aname, Dice swing) {
		return 0;
	}
	
	public int attack(Being t1, Being t2, String aname, String bname, Dice swing) {
		return 0;		
	}
		
	//human place holders
	
	public String getname_without_number() {
		return "";
	}
	public int getdevils_clock() {
		return 100;
	}
	public int getturns_as_leader() {
		return 0;
	}
	
	public int getdevil_target() {
		return -1;
	}
	
	public int getone_who_seeks_me() {	
		return -1;
	}
	
	public int getnumber_of_shells() {
		return 0;
	} 
	
	public int getbrains_hunger() {
		return 0;
	}
	
	public int getincantation(int word) {
		return -1;
	}
	
	public int resurrection_clock() {
		return 0;
	}
	
	public boolean knows_joke(int joke, int player) {
		return false;
	}
	
	public boolean did_he_read(int writing) {
		return false;
	}
	
	public int how_many_read() {
		int well_how_many = 0;
		return(well_how_many);
	}

	public boolean is_he_leader() {
		return false;
	}
	
	public boolean has_job() {
		return false;
	}
	
	public boolean finished_job() {
		return false;
	} 
	
	public boolean has_teeth() {
		return false;
	}
	
	public boolean sword_drawn() {
		return false;
	}
	
	public boolean has_symbol() {
		return false;
	}
	
	public boolean is_zombie() {
		return false;
	}
	
	public boolean soul_claimed() {
		return false;
	}
	
	public boolean devils_clock_running() {
		return false;
	} 

	public int getamount_of_meat() {
		return 0;
	}
	
	public void pick_up_meat() {
	}
	
	public void lose_meat() {
	}
	
	public void lose_all_meat() {
	}

	public int getnumber_of_bladders() {
		return 0;
	}
	
	public int getnumber_of_empty_bladders()  {
		return 0;
	}
	
	public void pick_up_bladder(int start) {
	}
	
	public void fill_bladder(String contents) {
		
	}
	
	public void lose_bladder (String contents) {
		
	}
	
	public void lose_all_bladders() {
		
	}
	
	public void lose_certain_bladder(int index) {
		
	}
	
	public boolean hasbladder_of(String contents) {
		return false;
	}

	public boolean is_certain_bladder_full(int index) {
		return false;
	}
	
	public String getcontents_of_certain_bladder(int index) {
		return "none";
	}
	
	public int getstart_time_of_certain_bladder(int index) {
		return 0;
	}
	
	public String attack_sounds(Dice swing) {
		return("");
	}
	
	public int getnumber_of_perceived_pets() {
		return 0;
	}

	public int getcertain_perceived_pet(int index) {
		return -1; 
	}
	
	public boolean does_player_perceive_this_pet(int pet_number) {
		return false;
	}

	public int get_perceived_pet_away_time_by_pet_number(int pet_number) {
		return 0;
	}

	public int get_perceived_pet_away_time_by_index(int index) {
		return 0;
	}
	
	public boolean has_stained_fingers() {
		return false;
	}
	
	public boolean knows_sparks() {
		return false;
	}
	
	public String getbird_report() {
		return "";
	}
	
	//setters
	
	public void setbird_report(String report) {
	}
	
	public void hears_joke(int joke, int player) {
	}
	
	public void reset_knows_joke(int player, int jokes) {
	}
	
	public void stain_fingers() {
	}

	public void notice_sparks() {
	}
	
	public void read_writing(int writing) {
	}
	
	public void reset_reading() {
	}
	
	public void set_shells(int number_of_shells) {
	}
	
	public void pick_up_shells(int number_of_shells) {
	}
	
	public void lose_shells(int number_of_shells) {
	}
	
	public void lose_all_shells() {
	}
	
	public void devil_lays_claim() {
	}
	
	public void devil_drops_claim() {
	}
	
	public void setdevil_target(int target) {
	}
	
	public void setone_who_seeks_me(int hunter) {	
	} 
	
	public void return_for_devils_work() {
	}

	public void return_for_encore() {
	}
	
	public void fail_devils_challenge() {
	}
	
	public void succeed_devils_challenge() {
	} 
	
	public void give_job() {
	}
	
	public void job_over() {
	}
	
	public void get_teeth() {
	}
	
	public void give_teeth() {
	}
	
	public void draw_sword() {
	}
	
	public void sheathe_sword() {
	}

	public void carve_forehead() {
	}
	
	public void rise_to_seek_brains(int new_res_clock) {
	}
	
	public void end_zombie() {
	}
	
	public void eat_brains() {
	}
	
	public void setresurrection_clock(int new_time) {
	}
		
	public void changeresurrection_clock(int delta) {
	}
	
	public void lose_time_devils_clock() {
	}
	
	public void get_hungrier() {
	}
	
	public void addturn_as_leader() {
	}
	
	public void makeleader() {
	}
	
	public void abdicate() {
	}
	
	public void setincantation(int a, int b, int c) {
	}
	
	public boolean will_stay_put() {
		return false;
	}
	
	public void set_will_stay_put() {
	}
	
	public void set_in_the_forest_true() {
	}
	
	public void free_to_move_again() {
	}
	
	public void perceive_new_pet(int pet_number, String name) {

	}

	public boolean does_player_perceive_this_pet_by_name (String name) {
		return false;
	}
	
	public int getperceived_pet_index_from_name (String name) {
		return -1;
	}
	
	public void set_name_if_perceived_pet(int index, String name) {
		
	}
	
	public void lose_perception_of_pet(int pet_number) {

	}
	
	public void lose_perception_of_pet_by_index(int index) {
	}	
	
	public void tick_pet_absence_perception(int index) {
	}
	
	public void reset_pet_absence_perception(int index) {
	}
	

//pet place holders
	
	public void hide_switch() {
	}
	
	public boolean hidden() {
		return false;
	}
	
	public boolean has_name() {
		return true;
	}

	public boolean pet_knows_gregor() {
		return false;
	}

	public boolean did_loyalty_message() {
		return false;
	}
	
	public boolean did_new_master_message() {
		return false;
	}
	
	public int get_memory_causing_loyalty() {
		return 0;
	}
	
	public void make_memory(String type) {
	}

	public void tick_memory_causing_loyalty() {
	}
	
	public void go_hide() {
	}
	
	public void new_master(int a, String b, int p) {
	}
	
	public void leave_master() {
	}
	
	public void tick_rounds_with_master() {
	}
	
	public void tick_rounds_by_masters_side(){
	}
	
	public void come_out_from_hiding() {
	}

	public int getmasters_number() {
		return -1;
	}
	
	public String getmasters_name() {
		return "";
	}
	
	public int getrounds_with_master() {
		return 0;
	}
	
	public int getrounds_by_masters_side() {
		return 0;
	}
	
	public int gettotal_rounds_by_masters_side() {
		return 0;
	}

	public int check_masters_health() {
		return 0;
	}
	
	public int attack(Player target, String aname, Dice hiss) {
		return 0;
	}
	
	public void do_loyalty_message() {
	}
	
	public void do_new_master_message() {
	}
	
	public void reset_loyalty_messages() {
	}
	
	public void update_masters_health(int mhealth) {
	}
	
	public int getrounds_away_from_master() {
		return 0;
	}
	
	public int getnumber_of_master_calls() {
		return 0;
	}
	
	public int getreturn_to_master_adj() {
		return 0;
	}

	public void tick_rounds_away_from_master() {
	}

	public void tick_number_of_master_calls() {
	}
	
	public void pet_sees_gregors_house() {
	}

	public void reset_from_master_reunion_or_parting() {
	}
		
	
	//specific to cat 
	
	public void high_battle(String a) {
	}
	
	public void endhigh_battle() {
	}

	//beast placeholders 
	
	public String getname_plural() {
		return "";
	}

	public String getsound_of_pain() {
		return "";
	}
	
	public String getkill_sound(int x, String y) {
		return "";
	}
	
	public String getvictory_message(int agreement) {
		return "";
	}
	
	public int getskill_bonus_from_hit() {
		return 0;
	}
	
	public int getvalue() {
		return 0;
	}

	public boolean it_might_run() {
		return false;
	}
	
	public int getrun_chance() {
		return 0;
	}
	
	
	
	
	//placeholders for beast attacks
	public Attack attack_one(Being victim, String bname, int textlevel, Dice bite, String rat_index, boolean tp) {
		return (new Attack());
	}
	public Attack attack_two(Being victim, String bname, int textlevel, Dice bite, String rat_index, boolean tp) {
		return (new Attack());
	}
	public Attack attack_three(Being victim, String bname, int textlevel, Dice bite, String rat_index, boolean tp) {
		return (new Attack());
	}
	public Attack attack_four(Being victim, String bname, int textlevel, Dice bite, String rat_index, boolean tp) {
		return (new Attack());
	}
	public Attack attack_five(Being victim, String bname, int textlevel, Dice bite, String rat_index, boolean tp) {
		return (new Attack());
	}
	public Attack attack_group(Being[] toast, String[] bnames, int textlevel, Dice breath, String dragon_index) {
		return (new Attack());
	}
	public Attack attack_group_btype(Being victim, String bname, int number_of_victims, int textlevel, Dice bomb, String gnome_index, boolean tp) {
		return (new Attack());
	}
}


