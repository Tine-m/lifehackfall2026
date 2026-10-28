package app.controllers.teamb;

import app.entities.teamb.Card;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.teamb.CardMapper;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

import java.util.List;

public class MemoController {

    private ConnectionPool connectionPool;

    public MemoController(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }
    //test

    public void setRoutes(JavalinConfig config) {
        config.routes.get("/cards", ctx -> getCards(ctx));
        config.routes.get("/memorygame", ctx -> renderFrontpage(ctx));
        config.routes.get("/results", ctx -> resultsPage(ctx));
        config.routes.post("/results", ctx -> resultsPageWithStats(ctx));
        config.routes.get("/game", ctx -> renderGame(ctx));
    }


    public void renderFrontpage(Context ctx) {
        ctx.render("teamb/frontpage.html");
    }

    private void resultsPage(Context ctx) {
        ctx.render("teamb/results.html");

    }

   public void getCards(Context ctx) {
        try {
            List<Card> cards = CardMapper.getRandomCards(8, connectionPool);
            ctx.json(cards);
        } catch (DatabaseException e) {
            ctx.status(500);
        }
    }


    public void renderGame(Context ctx){
        ctx.render("teamb/game.html");
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
