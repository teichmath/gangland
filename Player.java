
public class Player extends Being {

	int speed;
	int personality;
	int loyalty;
	int affiliation;
	int potential_affiliation;
	int age;
	int[] former_affiliations;
	int[] friend;
	int[] encounters_this_round;
	int[] time_of_meeting;
	int morale;
	int weaponskill;
	int generation;
	int potential_x_position;
	int potential_y_position;
	int last_x_position;
	int last_y_position;
	int round_clock;
	int recent_hit_clock;
	int house_clock;
	int gregor_clock;
	int forest_entry_clock;
	int kills;
	int burial_meter;
	int gang_we_asked_to_merge;
	int last_longhouse_visit;
	int boardside;
	int rounds_in_place;
	int recent_eat_brains_clock;
	int brains_hunger;
	int resurrection_clock;
	int forest_spirit_credits;
	int[] forest_entry_perceived;
	int[] forest_exit_perceived;
	boolean was_mourned;
	boolean just_arrived;
	boolean just_hatched;
	boolean[] zombie_meetings;
	boolean blessed;
	boolean cursed;
	boolean recent_burial_attempt;
	boolean left_forest_need_message;
	boolean[][] forest_perceived;
	boolean zombie;
	boolean zombie_potential;
	boolean leader;
	boolean sword_drawn;
	boolean poison_damage_this_round;
	String bird_report;
	String[] soundeffect;
	

	//getters

	public int getstat_for_uncertainty(int which_stat) {
		int stat_to_return = 0;
		if(which_stat == 0) stat_to_return = this.strength;
		if(which_stat == 1) stat_to_return = this.health;
		if(which_stat == 2) stat_to_return = this.maxhealth;
		if(which_stat == 3) stat_to_return = this.speed;
		if(which_stat == 4) stat_to_return = this.weaponskill;
		if(which_stat == 5) stat_to_return = this.personality;
		if(which_stat == 6) stat_to_return = this.morale;

		return stat_to_return;

	}

	public int getspeed() {
		return this.speed;
	}
	
	public int getspeedadj(int att) {
		return this.speed - 5;
	}
	
	public int getstrengthadj() {
		int strengthadj = 3;
		if(this.strength < 18) strengthadj = 2;
		if(this.strength < 16) strengthadj = 1;
		if(this.strength < 14) strengthadj = 0;
		if(this.strength < 8) strengthadj = -1;
		if(this.strength < 6) strengthadj = -2;
		if(this.strength < 4) strengthadj = -3;
		if(this.strength < 3) strengthadj = strengthadj - (int)((3-this.strength)/2 + .5);
		if(this.strength > 18) strengthadj = strengthadj + (int)((this.strength - 18)/2 + .5);
		if(this.blessed) strengthadj = strengthadj + 5;
		return strengthadj;
	}
	
	public int getgeneration() {
		return this.generation;
	}
	
	public int getpersonality() {
		return this.personality;
	}
	
	public int getloyalty() {
		return this.loyalty;
	}
	
	public int getaffiliation() {
		return this.affiliation;
	}

	public int getpotential_affiliation() {
		return this.potential_affiliation;
	}
		
	public int getmorale() {
		return this.morale;
	}
	
	public int getformer_affiliation(int form_aff_array_place_to_look_up) {
		if(form_aff_array_place_to_look_up < this.former_affiliations.length) return this.former_affiliations[form_aff_array_place_to_look_up];
		else return -1;
	}
	
	public int getfriend(int friend_to_look_up) {
		if(friend_to_look_up < this.friend.length) return this.friend[friend_to_look_up];
		else return -1;
	}
	
	public int times_saw(int friend_to_look_up) {
		if(friend_to_look_up < this.encounters_this_round.length) return this.encounters_this_round[friend_to_look_up];
		else return 0;
	}
	
	public int gettime_of_meeting(int person) {
		if(person < this.time_of_meeting.length) return this.time_of_meeting[person];
		else return -1;
	}
	
	public int getweaponskill() {
		return this.weaponskill;
	}
	
	public int getpotential_x() {
		return this.potential_x_position;
	}
	
	public int getpotential_y() {
		return this.potential_y_position;
	}
	
