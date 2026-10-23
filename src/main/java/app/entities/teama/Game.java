package app.entities.teama;

public class Game {
	private Colour target;
	private Colour guess;
	private double score;

	public Game(Colour target) {
		this.target = target;
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

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
}
