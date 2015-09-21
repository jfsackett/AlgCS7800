package hw0;

import java.util.Random;

public class GuessNum {
	private final int n;
	private final int guessMe;
	
	private int prevGuess;
	
	public static void main(String[] args) {
		int n = 16;
		int numTests = 1;
		if (args.length > 0) {
			n = Integer.parseInt(args[0]);
		}
		if (args.length > 1) {
			numTests = Integer.parseInt(args[1]);
		}

		Random random = new Random();
		for (int ix=1; ix <= numTests; ix++) {
			int guessMe = ix; //random.nextInt(n - 1) + 1;
			System.out.print("Find: " + guessMe);
			GuessNum guessNum = new GuessNum(guessMe, n);
			int found = guessNum.guessIt();
			if (found == guessMe) {
				System.out.println(", Found.");				
			}
			else {
				System.out.println(", NOT FOUND.");
				break;
			}
		}
	}

	public GuessNum(int guessMe, int n) {
		this.guessMe = guessMe;
		this.n = n;
	}
	
	public int guessIt() {
		int low = 1;
		int high = n;
		int currGuess = high;
		guess(low);
		Result result;
		int ix = (int)Math.ceil((Math.log(n) / Math.log(2)));
		while ((result = guess(currGuess)) != Result.CORRECT && low != high && ix-- > 0) {
//			System.out.println("Guess: " + currGuess + "  Result: " + result + "  Low: " + low + "  High: " + high);
			if ((result == Result.CLOSER && currGuess >= high) || (result == Result.FURTHER && currGuess <= low)) {
				low = (high + low + 1) / 2;
			}
			else {
				high = (high + low) / 2;
			}
			currGuess = high + low - prevGuess;
		}
		
		return (low == high) ? low : currGuess; 
	}
	
	private Result guess(int currGuess) {
		Result result;
		if (currGuess == guessMe) {
			result = Result.CORRECT;
		}
		else if (Math.abs(guessMe - currGuess) < Math.abs(guessMe - prevGuess)) {
			result = Result.CLOSER;
		}
		else {
			result = Result.FURTHER;
		}
		prevGuess = currGuess;
		return result;
	}
	
	private enum Result {
		CORRECT, CLOSER, FURTHER
	}	
}
