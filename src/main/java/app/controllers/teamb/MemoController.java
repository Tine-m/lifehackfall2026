package app.controllers.teamb;

import app.persistence.ConnectionPool;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

public class MemoController {

    private ConnectionPool connectionPool;

    public MemoController(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    public void setRoutes(JavalinConfig config){
    config.routes.get("/memorygame", ctx -> renderFrontpage(ctx));
    config.routes.get("/game", ctx -> renderGame(ctx));
    }


    public void renderFrontpage(Context ctx){
        ctx.render("teamb/frontpage.html");
    }

    public void renderGame(Context ctx){
        ctx.render("teamb/game.html");
    }
    //test

}