	public int getlast_x() {
		return this.last_x_position;
	}

	public int getlast_y() {
		return this.last_y_position;
	}

	public int getround_clock() {
		return this.round_clock;
	}

	public int gethouse_clock() {
		return this.house_clock;
	}
	
	public int getgregor_clock() {
		return this.gregor_clock;
	}
	
	public int getage() {
		return this.age;
	}
	
	public int getkills() {
		return this.kills;
	}
	
	public int getlast_longhouse_visit() {
		return this.last_longhouse_visit;
	}
	
	public int getlast_gang() {
		int last_gang = 0;
		for(int i = 0; i< 100; i++) {
			if (this.former_affiliations[i]==0) {
				if(i == 0) break;
				else {
					last_gang = this.former_affiliations[i - 1];
					break;
				}
			}
			if(i == 99 && last_gang == 0) last_gang = this.former_affiliations[i];
		}
		return last_gang;
	}
	
	public int how_buried() {
		return this.burial_meter;
	}
	
	public int getgang_we_asked_to_merge() {
		return this.gang_we_asked_to_merge;
	}
	
	public int getforest_entry_perceived_x() {
		return this.forest_entry_perceived[0];
	}
	
	public int getforest_entry_perceived_y() {
		return this.forest_entry_perceived[1];
	}

	public int getforest_exit_perceived_x() {
		return this.forest_exit_perceived[0];
	}

	public int getforest_exit_perceived_y() {
		return this.forest_exit_perceived[1];
	}
	
	public int getforest_entry_clock() {
		return this.forest_entry_clock;
	}

	public int getnumber_of_forest_spirit_credits() {
		return this.forest_spirit_credits;
	}
	
	public int getrounds_in_place() {
		return this.rounds_in_place;
	}
	
	public int getbrains_hunger() {
		return this.brains_hunger;
	}
	
	public boolean getforest_perceived(int x, int y) {
		boolean result = false;
		if(x>=0 && x<=5 && y>=0 && y<=5) result = this.forest_perceived[x][y];
		return result;
	}
	
	public boolean[][] getwhole_forest_perceived() {
		return forest_perceived;
	}

	public int resurrection_clock() {
		return this.resurrection_clock;
	}
	
	public boolean is_going_here(int x, int y) {
		boolean result = false;
		if(x == this.potential_x_position && y == this.potential_y_position) result = true;
		return result;
	}

	public boolean knows_a_zombie(int player) {
		return this.zombie_meetings[player];
	}
	
	public boolean was_mourned() {
		if(this.was_mourned) return true;
		else return false;
	}
	
	public boolean just_arrived() {
		if(this.just_arrived) return true;
		else return false;
	}
	
	public boolean is_blessed() {
		if(this.blessed) return true;
		else return false;
	}
	
	public boolean is_cursed() {
		if(this.cursed) return true;
		else return false;
	}
	
	public boolean recent_burial_attempt() {
		if(this.recent_burial_attempt) return true;
		else return false;
	}
	
	public boolean need_forest_message() {
		if(this.left_forest_need_message) return true;
		else return false;
	}

	public boolean is_zombie() {
		if(this.zombie) return true;
		else return false;
	}

	public boolean has_symbol() {
		if(this.zombie_potential) return true;
		else return false;
	}
	
	public boolean was_poison_damaged_this_round() {
		if(this.poison_damage_this_round) return true;
		else return false; 
	}
	
	public boolean in_the_forest() {
		//if(this.x_position < this.boardside + 3 || this.y_position < this.boardside + 3) this.in_the_forest = false;
		if(this.in_the_forest) return true;
		else return false;
	} 

	//this is a getter for a field that actually doesn't exist.
	public int getmoraleadj() {
		if(this.morale == 0) return -3;
		else if(this.morale >= 1 && this.morale <= 3) return -2;
		else if(this.morale >= 4 && this.morale <= 8) return -1;
		else if(this.morale >= 32 && this.morale <= 36) return 1;
		else if(this.morale >= 37 && this.morale <= 39) return 2;
		else if(this.morale == 40) return 3;
		else return 0;
	}
	
