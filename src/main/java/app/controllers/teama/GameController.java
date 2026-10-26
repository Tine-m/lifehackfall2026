package app.controllers.teama;

import app.entities.teama.Colour;
import app.entities.teama.Game;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.services.teama.ColourService;
import app.services.teama.ScoreService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GameController {

    private ConnectionPool connectionPool;
    private ColourService colourService;
    private ScoreService scoreService;
    private Game currentGame;

    public GameController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.colourService = new ColourService(connectionPool);
        this.scoreService = new ScoreService(connectionPool);
    }

    public void setRoutes(JavalinConfig config) {
        config.routes.get("/rcade", ctx -> renderFrontpage(ctx));
        config.routes.get("/rcade/niveau1", ctx -> renderNiveau1(ctx));
        config.routes.get("/rcade/niveau2", ctx -> renderNiveau2(ctx));
        config.routes.get("/rcade/niveau3", ctx -> renderNiveau3(ctx));
        config.routes.get("/rcade/attackmode", ctx -> renderAttackmode(ctx));

        config.routes.post("/rcade/niveau1", ctx -> submitGuess(ctx));
    }

    public void renderFrontpage(Context ctx){
        try {
            ctx.attribute("todayList", scoreService.getTopToday(5));
            ctx.attribute("weekList", scoreService.getTopWeek(5));
            ctx.attribute("allTimeList", scoreService.getTopAllTime(10));
        } catch (DatabaseException e) {
            System.out.println("Highscores kunne ikke loades");
        }

        ctx.sessionAttribute("niveau1Status", "active");
        ctx.sessionAttribute("niveau2Status", null);
        ctx.sessionAttribute("niveau3Status", null);
        ctx.sessionAttribute("attackModeStatus", null);
        ctx.render("teama/frontpage.html");
    }

    public void renderNiveau1(Context ctx){
        currentGame = new Game(colourService.getRandomColour(), 1);
        ctx.attribute("targetColour", currentGame.getTarget().toRgb());

        Map<String, Object> model = new HashMap<>();
        model.put("redGuess", 0);
        model.put("greenGuess", 0);
        model.put("blueGuess", 0);

        ctx.render("teama/niveau1.html", model);
    }

    public void renderNiveau2(Context ctx){
        currentGame = new Game(colourService.getRandomColour(), 2);
        ctx.attribute("targetColour", currentGame.getTarget().toRgb());

        Map<String, Object> model = new HashMap<>();
        model.put("redGuess", 0);
        model.put("greenGuess", 0);
        model.put("blueGuess", 0);

        ctx.render("teama/niveau2.html", model);
    }

    public void renderNiveau3(Context ctx){
        currentGame = new Game(colourService.getRandomColour(), 3);
        ctx.attribute("targetColour", currentGame.getTarget().toRgb());

        Map<String, Object> model = new HashMap<>();
        model.put("redGuess", 0);
        model.put("greenGuess", 0);
        model.put("blueGuess", 0);

        ctx.render("teama/niveau3.html", model);
    }

    public void renderAttackmode(Context ctx){
        currentGame = new Game(colourService.getRandomColour(), 0);
        ctx.attribute("targetColour", currentGame.getTarget().toRgb());

        Map<String, Object> model = new HashMap<>();
        model.put("redGuess", 0);
        model.put("greenGuess", 0);
        model.put("blueGuess", 0);

        ctx.render("teama/attackmode.html", model);
    }

    public void submitGuess(Context ctx){
        int redGuess = Integer.parseInt(ctx.formParam("redGuess"));
        int greenGuess = Integer.parseInt(ctx.formParam("greenGuess"));
        int blueGuess = Integer.parseInt(ctx.formParam("blueGuess"));
        currentGame.setGuess(new Colour(redGuess, greenGuess, blueGuess));

        ctx.attribute("targetColour", currentGame.getTarget());
        ctx.attribute("guessColour", currentGame.getGuess());
        ctx.attribute("score", colourService.getScore(currentGame.getGuess(), currentGame.getTarget()));

        ctx.render("teama/result.html");
    }
}
