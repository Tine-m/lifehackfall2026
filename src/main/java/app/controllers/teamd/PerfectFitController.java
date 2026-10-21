    package app.controllers.teamd;

    import app.persistence.ConnectionPool;
    import io.javalin.config.JavalinConfig;
    import io.javalin.http.Context;

    import java.util.List;

    public class PerfectFitController {
        private ConnectionPool connectionPool;

        public PerfectFitController(ConnectionPool connectionPool){
            this.connectionPool = connectionPool;
        }

        public void setRoutes(JavalinConfig config){
            config.routes.get("/perfectfit", ctx -> showFrontpage(ctx));
            config.routes.post("/perfectfit/resultat", ctx -> showResult(ctx));
        }

        private void showFrontpage(Context ctx) {
            ctx.render("teamd/frontpage.html");
        }

        private void showResult(Context ctx){
            // Testværdier: skal erstattes med data fra formularen og mapperen
            ctx.attribute("brandName", "Hunkemøller");
            ctx.attribute("size", "75C");
            ctx.attribute("sizeSystem", "EU");
            ctx.attribute("underbust", 75);
            ctx.attribute("bust", 90);
            ctx.attribute("sisterSizes", List.of("70D", "80B"));
            ctx.render("teamd/result.html");
        }
    }
