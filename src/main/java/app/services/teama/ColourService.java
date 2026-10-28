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
        // Gættets r, g og b-værdier sammenlignes med targetfarvens værdier
        int distR = targetColour.getR()-guess.getR();
        int distG = targetColour.getG()-guess.getG();
        int distB = targetColour.getB()-guess.getB();

        // Scoren udregnes ved at trække afstanden mellem farverne fra den størst mulige afstand ganget,
        // hvorefter scoren ganges med 1000
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