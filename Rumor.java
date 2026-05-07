
public class Rumor {

	//fields
	int humansize;
	int player_subject;
	int generation_of_player_subject;
	int best_kills_of_player_subject;
	int user;
	int user_heard;
	String tag;
	boolean gregorknows;
	boolean[] hears;
	boolean[][] knowsknows;
	boolean[] squashed;
	
	//constructor

	public Rumor(int humansize, int user, String tag, boolean gregor) {
		this(humansize, user, tag, gregor, -1, 1);
	}
	
	public Rumor(int humansize, int user, String tag, boolean gregor, int subject, int generation) {
		this.humansize = humansize;
		this.tag = tag;
		this.user = user;
		this.user_heard = 0;
		this.player_subject = subject;
		this.generation_of_player_subject = generation;
		this.best_kills_of_player_subject = 0;
		this.gregorknows = gregor;
		this.hears = new boolean[humansize];
		this.knowsknows = new boolean[humansize][humansize];
		this.squashed = new boolean[humansize];
		
		for(int i = 0; i < humansize; i++) {
			hears[i] = false;
			squashed[i] = false;
			for(int j = 0; j < humansize; j++) {
				knowsknows[i][j] = false;
			}
		}
		
	}
	
	//getters

	public String gettag() {
		return this.tag;
	}

	public boolean knows_knows(int first, int second) {
		return this.knowsknows[first][second];
	}

	public boolean has_heard(int hearer) {
		return this.hears[hearer];
	}
	
	public boolean knows_is_false(int knower) {
		return this.squashed[knower];
	}
	
	public boolean gregor_knows() {
		return this.gregorknows;
	}
	
	public boolean user_heard_enough() {
		if(this.user_heard >= 2) return true;
		else return false;
	}
	
	public int getplayer_subject() {
		return this.player_subject;
	}

	public int getgeneration_of_player_subject() {
		return this.generation_of_player_subject;
	}
	
	public int getbest_kills_of_player_subject() {
		return this.best_kills_of_player_subject;
	}

	//setters

	public void hears_rumor(int hearer) {
		this.hears[hearer] = true;
		if(hearer == user) this.user_heard++;
	}
	
	public void gregor_hears_rumor() {
		this.gregorknows = true;
	}
	
	public void now_knows_knows(int first, int second) {
		this.knowsknows[first][second] = true;
	}
	
	public void cancel_knows_knows(int first, int second) {
		this.knowsknows[first][second] = false;
	}
	
	public void now_knows_is_false(int knower) {
		this.squashed[knower] = true;
	} 

	public void forget_all(int erased) {
		this.hears[erased] = false;
		this.squashed[erased] = false;
		for(int i = 0; i < humansize; i++) {
			this.knowsknows[erased][i] = false;
		}
	}
	
	public void clear_all_for_this_soul(int erased) {
		this.hears[erased] = false;
		this.squashed[erased] = false;
		for(int i = 0; i < humansize; i++) {
			this.knowsknows[erased][i] = false;
			this.knowsknows[i][erased] = false;
		}
	}

	public void setplayer_subject(int ps) {
		this.player_subject = ps;
	}

	public void setbest_kills_of_player_subject(int bk) {
		this.best_kills_of_player_subject = bk;
	}
	
	public void squash_rumor() {
		boolean go_ahead = true;
		
		//we only let the rumor live if there's one person who knows it and believes it.
i_loop:	for(int i = 0; i < humansize; i++) {
			if(this.hears[i]) {
				if(!this.squashed[i]) {
					go_ahead = false;
					break i_loop;
				}
			}
		}
		
		if(go_ahead) {
			this.tag = "blank";
			for(int i = 0; i < humansize; i++) {
				hears[i] = false;
				squashed[i] = false;
				for(int j = 0; j < humansize; j++) {
					knowsknows[i][j] = false;
				}
			}			
		}
	}



}
