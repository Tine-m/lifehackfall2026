package app.controllers.teamb;

import app.entities.teamb.Card;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.teamb.CardMapper;
import app.persistence.teamb.GameMapper;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

import java.util.HashMap;
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

    private void resultsPage(Context ctx) {
        String mode = "classic".equals(ctx.queryParam("mode")) ? "classic" : "lifehack";
        boolean lifehack = "lifehack".equals(mode);

        try {
            ctx.attribute("mode", mode);
            ctx.attribute("leaderboard", GameMapper.getTopGames(lifehack, 10, connectionPool));
            ctx.render("teamb/results.html");
        } catch (DatabaseException e) {
            ctx.status(500);
        }
    }

    private void resultsPageWithStats(Context ctx) {
        int seconds = Integer.parseInt(ctx.formParam("time"));
        int moves = Integer.parseInt(ctx.formParam("moves"));
        String mode = "classic".equals(ctx.formParam("mode")) ? "classic" : "lifehack";
        boolean lifehack = "lifehack".equals(mode);
        boolean completed = Boolean.parseBoolean(ctx.formParam("completed"));
        String name = ctx.formParam("name");

        String formattedTime = String.format("%02d:%02d", seconds / 60, seconds % 60);

        try {
            boolean saving = name != null && !name.isBlank();

            if (saving && completed) {
                int gameId = GameMapper.saveGame(name.trim(), lifehack, moves, seconds, connectionPool);
                ctx.attribute("highlight", gameId);
            }

            ctx.attribute("time", formattedTime);
            ctx.attribute("seconds", seconds);
            ctx.attribute("moves", moves);
            ctx.attribute("mode", mode);
            ctx.attribute("completed", completed);
            ctx.attribute("canSave", completed && !saving);
            ctx.attribute("leaderboard", GameMapper.getTopGames(lifehack, 10, connectionPool));

            ctx.render("teamb/results.html");
        } catch (DatabaseException e) {
            ctx.status(500);
        }
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

            ctx.render("teamb/game.html", model);

        } catch (DatabaseException e) {
            ctx.status(500);
        }
    }



}