	//same here.
	public double getevaluation() {
		double evaluation = ( (this.weaponskill*2)*.4  +  (this.speed-2)*(100/3)*.3  +  (this.strength-3)*(100/9)*.2  +  (this.maxhealth - 10)*(100/30)*.1 );
		return evaluation;
	}
		
	public String toString() {
		String reportstring = this.name;
		if(this.zombie) {
			reportstring = reportstring + " (UNDEAD)";
		}
		reportstring = reportstring +"\nStrength: "+this.strength+" Health: "+this.health+"(Max "+this.maxhealth+") Speed: "+this.speed+"\n Skill: "+this.weaponskill+" Personality: "+this.personality+" Morale: "+this.morale+"\n Affiliation: "+this.affiliation+"  Round Clock: "+this.round_clock;
		return reportstring;
	}
	
	public String longreport() {
		String reportstring = this.name;
		if(this.zombie) {
			reportstring = reportstring + " (UNDEAD)";
		}
		reportstring = reportstring +"       Strength: "+this.strength+" Health: "+this.health+"(Max "+this.maxhealth+") Speed: "+this.speed+" \n          Skill: "+this.weaponskill+" Personality: "+this.personality+" Morale: "+this.morale; 
		return reportstring;
	}	
	
	
	//setters
	
	public void setname(String newname) {
		this.name = newname;
	}
	
	public void setpersonality(int newpers) {
		this.personality = newpers;
	}	
	
	public void super_power_up() {
		this.strength = 18;
		this.speed = 8;
		this.weaponskill = this.weaponskill + 40;
		this.maxhealth = this.maxhealth + 20;
	}
	
	public void setaffiliation(int newaff) {
		for(int i=0; i<100; i++) {
			if (this.former_affiliations[i]==0) {
				this.former_affiliations[i] = this.affiliation;
				break;
			}
		}
		this.affiliation = newaff;
		this.potential_affiliation = 0;
		if(newaff == 0) this.leader = false;
	}
	
	public void setpotential_affiliation(int newaff) {
		this.potential_affiliation = newaff;
	}
	
	public void update_affiliation() {
		this.affiliation = this.potential_affiliation;
		this.potential_affiliation = 0;
	}
	
	public void setlast_longhouse_visit(int last) {
		this.last_longhouse_visit = last;
	}
	
	public void setlast_gang(int exgang) {
		for(int i=0; i<100; i++) {
			if (this.former_affiliations[i]==0) {
				this.former_affiliations[i] = exgang;
				break;
			}
		}
	}
	
	public void setmorale(int new_morale) {
		this.morale = new_morale;
		if(this.morale < 0) this.morale = 0;
		if(this.morale > 40) this.morale = 40;
	}		
	
	public void changemorale(int delta) {
		this.morale = this.morale + delta;
		if(this.morale < 0) this.morale = 0;
		if(this.morale > 40) this.morale = 40;
	}
	
	public void driftmorale() {
		if(this.morale > 20) this.morale--;
		if(this.morale < 20 && !this.poisoned) this.morale++;
	}
	
	public void tick_rounds_in_place() {
		this.rounds_in_place++;
	}
	
	public void recover() {
		if(this.health < this.maxhealth && this.health > 0 && !this.zombie && !this.poisoned) {
			this.health++;
			if(this.blessed) this.health++;
		}
		if(this.health > this.maxhealth) this.health = this.maxhealth;
	}
	
	public void camprecover() {
		if(this.health < this.maxhealth && this.health > 0 && !this.zombie && !this.poisoned) {
			this.health += 3;
			if(this.blessed) this.health += 2;
		}
		if(this.health > this.maxhealth) this.health = this.maxhealth;
	}

	public void houserecover() {
		if(this.health > 0) {
			this.health += 5;
			if(this.blessed) this.health += 3;
		}
		if(this.health > this.maxhealth) this.health = this.maxhealth;
	}
	
	public void heal_a_bit() {
		if(this.health < this.maxhealth && this.health > 0 && !this.zombie && !this.poisoned) {
			this.health += 5;
			if(this.blessed) this.health += 3;
		}
		if(this.health > this.maxhealth) this.health = this.maxhealth;
	}	
	
	public void full_heal() {
		this.health = this.maxhealth;
		this.poisoned = false;
	}
			
