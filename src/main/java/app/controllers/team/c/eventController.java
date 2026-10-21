package app.controllers.team.c;

import app.persistence.ConnectionPool;
import app.services.teamteachers.QuoteService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;


//Håndterer teamteachers' funktionalitet
public class eventController {

    private ConnectionPool connectionPool;
    private QuoteService quoteService;

    public eventController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.quoteService = new QuoteService(connectionPool);
    }

    public void setRoutes(JavalinConfig config) {

        config.routes.get("/eventPlanner", ctx -> ctx.render("team.c/login.html"));

        config.routes.post("/eventPlanner", ctx -> ctx.render("team.c/login.html"));



        config.routes.post("/eventPlanner/mainpage", ctx -> mainpage(ctx));


        config.routes.get("/eventPlanner/register", ctx -> register(ctx));
        config.routes.post("/eventPlanner/register", ctx -> events(ctx));


    }

public void events(Context ctx ){
        ctx.render("/team.c/login.html");


}

public void login (Context ctx) {
        ctx.render("/team.c/mainpage.html");

}

public void register (Context ctx) {
        ctx.render("/team.c/register.html");

    }
public void mainpage(Context ctx){
        ctx.render("/team.c/mainpage.html");

}


}
