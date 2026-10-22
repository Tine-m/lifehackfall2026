package app.controllers.teamc;

import app.persistence.ConnectionPool;
import app.services.teamteachers.QuoteService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;


//Håndterer teamteachers' funktionalitet
public class EventController {

    private ConnectionPool connectionPool;
    private QuoteService quoteService;

    public EventController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.quoteService = new QuoteService(connectionPool);
    }

    public void setRoutes(JavalinConfig config) {

        config.routes.get("/eventPlanner", ctx -> ctx.render("teamc/login.html"));

        config.routes.post("/eventPlanner", ctx -> ctx.render("teamc/login.html"));



        config.routes.post("/eventPlanner/mainpage", ctx -> mainpage(ctx));


        config.routes.get("/eventPlanner/register", ctx -> register(ctx));
        config.routes.post("/eventPlanner/register", ctx -> events(ctx));


    }

public void events(Context ctx ){
        ctx.render("/teamc/login.html");


}

public void login (Context ctx) {
        ctx.render("/teamc/mainpage.html");

}

public void register (Context ctx) {
        ctx.render("/teamc/register.html");

    }
public void mainpage(Context ctx){
        ctx.render("/teamc/mainpage.html");

}


}
