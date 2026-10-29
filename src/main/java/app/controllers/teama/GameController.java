package app.controllers.teama;

import app.entities.teama.AttackModeScore;
import app.entities.teama.Colour;
import app.entities.teama.Game;
import app.entities.teama.Highscore;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.services.teama.ColourService;
import app.services.teama.ScoreService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
        config.routes.get("/rcade/nextLevel", ctx -> renderNextLevel(ctx));
        config.routes.get("/rcade/playAgain", ctx -> renderSameLevel(ctx));


        config.routes.post("/rcade/submit", ctx -> submitGuess(ctx));
        config.routes.post("/rcade/savescore", ctx -> saveScore(ctx));
    }

    //Render same level for when the user wants to "Try again"
    private void renderSameLevel(Context ctx) {
        Game game = ctx.sessionAttribute("game");
        switch (game.getLevel()) {
            case 1:
                ctx.redirect("/rcade/niveau1");
                break;
            case 2:
                ctx.redirect("/rcade/niveau2");
                break;
            case 3:
                ctx.redirect("/rcade/niveau3");
                break;
            default:
                ctx.redirect("/rcade");
        }

    }

    //Send user to the next level
    private void renderNextLevel(Context ctx) {
        Game game = ctx.sessionAttribute("game");

        switch (game.getLevel()) {
            case 1:
                ctx.redirect("/rcade/niveau2");
                break;
            case 2:
                ctx.redirect("/rcade/niveau3");
                break;
            default:
                ctx.redirect("/rcade");
        }
    }

    public void renderFrontpage(Context ctx) {
        setHighscoreAttributes(ctx);

        ctx.sessionAttribute("niveau1Status", "active");
        ctx.sessionAttribute("attackModeStatus", "active");

        ctx.sessionAttribute("isAttackMode", false);

        ctx.render("teama/frontpage.html");
    }

    //Separate method for fetching highscores, e.g. called when a new highscore is added to make sure the user can see their own score
    public void setHighscoreAttributes(Context ctx){
        try {
            ctx.sessionAttribute("todayList", scoreService.getTopToday(5));
            ctx.sessionAttribute("weekList", scoreService.getTopWeek(5));
            ctx.sessionAttribute("allTimeList", scoreService.getTopAllTime(10));
        } catch (DatabaseException e) {
            System.out.println("Highscores kunne ikke loades");
        }
    }

    public void renderNiveau1(Context ctx) {
        Game game = new Game(colourService.getRandomColour(), 1);

        ctx.sessionAttribute("game", game);

        ctx.attribute("targetColour", game.getTarget().toRgb());

        ctx.render("teama/niveau1.html");
    }

    public void renderNiveau2(Context ctx) {
        Game game = new Game(colourService.getRandomColour(), 2);

        ctx.sessionAttribute("game", game);

        ctx.attribute("targetColour", game.getTarget().toRgb());

        ctx.render("teama/niveau2.html");
    }

    public void renderNiveau3(Context ctx) {
        Game game = new Game(colourService.getRandomColour(), 3);

        ctx.sessionAttribute("game", game);

        ctx.attribute("targetColour", game.getTarget().toRgb());

        ctx.render("teama/niveau3.html");
    }

    public void renderAttackmode(Context ctx) {
        ctx.sessionAttribute("isAttackMode", true);

        AttackModeScore scores = new AttackModeScore();
        ctx.sessionAttribute("attackModeScores", scores);

        ctx.redirect("/rcade/niveau1");
    }

    //Handling when a user submits a guess, grabbing their stored guess values and comparing with set minimum scores for unlocking the next level
    public void submitGuess(Context ctx){
        Game game = ctx.sessionAttribute("game");

        int redGuess = Integer.parseInt(ctx.formParam("redGuess"));
        int greenGuess = Integer.parseInt(ctx.formParam("greenGuess"));
        int blueGuess = Integer.parseInt(ctx.formParam("blueGuess"));

        game.setGuess(new Colour(redGuess, greenGuess, blueGuess));

        game.setScore(colourService.getScore(game.getGuess(), game.getTarget()));

        // If playing attackmode save score and go to next level
        if (ctx.sessionAttribute("isAttackMode")) {
            AttackModeScore scores = ctx.sessionAttribute("attackModeScores");
            switch (game.getLevel()) {
                case 1:
                    scores.setScore1(game.getScore());
                    break;
                case 2:
                    scores.setScore2(game.getScore());
                    break;
                case 3:
                    scores.setScore3(game.getScore());
            }
            ctx.sessionAttribute("attackModeScores", scores);

            if (!(game.getLevel() == 3)) ctx.redirect("/rcade/nextLevel");

            String msg = scoreService.highscoreCheck(game);
            ;
            boolean onHighscore = !msg.equals("Error: Highscores could not be loaded") && !msg.equals("You did not make the leaderboard");

            ctx.attribute("highscoreMessage", msg);
            ctx.attribute("onHighscore", onHighscore);
        }

        if (scoreService.unlockNextLevel(game)) {
            switch (game.getLevel()) {
                case 1:
                    ctx.sessionAttribute("niveau2Status", "active");
                    break;
                case 2:
                    ctx.sessionAttribute("niveau3Status", "active");
                    break;
                case 3:
                    ctx.sessionAttribute("attackModeStatus", "active");
                    break;
            }
            ctx.attribute("isNextLevelOpen", true);
        }
        ctx.render("teama/result.html");
    }

    //Handling actually saving a highscore to the database
    public void saveScore(Context ctx){
        Game game = ctx.sessionAttribute("game");

        String initials = ctx.formParam("initials").toUpperCase();

        Highscore highscore = new Highscore(game.getScore(), initials);
        try {
            scoreService.saveScore(highscore);
        }
        catch(DatabaseException e){
            System.out.println("Highscore could not be saved: " + e);
        }

        //"Reloading" highscores for the leaderboard to include the newly saved score.
        setHighscoreAttributes(ctx);

        ctx.render("teama/result.html");
    }
}
