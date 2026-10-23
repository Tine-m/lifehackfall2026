package app;

import app.configuration.ThymeleafConfig;
import app.controllers.MainController;
import app.controllers.teamc.UserEventController;
import app.controllers.teamd.PerfectFitController;
import app.controllers.teama.GameController;
import app.controllers.teamc.EventController;
import app.controllers.teamb.MemoController;
import app.controllers.teame.PasswordController;
import app.controllers.teamteachers.QuoteController;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

// Starter Javalin, konfigurerer applikationen og registrerer controllers...
public class Main {
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "lifehack";
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    public static void main(String[] args) {
        Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
            new MainController().setRoutes(config); // Fælles controller
            // TODO: registrér jeres egen controller endpoints herunder
            new QuoteController(connectionPool).setRoutes(config); // Teacher app Controller

            new EventController(connectionPool).setRoutes(config);
            new GameController(connectionPool).setRoutes(config); //Team a app Controller
            new PerfectFitController(connectionPool).setRoutes(config);
            new MemoController(connectionPool).setRoutes(config); //Team b app Controller
            new UserEventController(connectionPool).setRoutes(config);
            new PasswordController(connectionPool).setRoutes(config);//team e app Controller
            // TODO: Husk også at registrere jeres forside i LifehackRegistry klassen
        }).start(7070);

    }
}