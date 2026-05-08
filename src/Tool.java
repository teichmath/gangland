
public class Tool {

	String name;
	int tool_x;
	int tool_y;
	int tool_x_first_drop;
	int tool_y_first_drop;
	int owner;
	int appearance_chance;
	int find_chance;
	int time_of_last_start;
	boolean usable;
	boolean visible;
	boolean timed_appearance;
	boolean present;
	boolean obtainable;
	boolean user_knows_owner;
	boolean full;
	String contents;
	String user_finds_it_message;
	String another_finds_it_message;
	String loot_message;
	String loot_message_plural;
	String user_finds_but_doesnt_take_it_message;
	
	
	//constructors
	public Tool(String name, Dice drop, int boardside, int find_chance, String user_finds_it_message, String another_finds_it_message, String loot_message) {
		
		this(name, drop, boardside, false, 0, find_chance, user_finds_it_message, another_finds_it_message, loot_message, "", "");
	}

	public Tool(String name, Dice drop, int boardside, boolean t_app, int a_chance, int find_chance, String user_finds_it_message, String another_finds_it_message, String loot_message) {

		this(name, drop, boardside, t_app, a_chance, find_chance, user_finds_it_message, another_finds_it_message, loot_message, "", "");
	}

	public Tool(String name, Dice drop, int boardside, boolean t_app, int a_chance, int find_chance, String user_finds_it_message, String another_finds_it_message, String loot_message, String loot_message_plural) {

		this(name, drop, boardside, t_app, a_chance, find_chance, user_finds_it_message, another_finds_it_message, loot_message, loot_message_plural, "");
	}

	public Tool(String name, Dice drop, int boardside, int find_chance, String user_finds_it_message, String another_finds_it_message, String loot_message, String loot_message_plural, String user_finds_but_doesnt_take_it_message) {
	
		this(name, drop, boardside, false, 0, find_chance, user_finds_it_message, another_finds_it_message, loot_message, loot_message_plural, user_finds_but_doesnt_take_it_message);
	}

	public Tool(String name, Dice drop, int boardside, boolean t_app, int a_chance, int find_chance, String user_finds_it_message, String another_finds_it_message, String loot_message, String loot_message_plural, String user_finds_but_doesnt_take_it_message) {
		this.name = name;
		this.tool_x = -1;
		this.tool_y = -1;
		this.tool_x_first_drop = drop.throwdice(1, boardside);
		this.tool_y_first_drop = drop.throwdice(1, boardside);
		this.owner = -1;
		this.appearance_chance = a_chance;
		this.usable = true;
		this.visible = false;
		this.timed_appearance = t_app;
		this.present = false;
		this.obtainable = false;
		this.user_knows_owner = false;
		this.full = false;
		this.find_chance = find_chance;
		this.user_finds_it_message = user_finds_it_message;
		this.another_finds_it_message = another_finds_it_message;
		this.user_finds_but_doesnt_take_it_message = user_finds_but_doesnt_take_it_message;
		this.loot_message = loot_message;
		this.loot_message_plural = loot_message_plural;
		this.time_of_last_start = -1;
		this.contents = "none";
		
	}
	
	//getters
	
	public String getname() {
		return this.name;
	}
		
	public int getx() {
		return this.tool_x;
	}
	
	public int gety() {
		return this.tool_y;
	}
	
	public int getowner() {
		return this.owner;
	}
		
	public boolean is_usable() {
		if(this.usable) return true;
		else return false;
	}
	
	public boolean is_visible() {
		if(this.visible) return true;
		else return false;
	}

	public boolean is_present() {
		if(this.present) return true;
		else return false;
	}	

	public int getappearance_chance() {
		return this.appearance_chance;
	}
	
	public boolean is_timed() {
		if(this.timed_appearance) return true;
		else return false;
	}	

	public boolean is_here(int x, int y) {
		if(this.tool_x == x && this.tool_y == y) return true;
		else return false;
	}
	
	public boolean is_obtainable() {
		if(this.obtainable) return true;
		else return false;
	}

	public boolean is_full() {
		if(this.full) return true;
		else return false;
	}

	public boolean user_knows_owner() {
		if(this.user_knows_owner) return true;
		else return false;
	}
	
	public int getfind_chance() {
		return this.find_chance;
	}		 
	
	public String user_finds_it_message() {
		return this.user_finds_it_message;
	}
	
	public String another_finds_it_message() {
		return this.another_finds_it_message;
	}
	
	public String user_finds_but_doesnt_take_it_message() {
		return this.user_finds_but_doesnt_take_it_message;
	}

	public String loot_message() {
		return this.loot_message;
	}
	
	public String loot_message_plural() {
		return this.loot_message_plural;
	}
	
	public String get_contents() {
		return this.contents;
	}
	
	public boolean is_stake_planted() {
		if(this.present && !this.obtainable && this.owner == -1) return true;
		else return false;
	}
	
	public int gettime_of_last_start() {
		return this.time_of_last_start;
	}
	
	public boolean is_a_okay() {
		return(this.visible && this.present && this.obtainable && this.usable);
	}
	
	
	//setters
	
	public void start_item(int time) {
		this.tool_x = this.tool_x_first_drop;
		this.tool_y = this.tool_y_first_drop;
		this.visible = true;
		this.present = true;
		this.obtainable = true;
		this.usable = true;
		this.time_of_last_start = time;
	}
	
	public void set_drop_point(int new_x, int new_y) {
		this.tool_x_first_drop = new_x;
		this.tool_y_first_drop = new_y;
	}

	public void set_xy(int new_x, int new_y) {
		this.tool_x = new_x;
		this.tool_y = new_y;
	}
	
	public void gets_picked_up(int new_owner) {
		this.owner = new_owner;
	}
	
	public void gets_dropped(int new_x, int new_y) {
		this.owner = -1;
		this.tool_x = new_x;
		this.tool_y = new_y;
		this.user_knows_owner = false;
	}
	
	public void breaks() {
		this.usable = false;
	}
	
	public void repaired() {
		this.usable = true;
	}
	
	public void plant_stake(int new_x, int new_y) {
		this.obtainable = false;
		this.owner = -1;
		this.tool_x = new_x;
		this.tool_y = new_y;
		this.user_knows_owner = false;
	}
	
	public void pull_stake(int owner) {
		this.obtainable = true;
		this.owner = owner;
	}	
	
	public void fill(String c) {
		this.full = true;
		this.contents = c;
	}
	
	public void empty() {
		this.full = false;
		this.contents = "none";
	}
	
	//for when an item will not return.
	public void gets_lost() {
		this.visible = false;
		this.owner = -1;
	}
	
	public void reveal() {
		this.visible = true;
		this.present = true;
		this.obtainable = true;
	}
	
	public void user_learns_owner() {
		this.user_knows_owner = true;
	}
	
	
	
}