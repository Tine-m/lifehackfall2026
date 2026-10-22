package app.controllers.teama;

import app.persistence.ConnectionPool;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

public class GameController {

    private ConnectionPool connectionPool;

    public GameController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void setRoutes(JavalinConfig config) {
        config.routes.get("/rcade", ctx -> renderFrontpage(ctx));
    }

    public void renderFrontpage(Context ctx){
        ctx.render("teama/frontpage.html");
    }
}
