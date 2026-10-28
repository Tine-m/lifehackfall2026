
package app.services.teamb;

import app.entities.teamb.Card;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.teamb.CardMapper;
import app.persistence.teamb.GameMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MemoService {

    private final ConnectionPool connectionPool;

    public MemoService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public List<Card> getGameCards(boolean lifehack)
            throws DatabaseException {
        return CardMapper.getRandomCards(8, lifehack, connectionPool);
    }

    // Returnerer ét kort pr. par (foretrækker kortet med billede)
    public List<Card> getLifehackCards(List<Card> cards) {
        Map<Object, Card> byPair = new LinkedHashMap<>();

        for (Card card : cards) {
            Card existing = byPair.get(card.getPairId());

            if (existing == null
                    || (existing.getImageURL() == null
                    && card.getImageURL() != null)) {
                byPair.put(card.getPairId(), card);
            }
        }

        return new ArrayList<>(byPair.values());
    }

    public List<?> getLeaderboard(boolean lifehack)
            throws DatabaseException {
        return GameMapper.getTopGames(lifehack, 10, connectionPool);
    }

    // GET /results: kun leaderboard
    public Map<String, Object> getResultsModel(String requestedMode)
            throws DatabaseException {
        String mode = "classic".equals(requestedMode)
                ? "classic" : "lifehack";
        boolean lifehack = "lifehack".equals(mode);

        Map<String, Object> model = new HashMap<>();
        model.put("mode", mode);
        model.put("leaderboard", getLeaderboard(lifehack));

        return model;
    }

    // POST /results: viser resultatet og gemmer eventuelt spillet
    public Map<String, Object> getResultsModel(String time, String moves, String requestedMode,
                                               String completedParam, String name,
                                               List<Card> gameCards) throws DatabaseException {

        int seconds = Integer.parseInt(time);
        int moveCount = Integer.parseInt(moves);

        String mode = "classic".equals(requestedMode) ? "classic" : "lifehack";
        boolean lifehack = "lifehack".equals(mode);
        boolean completed = Boolean.parseBoolean(completedParam);

        Map<String, Object> model = new HashMap<>();
        model.put("time", String.format(
                "%02d:%02d", seconds / 60, seconds % 60));
        model.put("seconds", seconds);
        model.put("moves", moveCount);
        model.put("mode", mode);
        model.put("completed", completed);

        // Lifehack-kort fra det spil der lige er spillet (null i klassisk mode)
        model.put("gameCards", lifehack ? gameCards : null);

        if (name != null && completed) {
            // Trin 2: spilleren har skrevet navn -> gem i databasen
            int gameId = saveGame(name, lifehack, moveCount, seconds);
            model.put("canSave", false);
            model.put("highlight", gameId);
        } else {
            // Trin 1: spillet er lige slut -> vis resultat + navnefelt
            model.put("canSave", completed);
        }

        model.put("leaderboard", getLeaderboard(lifehack));

        return model;
    }

    public int saveGame(String name, boolean lifehack, int moves, int seconds) throws DatabaseException {
        String playerName = name == null ? "" : name.trim();

        if (playerName.isEmpty()) {
            playerName = "ukendt";
        }

        return GameMapper.saveGame(
                playerName, lifehack, moves, seconds, connectionPool);
    }
}
