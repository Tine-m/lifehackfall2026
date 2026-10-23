package app.controllers.teamc;

import app.entities.teamc.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.services.teamc.EventService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

//Håndterer teamteachers' funktionalitet
public class EventController {

    private ConnectionPool connectionPool;
    private EventService eventService;

    public EventController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.eventService = new EventService(connectionPool);
    }

    public void setRoutes(JavalinConfig config) {

        config.routes.get("/eventPlanner", ctx -> ctx.render("teamc/login.html"));
        config.routes.post("/eventPlanner/mainpage", ctx -> loginController(ctx));
        config.routes.get("/eventPlanner/register", ctx -> ctx.render("teamc/register.html"));
        config.routes.post("/eventPlanner/register/userInsert", ctx  -> register(ctx));

    }

    public void loginController (Context ctx) throws DatabaseException {

        String email = ctx.formParam("mail");
        String password = ctx.formParam("password");

        User user = eventService.login(email,password);

        if (user != null){
            ctx.sessionAttribute("user", user);
            ctx.render("/teamc/mainpage.html");
        } else {
            ctx.status(401);
            ctx.attribute("msg",
                    "E-mail eller adgangskode er forkert.");
            ctx.render("teamc/login.html");
        }

    }

    public void register(Context ctx) throws DatabaseException {

        String username = ctx.formParam("username");
        String email = ctx.formParam("mail");
        String password = ctx.formParam("password");
        String password2 = ctx.formParam("password2");

        if (password == null || !password.equals(password2)) {
            ctx.attribute("msg", "Adgangskoderne er ikke ens.");
            ctx.render("teamc/register.html");
            return;
        }

        User newUser = eventService.createUser(username, password, email);
        System.out.println(newUser);

        if (newUser != null) {
            ctx.attribute("msg", "Din konto er oprettet. Du kan nu logge ind.");
            ctx.render("teamc/login.html");
        } else {
            String msg = "Brugernavnet findes allerede, eller adgangskoden opfylder ikke kravene.";
            ctx.attribute("msg", msg);
            ctx.render("teamc/register.html");
        }
    }



}
