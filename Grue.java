
public class Grue extends Beast {

	
	//constructor
	public Grue(Dice birth) {
		this.name = "grue";
		this.type_for_battle = "beast";
		this.att_speed = new int[5];
		this.att_speed[0] = birth.throwdice(1,6);
		this.att_speed[1] = birth.throwdice(2,5);
		this.att_speed[2] = 0;
		this.att_speed[3] = 0;
		this.att_speed[4] = 0;
		this.strengthadj = 0;
		this.skill_bonus_from_hit = 1;
		this.value = 10;
		this.run_chance = 3;
		this.name_plural = "rats";
		this.sound_of_pain = "SQUEEAK!";
		this.kill_sounds = new String[3];
		this.kill_sounds[0] = "\"Please, no more...\"";
		this.kill_sounds[1] = "\"Aaarrrrg... \"";
		this.kill_sounds[2] = "\"RrrAAAaaaa... ugh.\"";
		this.singular_victory_message = "The rat nibbles cold flesh for a while, and then scuttles out of view.";
		this.plural_victory_message = "The rats squeak in argument over the cold flesh... once sated, they scuttle out \nof view.";
		this.number_of_attacks = 2;
		this.strength = birth.throwdice(2,3);
		this.health = birth.throwdice(1,3) + 3;
		this.maxhealth = this.health;
		this.defense = 2;
		this.potential_wound = 0;
	}		

	//grue attacks
	public Attack attack_one() {
		//do nothing
		return (new Attack(0, "..."));
	}
	
	public Attack attack_two(Player victim, int textlevel, Dice bite, String rat_index) {
		int hitroll = bite.throwdice(1,20);
		int tohit = victim.getdefense() + victim.getmoraleadj()- this.att_speed[0] + 3;
		if(tohit <= 1) tohit = 2;
		int damage = 0;
		String dialogue = "";
		if(hitroll >= tohit || hitroll == 20) {
			Wait.Blip();
			damage = bite.throwdice(1,4) + this.strengthadj;
			if(damage < 1) damage = 1;
			switch (textlevel) {
				case 0: break;
				case 1: 
					dialogue = ("\"Ouch!\"");
					break;
				case 2:
				case 3:
					if(rat_index.compareToIgnoreCase("void") == 0) {
						dialogue = ("The rat bites "+victim.getname()+" for "+damage+" points!");
					}
					else dialogue = ("The rat ("+rat_index+") bites "+victim.getname()+" for "+damage+" points!");
					break;
				default:
					System.out.println("Wha?");
			}				
		}
		else {
			switch (textlevel) {
				case 0:
				case 1: 
					break;
				case 2:
				case 3:
					if(rat_index.compareToIgnoreCase("void") == 0) {
						dialogue = ("The rat chomps at "+victim.getname()+", but misses!");
					}
					else dialogue = ("The rat ("+rat_index+") chomps at "+victim.getname()+", but misses!");
					break;
				default:
					System.out.println("Wha?");
			}
		} 
		return (new Attack(damage, dialogue));
	}
}
