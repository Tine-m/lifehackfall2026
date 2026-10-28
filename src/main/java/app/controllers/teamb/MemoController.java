
package app.controllers.teamb;

import app.entities.teamb.Card;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.services.teamb.MemoService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoController {

    private final MemoService memoService;

    public MemoController(ConnectionPool connectionPool) {
        this.memoService = new MemoService(connectionPool);
    }

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
            ctx.sessionAttribute("savedGameId", null);

            Map<String, Object> model = new HashMap<>();
            model.put("mode", mode);
            model.put("cards", cards);
            model.put("totalPairs", cards.size() / 2);

            ctx.render("teamb/game.html", model);
        } catch (DatabaseException e) {
           ctx.status(500);
        }
    }


        // POST /results: bruges både når spillet slutter og når spilleren gemmer sit navn.
        private void resultsPageWithStats(Context ctx) {
            try {
                String time = ctx.formParam("time");
                String moves = ctx.formParam("moves");
                String mode = ctx.formParam("mode");
                String completed = ctx.formParam("completed");
                String name = ctx.formParam("name");

                Integer savedGameId = ctx.sessionAttribute("savedGameId");

                boolean wantsToSave = name != null
                        && !name.trim().isEmpty()
                        && Boolean.parseBoolean(completed);

                // Undgå at gemme det samme spil igen ved gentagne POST-requests.
                if (wantsToSave && savedGameId != null) {
                    Map<String, Object> model = memoService.getResultsModel(
                            time, moves, mode, completed, null,
                            ctx.sessionAttribute("gameCards"));

                    model.put("canSave", false);
                    model.put("highlight", savedGameId);

                    ctx.render("teamb/results.html", model);
                    return;
                }

                Map<String, Object> model = memoService.getResultsModel(
                        time, moves, mode, completed, name,
                        ctx.sessionAttribute("gameCards"));

                // Gem ID'et i sessionen, så samme spil ikke gemmes igen.
                if (wantsToSave) {
                    ctx.sessionAttribute("savedGameId", model.get("highlight"));
                }

                ctx.render("teamb/results.html", model);

            } catch (DatabaseException e) {
                ctx.status(500);
            }
        }

}


