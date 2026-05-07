import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Safeland {

	public static void main(String args[])  {
		
		Dice dice = new Dice();
		
		Being[] army = new Being[50];

		// army[0] = new Human("Zero", dice, 0, 3, 5, 5, 10);
		// army[1] = new Dog(dice, 10, 10, 10);
		// army[2] = new Rat(dice);
		// System.out.println("Here they are!");
		// System.out.println(army[0]);
		// System.out.println(army[1]);
		// System.out.println(army[2]);
		//
		// masterStringOut("he is standing right there.");
		// masterStringOut("he he he he he.");
		// masterStringOut("He, Sir? He? He?!");
		// masterStringOut("It can't be he.");
		// masterStringOut("There there, my theremin.");
		//
		for(int d = 0; d < 100; d++) {
			System.out.println(dice.throwdice(1, 0));
		}
		
	}
	
	public void masterStringOut(String stringin) {
		
		stringin = stringin.replaceAll("\\s he \\s", "\\s she \\s");

		System.out.println(stringin);

	}
}