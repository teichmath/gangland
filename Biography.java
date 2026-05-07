
public class Biography {

	int subject_array_number;
	int user_array_number;
	String name;
	int turn_met;
	int turn_end;
	int jokes_heard;
	int jokes_told;
	int date_of_death;
	boolean activated;
	boolean death;
	boolean formed_new_gang;
	boolean initial_gang_rejection;
	int last_turn;
	int last_seen;
	int taunts_heard;
	int taunts_told;
	int threats_heard;
	int threats_told;
	int clarity;
	boolean dueled_for_leader;
	boolean you_dueled;
	boolean you_challenged;
	boolean challenged_you;
	boolean you_joined;
	boolean joined_you;
	boolean you_invited;
	boolean invited_you;
	boolean you_asked_to_join;
	boolean asked_to_join_you;
	boolean you_proposed_gang;
	boolean proposed_gang_to_you;
	boolean unfriendly;
	boolean you_abandoned;
	boolean abandoned_you;
	boolean you_deserted;
	boolean deserted_you;
	boolean gangs_no_merge;
	boolean gangs_merged;
	boolean attacked_you_same_gang;
	boolean attacked_you;
	boolean you_attacked_same_gang;
	boolean you_attacked;
	boolean you_slew;
	boolean slew_same_gang;
	boolean slain_by_player;
	String  slayer;
	boolean slain_by_beast;
	boolean you_conceded;
	boolean conceded_to_you;
	boolean you_bested;
	boolean bested_you;
	boolean you_ran_from_duel;
	boolean ran_from_duel;
	boolean you_challenged_alone;
	boolean challenged_you_alone;
	boolean you_were_voted_out;
	boolean was_voted_out;
	boolean you_rejected;
	boolean rejected_you;
	boolean you_scoffed;
	boolean scoffed_at_you;
	boolean you_rejected_personally;
	boolean met_in_battle;
	boolean gang_attacked_gang;
	boolean you_attacked_gang;
	boolean gang_attacked_you;
	boolean your_gang_attacked;
	boolean attacked_your_gang;
	
	
	//constructor
	public Biography(String name, int user) {
		this.name = name;
		this.user_array_number = user;
		this.subject_array_number = -1;
		this.turn_met = -1;
		this.last_turn = -1;
		this.last_seen = -1;
		this.activated = false;
		this.death = false;
		this.taunts_heard = 0;
		this.taunts_told = 0;
		this.threats_heard = 0;
		this.threats_told = 0;
		this.jokes_heard = 0;
		this.jokes_told = 0;
		this.clarity = 0;
		this.formed_new_gang = false;
		this.dueled_for_leader = false;
		this.you_dueled = false;
		this.you_challenged = false;
		this.challenged_you = false;
		this.you_joined = false;
		this.joined_you = false;
		this.you_invited = false;
		this.invited_you = false;
		this.you_asked_to_join = false;
		this.asked_to_join_you = false;
		this.you_proposed_gang = false;
		this.proposed_gang_to_you = false;
		this.unfriendly = false;
		this.you_abandoned = false;
		this.abandoned_you = false;
		this.you_deserted = false;
		this.deserted_you = false;
		this.gangs_no_merge = false;
		this.gangs_merged  = false;
		this.attacked_you_same_gang = false;
		this.attacked_you = false;
		this.you_attacked_same_gang = false;
		this.you_attacked = false;
		this.you_slew = false;
		this.slew_same_gang = false;
		this.slain_by_player = false;
		this.slayer = "";
		this.date_of_death = -1;
		this.slain_by_beast = false;
		this.you_conceded = false;
		this.conceded_to_you = false;
		this.you_dueled = false;
		this.you_bested = false;
		this.bested_you = false;
		this.you_ran_from_duel = false;
		this.ran_from_duel = false;
		this.you_challenged_alone = false;
		this.challenged_you_alone = false;
		this.you_were_voted_out = false;
		this.was_voted_out = false;
		this.you_rejected = false;
		this.rejected_you = false;
		this.you_scoffed = false;
		this.scoffed_at_you = false;
		this.you_rejected_personally = false;
		this.met_in_battle = false;
		this.gang_attacked_gang = false;
		this.you_attacked_gang = false;
		this.gang_attacked_you = false;
		this.your_gang_attacked = false;
		this.attacked_your_gang = false;
	
	}
	
	
	//read the biography
	
