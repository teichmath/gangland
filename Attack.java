
public class Attack {

	int damage;
	int[] damage_group;
	String[] dialogue_group;
	String dialogue_a;
	String dialogue_b;
	String dialogue_c;
	int wait_a;
	int wait_b;
	
	public Attack() {
		this(0, new int[5], new String[5], "", "", "", 0, 0, true);
	}
	
	public Attack(int d, String dl) {
		this(d, new int[5], new String[5], dl, "", "", 0, 0, true);
	}

	public Attack(int d, String da, String db) {
		this(d, new int[5], new String[5], da, db, "", 0, 0, true);
	}
	
	public Attack(int d, String da, String db, String dc) {
		this(d, new int[5], new String[5], da, db, dc, 0, 0, true);
	}
	
	public Attack(int[] dg, String[] dlg) {
		this(0, dg, dlg, "", "", "", 0, 0, false);
	}
	
	public Attack(int d, String da, String db, String dc, int wa) {
		this(d, new int[5], new String[5], da, db, dc, wa, 0, true);
	}
	
	public Attack(int d, int[] dg, String[] dlg, String da, String db, String dc, int wa, int wb, boolean replace) {
		this.damage = d;
		this.damage_group = dg;
		this.dialogue_group = dlg;
		this.dialogue_a = da;
		this.dialogue_b = db;
		this.dialogue_c = dc;
		this.wait_a = wa;
		this.wait_b = wb;
		if(replace) {
			this.damage_group = new int[5];
			this.dialogue_group = new String[5];
			for(int i = 0; i < 5; i++) {
				damage_group[i] = 0;
				dialogue_group[i] = "";
			}
		}
	}

	public int getDamage() {
		return damage;
	}

	public String getDialogue() {
		return dialogue_a;
	}
	
	public int all_damage(int d) {
		if(d < damage_group.length) return damage_group[d];
		else {
			System.out.println("**** d doesn't fit in damage_group.");
			return 0;
		}
	}
	
	public String all_dialogue(int d) {
		if(d < dialogue_group.length) return dialogue_group[d];
		else {
			System.out.println("*** d doesn't fit in dialogue_group.");
			return "";
		}
	}
	
	public String getDialogue_a() {
		return dialogue_a;
	}

	public String getDialogue_b() {
		return dialogue_b;
	}

	public String getDialogue_c() {
		return dialogue_c;
	}

	public int getWait_a() {
		return wait_a;
	}

	public int getWait_b() {
		return wait_b;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public void setDialogue(String dialogue) {
		this.dialogue_a = dialogue;
	}
	
	
}
