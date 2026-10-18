package app.persistence.teamteachers;

import app.entities.teamteachers.Quote;
import app.persistence.ConnectionPool;

public class QuoteMapper {

    private ConnectionPool connectionPool;

    public QuoteMapper(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    //ONLY for demo purpose!
    public Quote getPhilosophicalAnswer(String input) {
        // hardcoded demo data. Should search the database for real data
        return new Quote("Kierkegaard", "Af alle latterlige ting forekommer det mig at være det allerlatterligste at have travlt", "kierkegaard.png");
    }

}