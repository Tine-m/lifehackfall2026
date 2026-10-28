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

    public MemoController(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    public void setRoutes(JavalinConfig config) {
        config.routes.get("/memorygame", ctx -> renderFrontpage(ctx));
        config.routes.get("/game", ctx -> renderGame(ctx));
        config.routes.get("/results", ctx -> resultsPage(ctx));
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

        try {
            List<Card> cards = CardMapper.getRandomCards(8, connectionPool);

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
