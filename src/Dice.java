import java.util.Random;

public class Dice {

	// One generator shared by every Dice, seeded once per process, so a whole session
	// (including restarts from the main loop) replays exactly from the seed + the inputs.
	// Set GANGLAND_SEED to reproduce a session; otherwise a random seed is chosen.
	static final long SEED = initSeed();
	static final Random sharedRandom = new Random(SEED);

	Random myRandom;

	public Dice() {
		this.myRandom = sharedRandom;
	}

	static long initSeed() {
		String s = System.getenv("GANGLAND_SEED");
		if (s != null && !s.trim().isEmpty()) {
			try {
				return Long.parseLong(s.trim());
			}
			catch (NumberFormatException e) {
				System.err.println("GANGLAND_SEED is not a number: " + s + " (using a random seed)");
			}
		}
		return new Random().nextLong();
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
