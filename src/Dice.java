import java.util.Random;

public class Dice {

	Random myRandom;

	public Dice() {
		this.myRandom = new Random();
	}

	public int throwdice(int rolls, int faces) {
		int result = 0;
		double x = 0;
		for (int i = 0; i < rolls; i++) {
			x = this.myRandom.nextDouble() * faces;
			result = result + (int) x + 1;
			if(result > faces) result--;
			if(result < 1) result = 1;
			x = 0;
		}
		return result;
	}
}
		
	