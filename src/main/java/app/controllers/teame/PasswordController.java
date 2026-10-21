
package app.controllers.teame;

import io.javalin.config.JavalinConfig;

public class PasswordController {

    // Registrerer SecurePass' forside i Javalin.
    public void setRoutes(JavalinConfig config) {

        config.routes.get("/securepass", ctx -> {
            ctx.render("teame/password-generator.html");
        });

    }
}