	public String readBio(int current_turn) {
	
		String bio_body_zero = "";
		String bio_body_one = "";
		String bio_body_two = "";
		String tauntthreattext = "";
		
		int tauntthreat = 1;
		if(this.taunts_heard > 0 && this.taunts_heard <= 3) tauntthreat = tauntthreat * 2;
		if(this.taunts_heard > 3) tauntthreat = tauntthreat * 3;
		if(this.taunts_told > 0 && this.taunts_told <= 3) tauntthreat = tauntthreat * 5;
		if(this.taunts_told > 3) tauntthreat = tauntthreat * 7;
		if(this.threats_heard > 0 && this.threats_heard <= 3) tauntthreat = tauntthreat * 11;
		if(this.threats_heard > 3) tauntthreat = tauntthreat * 13;
		if(this.threats_told > 0 && this.threats_told <= 3) tauntthreat = tauntthreat * 17;
		if(this.threats_told > 3) tauntthreat = tauntthreat * 19;
		
		if(tauntthreat == 2) tauntthreattext = "he taunts you sometimes... ";
		if(tauntthreat == 3) tauntthreattext = "he has taunted you quite a bit... ";
		if(tauntthreat == 5) tauntthreattext = "you taunt him sometimes... ";
		if(tauntthreat == 7) tauntthreattext = "you've taunted him quite a bit... ";
		if(tauntthreat == 11) tauntthreattext = "he threatens you sometimes... ";
		if(tauntthreat == 13) tauntthreattext = "he has threatened you quite a bit... ";
		if(tauntthreat == 17) tauntthreattext = "you threaten him sometimes... ";
		if(tauntthreat == 19) tauntthreattext = "you've threatened him quite a bit... ";
		if(tauntthreat == 10) tauntthreattext = "you've taunted each other on occasion... ";
		if(tauntthreat == 14) tauntthreattext = "he taunts you sometimes, although you've taunted him more... ";
		if(tauntthreat == 22 || tauntthreat == 33) tauntthreattext = "he has taunted you before, and sometimes threatened you... ";
		if(tauntthreat == 26) tauntthreattext = "he taunts you sometimes, although he seems to prefer outright threats... ";
		if(tauntthreat == 34) tauntthreattext = "he taunts you sometimes, and you've threatened him before... ";
		if(tauntthreat == 38 || tauntthreat == 51 || tauntthreat == 57) tauntthreattext = "you've threatened him, but he often just taunts you, unconcerned... ";
		if(tauntthreat == 15) tauntthreattext = "you've both taunted each other... ";
		if(tauntthreat == 21) tauntthreattext = "you're in some kind of a taunting war with him... ";
		if(tauntthreat == 39) tauntthreattext = "he's always either taunting you or threatening you... ";
		if(tauntthreat == 55 || tauntthreat == 65 || tauntthreat == 77 || tauntthreat == 91) tauntthreattext = "he's threatened you before, perhaps annoyed by your taunting... ";
		if(tauntthreat == 85 || tauntthreat == 119 || tauntthreat == 133) tauntthreattext = "you've taunted and threatened him mercilessly... ";
		if(tauntthreat == 11*17 || tauntthreat == 11*19 || tauntthreat == 13*17 || tauntthreat == 13*19) tauntthreattext = "you've both been at each other's throats for some time... ";
		if(tauntthreattext.compareToIgnoreCase("") == 0 && tauntthreat > 1) tauntthreattext = "you've both taunted and threatened each other like nobody's business... ";
		
		bio_body_zero += tauntthreattext;
				
		if(this.unfriendly && bio_body_zero.compareToIgnoreCase("") == 0) {
			bio_body_zero += "there's been some unfriendliness... ";
			bio_body_one += "there was some unfriendliness between you... ";
		}
		
		if(bio_body_zero.compareToIgnoreCase("") != 0 && (this.attacked_you || this.you_attacked || this.attacked_you_same_gang || this.you_attacked_same_gang)) bio_body_zero += "in fact ";
		if(this.attacked_you || this.attacked_you_same_gang) bio_body_zero = bio_body_zero+ "he attacked you at least once... ";
		if(this.you_attacked || this.you_attacked_same_gang) bio_body_zero= bio_body_zero+ "you attacked him at least once... ";
		if(this.attacked_you_same_gang || this.you_attacked_same_gang) bio_body_zero= bio_body_zero+ "you were in the same gang at the time... ";
		
		if(this.attacked_you || this.attacked_you_same_gang || this.you_attacked || this.you_attacked_same_gang || this.you_dueled) {
			bio_body_one += "you may have fought him... ";
			bio_body_two += "you think you probably were enemies, in a past life... ";
		}
				
		if(this.formed_new_gang) bio_body_zero= bio_body_zero+ "the two of you once decided to form a new gang... ";
		if(this.dueled_for_leader) bio_body_zero= bio_body_zero+ "there was a duel once between you, to see who would be leader of your gang... ";
		if(this.you_dueled && !this.dueled_for_leader) bio_body_zero= bio_body_zero+ "you remember that you've dueled with him over something... ";
		if(this.you_challenged && !this.challenged_you) bio_body_zero= bio_body_zero+ "you yourself drew your sword against him, it seems... ";
		if(this.challenged_you && !this.you_challenged) bio_body_zero= bio_body_zero+ "you recall that he drew his sword against you... ";
		if(this.you_challenged && this.challenged_you) bio_body_zero= bio_body_zero+ "at least once, each of you has drawn his sword against the other... ";
		if(this.you_ran_from_duel) bio_body_zero= bio_body_zero+ "you actually ran from the duel... ";
		if(!this.you_ran_from_duel && this.ran_from_duel) bio_body_zero= bio_body_zero+ "he actually ran from the duel... ";
		
		if(this.you_joined && this.you_asked_to_join) bio_body_zero= bio_body_zero+ "you asked him if you could join up with his gang, and they all agreed... ";
		if(this.you_joined && !this.you_asked_to_join && this.invited_you) bio_body_zero= bio_body_zero+ "he brought you into his gang... ";
		if(this.you_joined && !this.you_asked_to_join && !this.invited_you) bio_body_zero= bio_body_zero+ "you joined his gang, but it was through someone else... ";
		if(this.joined_you && this.asked_to_join_you) bio_body_zero= bio_body_zero+ "he asked you if he could join up with your gang, and they all agreed... ";
		if(this.joined_you && !this.asked_to_join_you && this.you_invited) bio_body_zero= bio_body_zero+ "you brought him into your gang... ";
		if(this.joined_you && !this.asked_to_join_you && !this.you_invited) bio_body_zero= bio_body_zero+ "he joined your gang, but it was through someone else... ";
		
		if(this.rejected_you && this.you_asked_to_join && !this.you_were_voted_out && !this.scoffed_at_you) bio_body_zero= bio_body_zero+ "you asked him if you could join up with his gang, but he wasn't keen on the idea... ";
		if(this.rejected_you && this.you_asked_to_join && !this.you_were_voted_out && this.scoffed_at_you) bio_body_zero= bio_body_zero+ "you asked him if you could join up with his gang, but he scoffed at you... ";
		if(this.you_asked_to_join && this.you_were_voted_out) bio_body_zero= bio_body_zero+ "you asked him if you could join up with his gang, and he seemed okay with it, but his gangfellows said no... ";
		if(this.you_rejected && this.asked_to_join_you && !this.was_voted_out) bio_body_zero= bio_body_zero+ "he asked you if he could join up with your gang, but you waved him off... ";
		if(this.asked_to_join_you && this.was_voted_out && !this.you_rejected_personally) bio_body_zero= bio_body_zero+ "he attempted to join your gang, but he was rejected in a vote... ";
		if(this.asked_to_join_you && this.was_voted_out && this.you_rejected_personally) bio_body_zero= bio_body_zero+ "he attempted to join your gang, but he was rejected in a vote, with you among the naysayers... ";
		
		if(this.formed_new_gang || this.you_joined || this.joined_you || this.gangs_merged) {
			bio_body_one += "you probably travelled together... ";
			if(bio_body_two.compareTo("") != 0) bio_body_two += "or he might have been a friend, actually... ";
			else bio_body_two += "you think he might have been a friend of yours, in a past life... ";
		}
		
		if(this.abandoned_you || this.you_abandoned || this.deserted_you || this.you_deserted) bio_body_one += "you were most likely separated, at some point... ";
		
		if(this.abandoned_you) bio_body_zero= bio_body_zero+ "hey, didn't he just walk off at some point? It seems like he disappeared...  ";
		if(this.you_abandoned) bio_body_zero= bio_body_zero+ "so, you were in the same gang, but you just walked off and abandoned him at some point... ";
		if(this.deserted_you) bio_body_zero= bio_body_zero+ "and wouldn't you know it, he ran off and deserted you in battle...  ";
		if(this.you_deserted) bio_body_zero= bio_body_zero+ "whoops, you're recalling now that you deserted him in battle... ";
	
		if(this.gangs_merged) bio_body_zero= bio_body_zero+ "your paths met when your gangs decided to band together into one... ";
		if(this.gangs_no_merge && !this.gangs_merged) bio_body_zero= bio_body_zero+ "your fates were nearly linked when your gangs discussed banding together into one, but then that didn't happen... ";			
		
		if(this.met_in_battle) bio_body_zero= bio_body_zero+ "you in fact met for the first time in battle... ";
		if(this.you_slew) bio_body_zero= bio_body_zero+ "oh, now you remember, you killed him... ";
		if(this.you_slew && this.slew_same_gang) bio_body_zero= bio_body_zero+ "your own gangfellow, and you killed him ! ... ";
		if(this.slain_by_player && !this.you_slew) bio_body_zero= bio_body_zero+ "he was finally slain by "+this.slayer+"... ";
		if(this.slain_by_beast) bio_body_zero= bio_body_zero+ "he was finally slain by a "+this.slayer+"... ";
		if(this.death && !this.slain_by_player && !this.slain_by_beast) bio_body_zero= bio_body_zero+ "you found him dead, but you're not sure how it happened... ";
		
		if(this.last_seen - current_turn > 30) bio_body_zero= bio_body_zero+ "that was long ago.";
		else if(this.death) bio_body_zero= bio_body_zero+ "may his soul be at peace.";
		else bio_body_zero= bio_body_zero+ "that's about all you can remember.";
	
		if(bio_body_one.compareTo("") != 0) bio_body_one += "the rest of it is a haze.";
		else bio_body_one += "but really, other than meeting him, nothing else comes to mind.";
		
		if(bio_body_two.compareTo("") != 0) bio_body_two += "it's too hazy to know for sure.";
		else bio_body_two += "you wish you could put together who he is, but it's too far back, in a past life.";
		
		if(clarity == 0) return bio_body_zero;
		else if(clarity == 1) return bio_body_one;
		else if(clarity == 2) return bio_body_two;
		else return "";
	}
	
	
	
