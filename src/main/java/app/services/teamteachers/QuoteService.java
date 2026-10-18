package app.services.teamteachers;

import app.entities.teamteachers.Quote;
import app.persistence.ConnectionPool;
import app.persistence.teamteachers.QuoteMapper;

public class QuoteService {
    private QuoteMapper quoteMapper;
    private ConnectionPool connectionPool;

    public QuoteService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        quoteMapper = new QuoteMapper(connectionPool);
    }

    public Quote getPhilosophicalAnswer(String input) {
        return quoteMapper.getPhilosophicalAnswer(input);
    }
}
