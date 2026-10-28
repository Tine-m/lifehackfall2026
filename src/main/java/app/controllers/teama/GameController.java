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
        setHighscoreAttributes(ctx);

        ctx.sessionAttribute("niveau1Status", "active");
        ctx.render("teama/frontpage.html");
    }

    public void setHighscoreAttributes(Context ctx){
        try {
            ctx.sessionAttribute("todayList", scoreService.getTopToday(5));
            ctx.sessionAttribute("weekList", scoreService.getTopWeek(5));
            ctx.sessionAttribute("allTimeList", scoreService.getTopAllTime(10));
        } catch (DatabaseException e) {
            System.out.println("Highscores kunne ikke loades");
        }
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

        ctx.attribute("targetColour", game.getTarget().toRgb());

        ctx.render("teama/niveau3.html");
    }

    public void renderAttackmode(Context ctx){
        ctx.sessionAttribute("isAttackMode", true);

        ctx.redirect("teama/niveau1");
    }

    public String highScoreCheck (Context ctx) {  //tjekker om den aktuelle score på det aktuelle niveau er højere end den laveste værdi på listen
        Game game = ctx.sessionAttribute("game");

        List <Highscore> todayList=null;
        List <Highscore> weekList=null;
        List <Highscore> allTimeList = null;

        try {
            todayList = scoreService.getTopToday(5);
            weekList = scoreService.getTopWeek(5);
            allTimeList = scoreService.getTopAllTime(10);
        } catch (DatabaseException e) {
            System.out.println("Highscores kunne ikke loades");
        }

        if(allTimeList.size()<10){
            return "allTimeList";
        }
        if (weekList.size()<5){
            return "weekList";
        }
        if(todayList.size()<5){
            return "todayList";
        } else switch (game.getLevel()) {

            case 1:
                if (game.getScore() > allTimeList.getLast().getScore1()) {
                    return "allTimeList";
                } else if(game.getScore()>weekList.getLast().getScore1()) {
                    return "weekList";
                } else if (game.getScore()>todayList.getLast().getScore1()) {
                    return "todayList";
                }
                break;
            case 2:
                if (game.getScore() > allTimeList.getLast().getScore2()) {
                    return "allTimeList";
                } else if(game.getScore()>weekList.getLast().getScore2()) {
                    return "weekList";
                } else if (game.getScore()>todayList.getLast().getScore2()) {
                    return "todayList";
                }
                break;
            case 3:
                if (game.getScore() > allTimeList.getLast().getScore3()) {
                    return "allTimeList";
                } else if(game.getScore()>weekList.getLast().getScore3()) {
                    return "weekList";
                } else if (game.getScore()>todayList.getLast().getScore3()) {
                    return "todayList";
                }
                break;
        }
        return "You did not make the highscore";
    }

    public void submitGuess(Context ctx){

        Game game = ctx.sessionAttribute("game");
        boolean onHighscore=false;
        ctx.attribute("onHighscore", onHighscore);

        int redGuess = Integer.parseInt(ctx.formParam("redGuess"));
        int greenGuess = Integer.parseInt(ctx.formParam("greenGuess"));
        int blueGuess = Integer.parseInt(ctx.formParam("blueGuess"));
        game.setGuess(new Colour(redGuess, greenGuess, blueGuess));

        game.setScore(colourService.getScore(game.getGuess(), game.getTarget()));

        String highscore=highScoreCheck(ctx);
        if(!highscore.equals("You did not make the highscore")){
            onHighscore=true;
            setHighscoreAttributes(ctx);
        }

        if (scoreService.unlockNextLevel(game)){
            switch (game.getLevel()){
                case 1:
                    ctx.sessionAttribute("niveau2Status", "active");
                    break;
                case 2:
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
      //  int score = Integer.parseInt(ctx.attribute("score"));

        Highscore highscore = new Highscore(game.getScore(), initials);
        try {
            scoreService.saveScore(highscore);
        }
        catch(DatabaseException e){
            System.out.println("Highscore kunne ikke gemmes " + e);
        }

    }
}