	//getters
	public boolean is_activated() {
		return this.activated;
	}
	
	public int getsubject_array_number() {
		return this.subject_array_number;
	}
	
	public String getname() {
		return this.name;
	}
	
	public int getlast_turn() {
		return this.last_turn;
	}
		
	public int getturn_met() {
		return this.turn_met;
	}

	public boolean death() {
		if(this.death) return true;
		else return false;
	}
		
	public int gettaunts_heard() {
		return this.taunts_heard;
	}

	public int gettaunts_told() {
		return this.taunts_told;
	}
	
	public int getthreats_heard() {
		return this.threats_heard;
	}

	public int getthreats_told() {
		return this.threats_told;
	}

	public int getjokes_heard() {
		return this.jokes_heard;
	}

	public int getjokes_told() {
		return this.jokes_told;
	}
	
	//setters
	public void activate() {
		this.activated = true;
	}	
	
	public void last_seen(int date) {
		if(this.activated) last_seen = date;
	}
	
	public void begin_bio(String name, int number, int turn, boolean battle) {
		if(!this.activated) {
			this.name = name;
			this.subject_array_number = number;
			this.turn_met = turn;
			this.activated = true;
			if(battle) this.met_in_battle = true;
		}
	}
	
	public void setlast_turn(int turn) {
		this.last_turn = turn;
	}
	
