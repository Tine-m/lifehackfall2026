
package app.controllers.teamb;

import app.entities.teamb.Card;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.services.teamb.MemoService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MemoController {

    private final MemoService memoService;

    public MemoController(ConnectionPool connectionPool) {
        this.memoService = new MemoService(connectionPool);
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
        try {
            ctx.render("teamb/results.html",
                    memoService.getResultsModel(ctx.queryParam("mode")));
        } catch (DatabaseException e) {
            ctx.status(500);
        }
    }



    public void renderGame(Context ctx) {
        String mode = "classic".equals(ctx.queryParam("mode")) ? "classic" : "lifehack";
        boolean lifehack = "lifehack".equals(mode);

        try {
            List<Card> cards = memoService.getGameCards(lifehack);

            // Gem ét kort pr. par i sessionen, så resultatsiden kan vise lifehacks
            ctx.sessionAttribute("gameCards", lifehack ? memoService.getLifehackCards(cards) : null);

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
        try {
            ctx.render("teamb/results.html",
                    memoService.getResultsModel(
                            ctx.formParam("time"),
                            ctx.formParam("moves"),
                            ctx.formParam("mode"),
                            ctx.formParam("completed"),
                            ctx.formParam("name"),
                            ctx.sessionAttribute("gameCards")));
        } catch (DatabaseException e) {
           ctx.status(500);
        }
    }

}
