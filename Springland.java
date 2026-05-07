import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class Springland {

	//these are all instance fields, NOT class(static) fields:
	Dice dice;
	Being[] army;
	String[] names;
	int armysize;
	int user;
	int turnclock;
	String player_name;
	String enter_to_continue;
	boolean play_on;
	
	
	public static void main(String args[])  {
		
		Springland game = new Springland();

		System.out.println("\n\n\n\n\n  \n\n\n\n\n \n\n\n\n\n \n\n\n\n\n \n\n\n\n\n \n\n\n\n\n \n\n\n\n\n \n\n\n\n\n \n\n\n\n\n");		
		
		System.out.println("*       Who are you ?                                                *");

		boolean name_okay = true;
		boolean game_instantiated = false;
		
		do {
			//get name input
   			name_okay = true;
			
			game.player_name = game.stringInput();
			
			if(game.player_name == null || game.player_name.isEmpty()) name_okay = false;
			
			if (game.player_name.compareTo("") == 0) {
				System.out.println("Eh? Speak up!");
				name_okay = false;
			}					

		} while(!name_okay);

		game.fillNamesArray();
		
		//populate the game
		for (int i = 0; i < game.armysize; i++) {
			game.army[i] = new Human(game.names[i], game.dice);
		}


		System.out.println("Right, "+game.player_name+", enjoy your stay!");
		
		Wait.manySec(2);

		//user's character is born and placed in the army array.
		game.army[game.user] = new Human(game.player_name, game.dice);
		
		System.out.println("\n\n***\n You get to your feet. A bit of a headache. Nothing looks familiar. \n");
		game.hitEnter();
			
		while (game.play_on) {
			game.Encounter();
		}
	}	
	//end main method
			
	//constructor	
	public Springland () {
		armysize = 50;
		user = armysize;
		play_on = true;
		dice = new Dice();
		army = new Human[armysize+1];
	}
	
	//bank of character names
	public void fillNamesArray() {
		names = new String[100];
		
			names[0] = "Jack";
			names[1] = "Bern";
			names[2] = "Quince";
			names[3] = "Macgreg";
			names[4] = "Tom";
			names[5] = "Robbin";
			names[6] = "Graebel"; 
			names[7] = "Knute"; 
			names[8] = "Owen"; 
			names[9] = "Henry"; 
			names[10] = "Marco";
			names[11] = "Erasus";
			names[12] = "Liudprand";
			names[13] = "Rosenfield";
			names[14] = "Renald";
			names[15] = "Klaus";
			names[16] = "Wastere";
			names[17] = "Eric";
			names[18] = "Ash";
			names[19] = "Eliot";
			names[20] = "Helmut";
			names[21] = "George";
			names[22] = "Richard";
			names[23] = "John";
			names[24] = "Elias";
			names[25] = "William";
			names[26] = "Oliver";
			names[27] = "Peter";
			names[28] = "Homer";
			names[29] = "David";
			names[30] = "Jakob";
			names[31] = "Winston";
			names[32] = "Samuel";
			names[33] = "Megunticook";
			names[34] = "Perry";
			names[34] = "Candide";
			names[35] = "Orson";
			names[36] = "Nollan";
			names[37] = "Tarran";
			names[38] = "Maxwell";
			names[39] = "Frederich";
			names[40] = "Alex";
			names[41] = "Colby";
			names[42] = "Xavier";
			names[43] = "Luke";
			names[44] = "Ben";
			names[45] = "Patronus";
			names[46] = "Mailer";
			names[47] = "Chuck";
			names[48] = "Cutter";
			names[49] = "Ignatious";
			names[50] = "Hulot";
			names[51] = "Rufus";
			names[52] = "Russokov";
			names[53] = "Scruggs";
			names[54] = "Davey";
			names[55] = "Skippy";
			names[56] = "Paul";
			names[57] = "Barney";
			names[58] = "Graham";
			names[59] = "Clank";
			names[60] = "Houndbone";
			names[61] = "Casper";
			names[62] = "Chase";
			names[63] = "Buddy";
			names[64] = "Gavriel";
			names[65] = "Virgil";
			names[66] = "Philip";
			names[67] = "Hinman";
			names[68] = "Edward";
			names[69] = "Lawrence";
			names[70] = "Riley";
			names[71] = "Flynn";
			names[72] = "Ryan";
			names[73] = "Julian";
			names[74] = "Red";
			names[75] = "Ralph";
			names[76] = "Lars";
			names[77] = "Bruno";
			names[78] = "Anders";
			names[79] = "Schroder";
			names[80] = "Gunther";
			names[81] = "Eustis";
			names[82] = "Geert";
			names[83] = "Honza";
			names[84] = "Vincent";
			names[85] = "Happy";
			names[86] = "Claxson";
			names[87] = "Ogden";
			names[88] = "Harvey";
			names[89] = "Disko";
			names[90] = "Salters";
			names[91] = "Pratt";
			names[92] = "Jonah";
			names[93] = "Buber";
			names[94] = "Einhard";
			names[95] = "Gogol";
			names[96] = "Bousquet";
			names[97] = "Morley";
			names[98] = "Ham";
			names[99] = "Rudin";
	}			
			
	public void Encounter() {
		int one_to_encounter = dice.throwdice(1, armysize) - 1;
		System.out.println(army[one_to_encounter].getname()+":  \"Hi, "+army[user].getname()+".\"");
		System.out.println("How do you respond?");
		String user_response = stringInput();
		System.out.println(army[one_to_encounter].getname()+":  \"Hmmf.\"\n\n");
	}

	public void hitEnter() {
		System.out.println("\n\n[ (hit Enter)  ]    ");
		enter_to_continue = stringInput();
	}
	
	public String stringInput() {

 		//  open up standard input
    	// BufferedReader is the class, br is the object, instantiated here. 
   		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  	   	String user_arg = "null";

    	//  read the user input from the command-line; need to use try/catch with the 
    	//  readLine() method 
    	try { 
    		user_arg = br.readLine(); 
    	} 
    	catch (IOException ioe) { 
    		System.out.println("IO error trying to read your input!"); 
    		System.exit(1); 
    	}

		user_arg = user_arg.trim();

		return user_arg;
    }

}

// :D)   THE END










































		