	public void battlescar(int scar) {
		if(scar > 0 && !this.zombie) this.maxhealth = this.maxhealth + 1 + (int)(scar/6);
	}
	
	public void do_poison_damage(Dice dice) {
		int damage = get_poison_damage(dice);
		ouch(damage);
		this.poison_damage_this_round = true;
	}
	
	public void gainskill(int delta) {
		if(!this.zombie) {
			this.weaponskill = this.weaponskill + delta;
			this.recent_hit_clock = 0;
			if(this.weaponskill < 0) this.weaponskill = 0;
		}
	}
	
	public void forgetskill() {
		if(!this.zombie) {
			if(this.recent_hit_clock >= 12){
				this.weaponskill--;
				if(this.weaponskill < 0) this.weaponskill = 0;
				this.recent_hit_clock = 0;
			}
			else this.recent_hit_clock++;
		}
	}
	
	public void changestrength(int delta) {
		this.strength = this.strength + delta;
	}
	
	public void changespeed(int delta) {
		this.speed = this.speed + delta;
	}

	public void changemaxhealth(int delta) {
		this.maxhealth = this.maxhealth + delta;
		if(this.maxhealth < 5) this.maxhealth = 5;
		if(this.maxhealth < this.health) this.health = this.maxhealth;
	}

	public void x_change_simple(int x) {
		this.x_position = x;
	}

	public void y_change_simple(int y) {
		this.y_position = y;
	}

	public void setxy(int newx, int newy) {
		if(newx != x_position || newy != y_position) {
			this.last_x_position = this.x_position;
			this.last_y_position = this.y_position;
			this.potential_x_position = newx;
			this.potential_y_position = newy;
			this.round_clock = 100;
			this.x_position = 0;
			this.y_position = 0;
			this.rounds_in_place = 0;
		}
	}
	
	public void setxyquick(int newx, int newy) {
		this.last_x_position = this.x_position;
		this.last_y_position = this.y_position;
		this.potential_x_position = newx;
		this.potential_y_position = newy;
		this.round_clock = 20;
		if(newx != this.x_position || newy != this.y_position) rounds_in_place = 0;
		this.x_position = newx;
		this.y_position = newy;
	}
	
	public void redirect(int x, int y) {
		this.potential_x_position = x + this.last_x_position;
		this.potential_y_position = y + this.last_y_position;
	}

	public void revertxy() {
		if(this.x_position != this.last_x_position || this.y_position != this.last_y_position) rounds_in_place = 0;
		this.x_position = this.last_x_position;
		this.y_position = this.last_y_position;
		this.potential_x_position = 0;
		this.potential_y_position = 0;
	}
		
	public void changexy(int xdelta, int ydelta) {

		if(xdelta != 0 || ydelta != 0) {
			boolean moved = true;
			this.last_x_position = this.x_position;
			this.last_y_position = this.y_position;
			this.potential_x_position = this.x_position + xdelta;
			this.potential_y_position = this.y_position + ydelta;
			if(this.potential_x_position < 1 || this.potential_x_position == 11 || this.potential_y_position < 1 || this.potential_y_position == 11) {
				this.potential_x_position = 0;
				this.potential_y_position = 0;
				moved = false;
			}
			if(moved) {
				this.round_clock = 100;
				this.x_position = 0;
				this.y_position = 0;
				rounds_in_place = 0;
			}
		}
	}
	
	public void changexyoffboard(int xdelta, int ydelta) {

		if(xdelta != 0 || ydelta != 0) {
			this.last_x_position = this.x_position;
			this.last_y_position = this.y_position;
			this.potential_x_position = this.x_position + xdelta;
			this.potential_y_position = this.y_position + ydelta;
			this.round_clock = 100;
			this.x_position = 0;
			this.y_position = 0;
			rounds_in_place = 0;
		}
	}
	
	
	public boolean lastxy_was(int formerx, int formery) {
		boolean outcome = false;
		if(this.x_position == 0 && this.y_position == 0) {
			if(last_x_position == formerx && last_y_position == formery) outcome = true;
		}
		return(outcome);
	}
	
	public void setgang_we_asked_to_merge(int gangnumber) {	
		this.gang_we_asked_to_merge = gangnumber;
	}
	
