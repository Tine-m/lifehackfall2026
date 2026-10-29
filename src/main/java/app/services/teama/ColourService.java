package app.services.teama;

import app.entities.teama.Colour;
import app.persistence.ConnectionPool;

import java.util.Random;

public class ColourService {
    private ConnectionPool connectionPool;

    public ColourService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public int getScore(Colour guess, Colour targetColour){
        // The R, G and B-values of the guess is compared to the target's values
        int distR = targetColour.getR()-guess.getR();
        int distG = targetColour.getG()-guess.getG();
        int distB = targetColour.getB()-guess.getB();

        // Score is calculated by subtracting the distance between the colors from the greatet possible distance,
        // after which the result is multiplied by 1000
        double dist3D = Math.sqrt((distR*distR)+(distG*distG)+(distB*distB));
        double score = (Math.sqrt(195075)-dist3D)*1000;
        return (int)Math.round(score);
    }

    public Colour getRandomColour() {
        return new Colour(getRandomRGBValue(), getRandomRGBValue(), getRandomRGBValue());
    }

    private int getRandomRGBValue() {
        Random random = new Random();
        return random.nextInt(1, 256);
    }
}