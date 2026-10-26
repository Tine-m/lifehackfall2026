package app.entities.teama;

public class Game {
	private Colour target;
	private Colour guess;
	private int score;
	private int benchmarkScore;
	private int level;

	public Game(Colour target, int level) {
		this.target = target;
		this.level = level;
		benchmarkScore = calculateBenchmarkScore(level);
	}

	private int calculateBenchmarkScore(int level) {
		switch (level) {
			case 1:
				return 800; // level 1
			case 2:
				return 1000; // level 2
			default:
				return 0;
		}
	}

	public Colour getTarget() {
		return target;
	}

	public void setTarget(Colour target) {
		this.target = target;
	}

	public Colour getGuess() {
		return guess;
	}

	public void setGuess(Colour guess) {
		this.guess = guess;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}