	public void setround_clock(int newtime) {
		this.round_clock = newtime;
	}
	
	public void changeround_clock(int delta) {
		this.round_clock = this.round_clock + delta;
	}
	
	public void setforest_entry_clock(int newtime) {
		this.forest_entry_clock = newtime;
	}
	
	public void changeforest_entry_clock(int delta) {
		this.forest_entry_clock += delta;
	}
	
	public void zeroround_clock() {
		this.round_clock = 0;
		if(this.just_arrived) {
			this.just_arrived = false;
		}
		if(this.potential_x_position > 0) {
			this.x_position = this.potential_x_position;
			this.just_arrived = true;
		}
		if(this.potential_y_position > 0) {
			this.y_position = this.potential_y_position;
			this.just_arrived = true;
		}
		this.potential_x_position = 0;
		this.potential_y_position = 0;
		this.gang_we_asked_to_merge = 0;
		if(this.just_hatched) {
			this.just_arrived = true;
			this.just_hatched = false;
		}
		if(this.recent_burial_attempt) this.recent_burial_attempt = false;		
		this.poison_damage_this_round = false;
	}
	
	public void sethouse_clock(int new_time) {
		this.house_clock = new_time;
	}
	
	public void changehouse_clock(int delta) {
		this.house_clock = this.house_clock + delta;
		if(this.house_clock < 0) this.house_clock = 0;
	}

	public void setgregor_clock(int new_time) {
		this.gregor_clock = new_time;
		if(this.gregor_clock < 0) this.gregor_clock = 0;
	}
	
	public void changegregor_clock(int delta) {
		this.gregor_clock = this.gregor_clock + delta;
		if(this.gregor_clock < 0) this.gregor_clock = 0;
	}
	
	public void getolder() {
		this.age++;
	}
	
	public void didnt_just_arrive() {
		this.just_arrived = false;
		this.just_hatched = false;
	}
	
	public void kill_player() {
		this.freshly_dead = true;
	}
	
	public void mourn_player() {
		this.was_mourned = true;
	}
	
	public void lay_player_to_rest() {
		this.freshly_dead = false;
		this.affiliation = 0;
	}
	
	public void receive_curse() {
		this.cursed = true;
	}
	
	public void meet_zombie(int player) {
		if(player < this.zombie_meetings.length) this.zombie_meetings[player] = true;
	}		
	
	public void reset_zombie_meeting(int player) {
		if(player < this.zombie_meetings.length) this.zombie_meetings[player] = false;
	}
	
	public void zombie_slayer_power_up() {
		this.blessed = true;
		this.cursed = false;
	}	

	public void carve_forehead() {
		this.zombie_potential = true;
	}

	public void end_zombie() {
		this.zombie = false;
		this.zombie_potential = false;
		this.health = 0;
		this.freshly_dead = true;
	}

	public void get_hungrier() {
		if(this.recent_eat_brains_clock >= 20) { 
			this.brains_hunger--;
			if(this.brains_hunger < -5) this.brains_hunger = -5;
			this.recent_eat_brains_clock = 0;
		}
		else this.recent_eat_brains_clock++;
	}

	public void setresurrection_clock(int new_time) {
		this.resurrection_clock = new_time;
		if(this.resurrection_clock < 0) this.resurrection_clock = 0;
	}
		
	public void changeresurrection_clock(int delta) {
		this.resurrection_clock = this.resurrection_clock + delta;
		if(this.resurrection_clock < 0) this.resurrection_clock = 0;
	}

	
	public void setfriend(int friend_position, int friend_new_value, int time, int codeline) {
		if(friend_position < this.friend.length && friend_position < this.time_of_meeting.length && friend_position > -1) {
			if(this.friend[friend_position] == -1 && friend_new_value > -1) this.time_of_meeting[friend_position] = time;
			//delme
//			if(this.friend[friend_position] > -1 && friend_new_value <= -1 && this.array_pos == 50) System.out.println("**** 682 Player.java, from "+codeline+" in Gangland.java:  changing friend "+friend_position+" from "+this.friend[friend_position]+" to "+friend_new_value);
			this.friend[friend_position] = friend_new_value;
			if(friend_new_value == -1) this.time_of_meeting[friend_position] = -1;
		}
	}
	
