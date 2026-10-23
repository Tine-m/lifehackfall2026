package app.controllers.teama;

import app.entities.teama.Colour;
import app.persistence.ConnectionPool;
import app.services.teama.ColourService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GameController {

    private ConnectionPool connectionPool;
    private ColourService colourService;

    public GameController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.colourService = new ColourService(connectionPool);
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
        ctx.sessionAttribute("niveau1Status", "active");
        ctx.sessionAttribute("niveau2Status", null);
        ctx.sessionAttribute("niveau3Status", null);
        ctx.sessionAttribute("attackModeStatus", null);
        ctx.render("teama/frontpage.html");
    }

    public void renderNiveau1(Context ctx){
        Colour targetColour = colourService.getNewTargetColour();
        ctx.attribute("targetColour", targetColour.toRgb());

        Map<String, Object> model = new HashMap<>();
        model.put("redGuess", 0);
        model.put("greenGuess", 0);
        model.put("blueGuess", 0);

        ctx.render("teama/niveau1.html", model);
    }

    public void renderNiveau2(Context ctx){
        Colour targetColour = colourService.getNewTargetColour();
        ctx.attribute("targetColour", targetColour);

        ctx.render("teama/niveau2.html");
    }

    public void renderNiveau3(Context ctx){
        Colour targetColour = colourService.getNewTargetColour();
        ctx.attribute("targetColour", targetColour);

        ctx.render("teama/niveau3.html");
    }

    public void renderAttackmode(Context ctx){
        Colour targetColour = colourService.getNewTargetColour();
        ctx.attribute("targetColour", targetColour);

        ctx.render("teama/attackmode.html");
    }

    public void submitGuess(Context ctx){
        int redGuess = Integer.parseInt(ctx.formParam("redGuess"));
        int greenGuess = Integer.parseInt(ctx.formParam("greenGuess"));
        int blueGuess = Integer.parseInt(ctx.formParam("blueGuess"));

        Colour guessColour = new Colour(redGuess, greenGuess, blueGuess);
        ctx.attribute("targetColour", colourService.getCurrentTargetColour());
        ctx.attribute("guessColour", guessColour);
        ctx.attribute("guessScore", colourService.getScore(guessColour));

        ctx.render("teama/popup.html");
    }
}
