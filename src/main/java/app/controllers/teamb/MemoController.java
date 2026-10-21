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
    config.routes.get("/memo", ctx -> renderFrontpage(ctx));
    }

    public void renderFrontpage(Context ctx){
        ctx.render("teamb/frontpage.html");
    }

}