	public void changefriend(int friend_position, int delta) {
		if(friend_position < this.friend.length) {
			//delme
			//if(this.friend[friend_position] + delta < 0) System.out.println("**** 690 Player.java, from "+codeline+" in Gangland.java: changing friend "+friend_position+" from "+this.friend[friend_position]+" to "+this.friend[friend_position]+" + "+delta);
			this.friend[friend_position] += delta;
			if(this.friend[friend_position] <  0) this.friend[friend_position] = 0;
			if(this.friend[friend_position] >  99) this.friend[friend_position] = 99;
		}
	}
	
	public void add_encounters(int friend, int delta) {
		if(friend < this.encounters_this_round.length) {
			this.encounters_this_round[friend] = this.encounters_this_round[friend] + delta;
			if(this.encounters_this_round[friend] < 0) this.encounters_this_round[friend] = 0;
		}
	}
	
	public void zeroencounters() {
		for(int i=0; i< this.encounters_this_round.length; i++){
			this.encounters_this_round[i] = 0;
		}
	}
	
	public void settime_of_meeting(int person, int time) {
		if(person < this.time_of_meeting.length) this.time_of_meeting[person] = time;
	}
	
	public void beltnotch() {
		this.kills++;
	}
	
	public void get_buried(int dirt) {
		this.burial_meter = this.burial_meter - dirt;
		this.recent_burial_attempt = true;
	}
	
	public void perceive_forest_anew(boolean[][] forest_perceived_incoming, int entry_point_x, int entry_point_y, int exit_point_x, int exit_point_y) {
		this.forest_perceived = forest_perceived_incoming;
		this.forest_entry_perceived[0] = entry_point_x;
		this.forest_entry_perceived[1] = entry_point_y;
		this.forest_exit_perceived[0] = exit_point_x;
		this.forest_exit_perceived[1] = exit_point_y;
		this.in_the_forest = true;
	}
	
	public void set_in_the_forest_true() {
		this.in_the_forest = true;
	}
	
	public void leave_forest() {
		this.left_forest_need_message = true;
		this.bird_report = "";
	}		

	public void complete_leaving_forest() {
		this.left_forest_need_message = false;
		this.in_the_forest = false;
	}
	
	public void setnumber_of_forest_spirit_credits(int value) {
		this.forest_spirit_credits = value;
	}
	
	public void add_forest_spirit_credit() {
		this.forest_spirit_credits++;
	}

	
	//player attack methods
	
	public int attack_player(Being target, String aname, Dice swing) {
		return attack(target, null, aname, "", swing);
	}
			
	public int attack_beast(Being target, String aname, Dice swing) {
		return attack(null, target, "", aname, swing);
	}
	
	public String attack_sounds(Dice swing) {
		int soundroll = swing.throwdice(1,5) - 1;
		return(soundeffect[soundroll]);
	}
	
	public int attack(Being t1, Being t2, String aname, String bname, Dice swing) {
		
		int attacker_bonus;
		if(this.morale == 0) attacker_bonus = -3;
		else if(this.morale >= 1 && this.morale <= 3) attacker_bonus = -2;
		else if(this.morale >= 4 && this.morale <= 8) attacker_bonus = -1;
		else if(this.morale >= 32 && this.morale <= 36) attacker_bonus = 1;
		else if(this.morale >= 37 && this.morale <= 39) attacker_bonus = 2;
		else if(this.morale == 40) attacker_bonus = 3;
		else attacker_bonus = 0;
		attacker_bonus += (int)(this.weaponskill/10);
		if(this.blessed) attacker_bonus += 2;
		int hitroll = swing.throwdice(1,20);
		int tohit;
		if(t1 == null) tohit = t2.getdefense() - attacker_bonus - this.getspeedadj(0);
		else tohit = t1.getdefense() + t1.getmoraleadj() - attacker_bonus - this.getspeedadj(0);
		if(tohit <= 1) tohit = 2;
		if(hitroll >= tohit || hitroll == 20) {
//			Wait.Blip();
			int damage = swing.throwdice(1,6) + getstrengthadj() + attacker_bonus;
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
		