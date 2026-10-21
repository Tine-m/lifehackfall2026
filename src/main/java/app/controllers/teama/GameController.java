package app.controllers.teama;

import app.persistence.ConnectionPool;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

import java.awt.*;

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

        config.routes.post("/rcade/niveau1", ctx -> submitGuess(ctx));
    }

    public void renderFrontpage(Context ctx){
        ctx.render("teama/frontpage.html");
    }

    public void renderNiveau1(Context ctx){
        Colour colourTarget = colourService.getRandomColour();
        ctx.attribute("colourTarget", colourTarget);

        ctx.render("teama/niveau1.html");
    }

    public void submitGuess(Context ctx){
        int redGuess = Integer.parseInt(ctx.formParam("redGuess"));
        int greenGuess = Integer.parseInt(ctx.formParam("greenGuess"));
        int blueGuess = Integer.parseInt(ctx.formParam("blueGuess"));

        Colour colourGuess = new Colour(redGuess, greenGuess, blueGuess);
        ctx.attribute("colourGuess", colourService.getScore(colourGuess));

        ctx.render("teama/niveau1.html");
    }
}
