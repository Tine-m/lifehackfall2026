package app.persistence.teama;

import app.entities.teama.Highscore;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ScoreMapper {
    private final ConnectionPool connectionPool;

    public ScoreMapper(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void saveScore(Highscore highscore) throws DatabaseException {
        String query = "INSERT INTO teama_highscores (initials, score1, score2, score3, date) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, highscore.getInitials());
            stmt.setInt(2, highscore.getScore1());
            stmt.setInt(3, highscore.getScore2());
            stmt.setInt(4, highscore.getScore3());
            stmt.setDate(5, Date.valueOf(highscore.getDate()));
            stmt.executeQuery();
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke gemme highscore: " + e.getMessage());
        }
    }

    public List<Highscore> getTopAllTime(int amount) throws DatabaseException {
        List<Highscore> highscores = new ArrayList<>();
        String query = "SELECT initials, score1, score2, score3, date FROM teama_highscores ORDER BY score DESC LIMIT ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, amount);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int score1 = rs.getInt("score1");
                int score2 = rs.getInt("score2");
                int score3 = rs.getInt("score3");
                String initials = rs.getString("initials");
                LocalDate date = rs.getDate("date").toLocalDate();
                highscores.add(new Highscore(score1, score2, score3, initials, date));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente highscores: " + e.getMessage());
        }
        return highscores;
    }

    public List<Highscore> getTopWeek(int amount) throws DatabaseException {
        List<Highscore> highscores = new ArrayList<>();
        String query = "SELECT initials, score1, score2, score3, date FROM teama_highscores WHERE date >= CURRENT_DATE - INTERVAL '6 days' ORDER BY score LIMIT ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, amount);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int score1 = rs.getInt("score1");
                int score2 = rs.getInt("score2");
                int score3 = rs.getInt("score3");
                String initials = rs.getString("initials");
                LocalDate date = rs.getDate("date").toLocalDate();
                highscores.add(new Highscore(score1, score2, score3, initials, date));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente highscores: " + e.getMessage());
        }
        return highscores;
    }

    public List<Highscore> getTopToday(int amount) throws DatabaseException {
        List<Highscore> highscores = new ArrayList<>();
        String query = "SELECT initials, score1, score2, score3, date FROM teama_highscores WHERE date = CURRENT_DATE ORDER BY score LIMIT ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, amount);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int score1 = rs.getInt("score1");
                int score2 = rs.getInt("score2");
                int score3 = rs.getInt("score3");
                String initials = rs.getString("initials");
                LocalDate date = rs.getDate("date").toLocalDate();
                highscores.add(new Highscore(score1, score2, score3, initials, date));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente highscores: " + e.getMessage());
        }
        return highscores;
    }
}