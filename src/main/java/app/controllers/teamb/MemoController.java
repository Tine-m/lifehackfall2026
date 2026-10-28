package app.controllers.teamb;

import app.entities.teamb.Card;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.teamb.CardMapper;
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
       // config.routes.get("/results", ctx -> resultsPageWithStats(ctx));
        config.routes.get("/game", ctx -> renderGame(ctx));
    }


    public void renderFrontpage(Context ctx) {
        ctx.render("teamb/frontpage.html");
    }

    private void resultsPage(Context ctx) {
        ctx.render("teamb/results.html");

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


    private void resultsPageWithStats(Context ctx) {
        int seconds = Integer.parseInt(ctx.formParam("time"));
        int moves = Integer.parseInt(ctx.formParam("moves"));

        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;

        String formattedTime = String.format(
                "%02d:%02d", minutes, remainingSeconds
        );

        ctx.attribute("time", formattedTime);
        ctx.attribute("moves", moves);

        ctx.render("teamb/results.html");
    }


}
