package app.services.teama;

import app.entities.teama.Game;
import app.entities.teama.Highscore;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.teama.ScoreMapper;

import java.util.ArrayList;
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

    public boolean unlockNextLevel(Game game) {
        return game.getScore() > game.getBenchmarkScore();
    }

    public String highscoreCheck(Game game) {
        List <Highscore> todayList = new ArrayList<>();
        List <Highscore> weekList = new ArrayList<>();
        List <Highscore> allTimeList = new ArrayList<>();

        try {
            allTimeList = getTopAllTime(10);
            weekList = getTopWeek(5);
            todayList = getTopToday(5);
        } catch (DatabaseException e) {
            System.out.println("Error: Highscores could not be loaded");
        }

        for (int i = 0; i < 10; i++) {
            if (i == allTimeList.size()){
                return "Congratulations! You got the number " + (i+1) + " highscore of all time!";
            }
            if (game.getScore() > allTimeList.get(i).getTotal() ) {
                return "Congratulations! You got the number " + (i+1) + " highscore of all time!";
            }
        }

        for (int i = 0; i < 5; i++) {
            if (i == weekList.size()){
                return "Congratulations! You got the number " + (i+1) + " highscore this week!";
            }
            if (game.getScore() > allTimeList.get(i).getTotal()) {
                return "Congratulations! You got the number " + (i+1) + " highscore this week!";
            }
        }

        for (int i = 0; i < 5; i++) {
            if (i == todayList.size()){
                return "Congratulations! You got the number " + (i+1) + " highscore today!";
            }
            if (game.getScore() > allTimeList.get(i).getTotal()) {
                return "Congratulations! You got the number " + (i+1) + " highscore today!";
            }
        }

        return "You did not make the leaderboard";
    }
}