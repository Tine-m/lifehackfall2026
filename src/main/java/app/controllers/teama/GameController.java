package app.controllers.teama;

import app.entities.teama.Colour;
import app.entities.teama.Game;
import app.entities.teama.Highscore;
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
        config.routes.post("/rcade/savescore", ctx -> saveScore(ctx));
    }

    public void renderFrontpage(Context ctx){
        try {
            ctx.sessionAttribute("todayList", scoreService.getTopToday(5));
            ctx.sessionAttribute("weekList", scoreService.getTopWeek(5));
            ctx.sessionAttribute("allTimeList", scoreService.getTopAllTime(10));
        } catch (DatabaseException e) {
            System.out.println("Highscores kunne ikke loades");
        }

        ctx.sessionAttribute("niveau1Status", "active");
        ctx.render("teama/frontpage.html");
    }

    public void renderNiveau1(Context ctx){
        currentGame = new Game(colourService.getRandomColour(), 1);
        ctx.attribute("targetColour", currentGame.getTarget().toRgb());

        ctx.render("teama/niveau1.html");
    }

    public void renderNiveau2(Context ctx){
        currentGame = new Game(colourService.getRandomColour(), 2);
        ctx.attribute("targetColour", currentGame.getTarget().toRgb());

        ctx.render("teama/niveau2.html");
    }

    public void renderNiveau3(Context ctx){
        currentGame = new Game(colourService.getRandomColour(), 3);
        ctx.attribute("targetColour", currentGame.getTarget().toRgb());

        ctx.render("teama/niveau3.html");
    }

    public void renderAttackmode(Context ctx){
        currentGame = new Game(colourService.getRandomColour(), 0);
        ctx.attribute("targetColour", currentGame.getTarget().toRgb());

        ctx.render("teama/attackmode.html");
    }

    public void submitGuess(Context ctx){
        int redGuess = Integer.parseInt(ctx.formParam("redGuess"));
        int greenGuess = Integer.parseInt(ctx.formParam("greenGuess"));
        int blueGuess = Integer.parseInt(ctx.formParam("blueGuess"));
        currentGame.setGuess(new Colour(redGuess, greenGuess, blueGuess));
        int score = colourService.getScore(currentGame.getGuess(), currentGame.getTarget());

        ctx.attribute("targetColour", currentGame.getTarget());
        ctx.attribute("guessColour", currentGame.getGuess());
        ctx.attribute("score", score);

        if (score >= currentGame.getBenchmarkScore()){
            switch (currentGame.getLevel()){
                case 1:
                    ctx.sessionAttribute("niveau2Status", "active");
                case 2:
                    ctx.sessionAttribute("niveau3Status", "active");
            }
        }

        ctx.render("teama/result.html");
    }

    public void saveScore(Context ctx){
        String initials = ctx.formParam("initials");
        int score = Integer.parseInt(ctx.attribute("score"));

        Highscore highscore = new Highscore(score, initials);
        try {
            scoreService.saveScore(highscore);
        }
        catch(DatabaseException e){
            System.out.println("Highscore kunne ikke gemmes " + e);
        }

    }
}
