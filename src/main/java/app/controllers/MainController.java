package app.controllers;

import app.configuration.LifehackRegistry;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

public class MainController {

    public void setRoutes(JavalinConfig config) {
        config.routes.get("/", ctx -> index(ctx));
    }

    private void index(Context ctx) {
        ctx.attribute("lifehackSites",
                LifehackRegistry.getSites());

        ctx.render("index.html");
    }
}