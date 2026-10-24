
package app.controllers.teame;

import app.persistence.ConnectionPool;
import io.javalin.config.JavalinConfig;

public class PasswordController {
    private final ConnectionPool connectionPool;

    public PasswordController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
    // Registrerer SecurePass' forside i Javalin.
    public void setRoutes(JavalinConfig config) {

        config.routes.get("/securepass", ctx -> {
            ctx.render("teame/password-generator.html");
        });

    }
}
