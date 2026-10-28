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
import org.jetbrains.annotations.NotNull;

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

    public void renderFrontpage(Context ctx){
        try {
            ctx.sessionAttribute("todayList", scoreService.getTopToday(5));
            ctx.sessionAttribute("weekList", scoreService.getTopWeek(5));
            ctx.sessionAttribute("allTimeList", scoreService.getTopAllTime(10));
        } catch (DatabaseException e) {
            System.out.println("Highscores kunne ikke loades");
        }

        ctx.sessionAttribute("niveau1Status", "active");
        ctx.sessionAttribute("niveau2Status", "pending");
        ctx.sessionAttribute("niveau3Status", "pending");
        ctx.render("teama/frontpage.html");
    }

    public void renderNiveau1(Context ctx){
        Game game = new Game(colourService.getRandomColour(), 1);

        ctx.sessionAttribute("game", game);

        ctx.attribute("targetColour", game.getTarget().toRgb());

        ctx.render("teama/niveau1.html");
    }

    public void renderNiveau2(Context ctx){
        Game game = new Game(colourService.getRandomColour(), 2);

        ctx.sessionAttribute("game", game);

        ctx.attribute("targetColour", game.getTarget().toRgb());

        ctx.render("teama/niveau2.html");
    }

    public void renderNiveau3(Context ctx){
        Game game = new Game(colourService.getRandomColour(), 3);

        ctx.sessionAttribute("game", game);

        ctx.render("teama/niveau3.html");
    }

    public void renderAttackmode(Context ctx){
        Game game = new Game(colourService.getRandomColour(), 0);

        ctx.sessionAttribute("game", game);

        ctx.render("teama/attackmode.html");
    }

    public void submitGuess(Context ctx){

        Game game = ctx.sessionAttribute("game");

        int redGuess = Integer.parseInt(ctx.formParam("redGuess"));
        int greenGuess = Integer.parseInt(ctx.formParam("greenGuess"));
        int blueGuess = Integer.parseInt(ctx.formParam("blueGuess"));
        game.setGuess(new Colour(redGuess, greenGuess, blueGuess));

        game.setScore(colourService.getScore(game.getGuess(), game.getTarget()));

        //Her skal der tjekkes om scoren skal på et leaderboard.


        // Hvis scoren er høj nok, bliver næste niveau unlocked.
        if (scoreService.unlockNextLevel(game)){
            switch (game.getLevel()){
                case 1:
                    // Midlertidig kode. Tjekker om den relevante side er "pending" dvs. ikke færdig
                    if (ctx.sessionAttribute("niveau2Status") == "pending") {break;}

                    ctx.sessionAttribute("niveau2Status", "active");
                    break;
                case 2:
                    // Midlertidig kode. Tjekker om den relevante side er "pending" dvs. ikke færdig
                    if (ctx.sessionAttribute("niveau3Status") == "pending") {break;}

                    ctx.sessionAttribute("niveau3Status", "active");
                    break;
            }
            ctx.attribute("isNextLevelOpen", true);
        }
        ctx.render("teama/result.html");
    }

    public void saveScore(Context ctx){
        Game game = ctx.sessionAttribute("game");

        String initials = ctx.formParam("initials");

        Highscore highscore = new Highscore(game.getScore(), initials);
        try {
            scoreService.saveScore(highscore);
        }
        catch(DatabaseException e){
            System.out.println("Highscore kunne ikke gemmes " + e);
        }

    }
}
