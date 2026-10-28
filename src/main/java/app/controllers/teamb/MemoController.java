package app.controllers.teamb;

import app.entities.teamb.Card;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.teamb.CardMapper;
import app.persistence.teamb.GameMapper;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MemoController {

    private ConnectionPool connectionPool;

    public MemoController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
    //test

    public void setRoutes(JavalinConfig config) {
        config.routes.get("/memorygame", ctx -> renderFrontpage(ctx));
        config.routes.get("/results", ctx -> resultsPage(ctx));
        config.routes.post("/results", ctx -> resultsPageWithStats(ctx));
        config.routes.get("/game", ctx -> renderGame(ctx));
    }


    public void renderFrontpage(Context ctx) {
        ctx.render("teamb/frontpage.html");
    }

    // GET /results: kun leaderboard (fra forsiden eller Lifehack/Klassisk-linkene)
    private void resultsPage(Context ctx) {
        String mode = "classic".equals(ctx.queryParam("mode")) ? "classic" : "lifehack";
        boolean lifehack = "lifehack".equals(mode);

        Map<String, Object> model = new HashMap<>();
        model.put("mode", mode);

        try {
            model.put("leaderboard", GameMapper.getTopGames(lifehack, 10, connectionPool));
        } catch (DatabaseException e) {
            e.printStackTrace();
            ctx.status(500);
            return;
        }

        ctx.render("teamb/results.html", model);
    }



    public void renderGame(Context ctx) {
        String mode;

        if ("classic".equals(ctx.queryParam("mode"))) {
            mode = "classic";
        } else {
            mode = "lifehack";
        }


        boolean lifehack = "lifehack".equals(mode);

        try {
            // 8 random par af lifehack kort
            List<Card> cards = CardMapper.getRandomCards(8, lifehack, connectionPool);

            Map<String, Object> model = new HashMap<>();
            model.put("mode", mode);
            model.put("cards", cards);
            model.put("totalPairs", cards.size() / 2);

            // Gem ét kort pr. par i sessionen, så resultatsiden kan vise lifehacks
            if (lifehack) {
                ctx.sessionAttribute("gameCards", onePerPair(cards));
            } else {
                ctx.sessionAttribute("gameCards", null);
            }

            ctx.render("teamb/game.html", model);

        } catch (DatabaseException e) {
            ctx.status(500);
        }
    }



    // POST /results: bruges både når spillet slutter og når spilleren gemmer sit navn.
    private void resultsPageWithStats(Context ctx) {
        int seconds = Integer.parseInt(ctx.formParam("time"));
        int moves = Integer.parseInt(ctx.formParam("moves"));
        String mode = "classic".equals(ctx.formParam("mode")) ? "classic" : "lifehack";
        boolean lifehack = "lifehack".equals(mode);
        boolean completed = Boolean.parseBoolean(ctx.formParam("completed"));
        String name = ctx.formParam("name");

        Map<String, Object> model = new HashMap<>();
        model.put("time", String.format("%02d:%02d", seconds / 60, seconds % 60));
        model.put("seconds", seconds);
        model.put("moves", moves);
        model.put("mode", mode);
        model.put("completed", completed);

        // Lifehack-kort fra det spil der lige er spillet (null i klassisk mode)
        List<Card> gameCards = ctx.sessionAttribute("gameCards");
        model.put("gameCards", lifehack ? gameCards : null);

        try {
            if (name != null && completed) {
                // Trin 2: spilleren har skrevet navn -> gem i databasen
                name = name.trim();
                if (name.isEmpty()) {
                    name = "ukendt";
                }
                int gameId = GameMapper.saveGame(name, lifehack, moves, seconds, connectionPool);
                model.put("canSave", false);
                model.put("highlight", gameId);
            } else {
                // Trin 1: spillet er lige slut -> vis resultat + navnefelt
                model.put("canSave", completed);
            }

            model.put("leaderboard", GameMapper.getTopGames(lifehack, 10, connectionPool));

        } catch (DatabaseException e) {
            e.printStackTrace();
            ctx.status(500);
            return;
        }

        ctx.render("teamb/results.html", model);
    }

    // Returnerer ét kort pr. par (foretrækker kortet med billede)
    private List<Card> onePerPair(List<Card> cards) {
        Map<Object, Card> byPair = new LinkedHashMap<>();
        for (Card c : cards) {
            Card existing = byPair.get(c.getPairId());
            if (existing == null || (existing.getImageURL() == null && c.getImageURL() != null)) {
                byPair.put(c.getPairId(), c);
            }
        }
        return new ArrayList<>(byPair.values());
    }

}
