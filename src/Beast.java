
public class Beast extends Being {

	String name_plural;
	int[] att_speed;
	int strengthadj;
	int skill_bonus_from_hit;
	int value;
	String sound_of_pain;
	String[] kill_sounds;
	String[] kill_sounds_for_dogs;
	String[] kill_sounds_for_cats;
	String singular_victory_message;
	String plural_victory_message;
	int run_chance; 
	
	//getters
	
	public int getspeedadj(int att) {
		//if(att < this.att_speed.size) 
		return this.att_speed[att];
		//else return 0;
	}
	
	public int getstrengthadj() {
		return this.strengthadj;
	}
	
	public int getmoraleadj() {
		return -5;
	}

	public String getspecies() {
		return this.name;
	}
	
	public boolean has_name() {
		return false;
	}
	
	public String getname_plural() {
		return this.name_plural;
	}

	public String getsound_of_pain() {
		return this.sound_of_pain;
	}
	
	public String getkill_sound(int x, String type) {
		if(type.compareToIgnoreCase("dog") == 0) {
			if(x < 3) return this.kill_sounds_for_dogs[x];
			else return this.kill_sounds_for_dogs[0];
		}
		if(type.compareToIgnoreCase("cat") == 0) {
			if(x < 3) return this.kill_sounds_for_cats[x];
			else return this.kill_sounds_for_cats[0];
		}
		else {
			if(x < 3) return this.kill_sounds[x];
			else return this.kill_sounds[0];
		}
	}
	
	public String getvictory_message(int agreement) {
		if(agreement == 1) return this.singular_victory_message;
		else return this.plural_victory_message;
	}
	
	public int getnumber_of_attacks() {
		return this.number_of_attacks;
	}
	
	public String toString() {
		return (this.name+"\nStrength: "+this.strength+" Health: "+this.health);
	}
	
	public int getskill_bonus_from_hit() {
		return this.skill_bonus_from_hit;
	}
	
	public int getvalue() {
		return this.value;
	}
	
	public boolean it_might_run() {
		if(this.run_chance == 0) return false;
		else return true;
	}
	
	public int getrun_chance() {
		return this.run_chance;
	}

	//setters
	
	public void setxy(int newx, int newy) {
		this.x_position = newx;
		this.y_position = newy;
	}


}
		