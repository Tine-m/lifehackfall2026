package app.services.teama;

import app.entities.teama.Colour;
import app.persistence.ConnectionPool;

import java.util.Random;

public class ColourService {
    private Colour currentTargetColour;
    private ConnectionPool connectionPool;

    public ColourService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public int getScore(Colour guess, Colour target){
        int distR = target.getR()-guess.getR();
        int distG = target.getG()-guess.getG();
        int distB = target.getB()-guess.getB();
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

    public Colour getNewTargetColour() {
        currentTargetColour = getRandomColour();
        return currentTargetColour;
    }

    public Colour getCurrentTargetColour() {
        return currentTargetColour;
    }
}