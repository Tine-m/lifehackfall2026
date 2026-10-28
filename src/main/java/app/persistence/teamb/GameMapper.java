
package app.persistence.teamb;

import app.entities.teamb.Game;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameMapper {

    // Gemmer et spil og returnerer det nye game_id
    public static int saveGame(String playerName, boolean lifehack, int moves,
                               int durationSeconds, ConnectionPool connectionPool)
            throws DatabaseException {

        String sql = """
                INSERT INTO teamb_game
                    (player_name, is_lifehack, score, duration_seconds)
                VALUES (?, ?, ?, ?)
                RETURNING game_id
                """;

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, playerName);
            ps.setBoolean(2, lifehack);
            ps.setInt(3, moves);
            ps.setInt(4, durationSeconds);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("game_id");
                }
                throw new SQLException("Intet game id blev returneret");
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Kunne ikke gemme spillet",
                    e.getMessage()
            );
        }
    }

    // defineres efter færrest moves først, derefter hurtigste tid
    public static List<Game> getTopGames(boolean lifehack, int limit, ConnectionPool connectionPool) throws DatabaseException {

        String sql = """
                SELECT game_id, player_name, is_lifehack,
                       score, duration_seconds
                FROM teamb_game
                WHERE is_lifehack = ?
                ORDER BY score ASC, duration_seconds ASC, finished_at ASC
                LIMIT ?
                """;

        List<Game> games = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBoolean(1, lifehack);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    games.add(new Game(
                            rs.getInt("game_id"),
                            rs.getString("player_name"),
                            rs.getBoolean("is_lifehack"),
                            rs.getInt("score"),
                            rs.getInt("duration_seconds")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Kunne ikke hente leaderboard",
                    e.getMessage()
            );
        }

        return games;
    }
}
