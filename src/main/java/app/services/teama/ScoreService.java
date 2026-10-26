package app.services.teama;

import app.entities.teama.Highscore;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.teama.ScoreMapper;

import java.util.List;

public class ScoreService {
    private ConnectionPool connectionPool;
    private ScoreMapper scoreMapper;

    public ScoreService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.scoreMapper = new ScoreMapper(connectionPool);
    }

    public void saveScore(Highscore highscore) throws DatabaseException {
        scoreMapper.saveScore(highscore);
    }

    public List<Highscore> getTopAllTime(int amount) throws DatabaseException {
        return scoreMapper.getTopAllTime(amount);
    }

    public List<Highscore> getTopWeek(int amount) throws DatabaseException {
        return scoreMapper.getTopWeek(amount);
    }

    public List<Highscore> getTopToday(int amount) throws DatabaseException {
        return scoreMapper.getTopToday(amount);
    }
}