	public void see_death() {
		this.death = true;
	}
	
	public void degrade_for_rebirth() {
		this.clarity--;
	}
	
	public void found_dead(int date) {
		if(!this.death) this.death = true;
		if(this.date_of_death == -1) this.date_of_death = date;
	}
	
	public void hear_joke() {
		this.taunts_heard++;
	}
	
	public void tell_joke() {
		this.taunts_told++;
	}
	
	public void form_new_gang() {
		this.formed_new_gang = true;
	}
	
	public void duel_for_leader() {
		this.dueled_for_leader = true;
		this.you_dueled = true;
	}
	
	public void challenge(int challenger) {	
		if(challenger == user_array_number) this.you_challenged = true;
		if(challenger == subject_array_number) this.challenged_you = true;
	}
	
	public void join_proposal(int proposer, int proposer_affil, int response_affil) {
		if(proposer_affil == response_affil) {
			if(proposer_affil == 0) {
				if(proposer == user_array_number) this.you_proposed_gang = true;
				if(proposer == subject_array_number) this.proposed_gang_to_you = true;		
			}
		}
		else {
			if(proposer_affil == 0) {
				if(proposer == user_array_number) this.you_asked_to_join = true;
				if(proposer == subject_array_number) this.asked_to_join_you = true;
			}
			if(response_affil == 0) {
				if(proposer == user_array_number) this.you_invited = true;
				if(proposer == subject_array_number) this.invited_you = true;
			}
		}
	}
		
	public void join(int joiner) {	
		if(joiner == user_array_number) this.you_joined = true;
		if(joiner == subject_array_number) this.joined_you = true;
	}
	
	public void unfriendly() {
		this.unfriendly = true;
	}
	
	public void abandoned(int abandoner) {
		if(abandoner == user_array_number) this.you_abandoned = true;
		if(abandoner == subject_array_number) this.abandoned_you = true;
	}

	public void deserted(int deserter) {
		if(deserter == user_array_number) this.you_deserted = true;
		if(deserter == subject_array_number) this.deserted_you = true;
	}

	public void gangs_no_merge() {
		this.gangs_no_merge = true;
	}
	
	public void gangs_merged() {
		this.gangs_merged  = true;
	}
	
	public void attack(int actor, int actor_affil, int actee_affil) {
		if(actor == subject_array_number) {
			if(actor_affil==actee_affil && actor_affil != 0) this.attacked_you_same_gang = true;
			else if(actor_affil != 0 && actee_affil != 0) this.gang_attacked_gang = true;
			else if(actor_affil != 0) this.gang_attacked_you = true;
			else if(actee_affil != 0) this.attacked_your_gang = true;
			else this.attacked_you = true;
		}
		else if(actor == user_array_number) {
			if(actor_affil==actee_affil && actor_affil != 0) this.you_attacked_same_gang = true;
			else if(actor_affil != 0 && actee_affil != 0) this.gang_attacked_gang = true;
			else if(actor_affil != 0) this.you_attacked_gang = true;
			else if(actee_affil != 0) this.your_gang_attacked = true;
			else this.you_attacked = true;
		}
	}
	
	public void slain_by_player(String actor, int actor_affil, int actee_affil, int date, boolean youslew) {
		if(youslew) this.you_slew = true;
		else {
			if(actor_affil == actee_affil) this.slew_same_gang = true;
		}
		this.slain_by_player = true;
		this.slayer = actor;
		this.date_of_death = date;
		this.death = true;
	}
	
	public void slain_by_beast(String code, int date) {
		this.slain_by_beast = true;
		this.slayer = code;
		this.date_of_death = date;
		this.death = true;
	}
	
	public void threatened(int threatener) {
		if(threatener == user_array_number) this.threats_told++;
		if(threatener == subject_array_number) this.threats_heard++;
	}

	public void taunted(int taunter) {
		if(taunter == user_array_number) this.taunts_told++;
		if(taunter == subject_array_number) this.taunts_heard++;
	}
	
	public void concession(int conceder) {
		if(conceder == user_array_number) this.you_conceded = true;
		if(conceder == subject_array_number) this.conceded_to_you = true;
	}
	
	public void you_dueled() {
		this.you_dueled = true;
	}
	
	public void bested(int best) {
		if(best == user_array_number) this.you_bested = true;
		if(best == subject_array_number) this.bested_you = true;
	}

	public void duel_run_away(int runner) {
		if(runner == user_array_number) this.you_ran_from_duel = true;
		if(runner == subject_array_number) this.ran_from_duel = true;
	}


	public void you_were_voted_out() {
		this.you_were_voted_out = true;
	}
	
	public void was_voted_out() {
		this.was_voted_out = true;
	}
	
	public void rejected(int rejector) {
		if(rejector == user_array_number) this.you_rejected = true;
		if(rejector == subject_array_number) this.rejected_you = true;
	}
	
	public void scoffed(int scoffer) {
		if(scoffer == user_array_number) this.you_scoffed = true;
		if(scoffer == subject_array_number) this.scoffed_at_you = true;
	}

	public void you_rejected_personally() {
		this.you_rejected_personally = true;
	}

}