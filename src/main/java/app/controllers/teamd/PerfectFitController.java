    package app.controllers.teamd;

    import app.persistence.ConnectionPool;
    import io.javalin.config.JavalinConfig;
    import io.javalin.http.Context;

    public class PerfectFitController {
        private ConnectionPool connectionPool;

        public PerfectFitController(ConnectionPool connectionPool){
            this.connectionPool = connectionPool;
        }

        public void setRoutes(JavalinConfig config){

            config.routes.get("/perfectfit", ctx -> showFrontpage(ctx));
            config.routes.get("/perfectfit/calculate", ctx -> showCalculate(ctx));
            config.routes.get("/perfectfit/measure", ctx -> showMeasure(ctx));
        }

        private void showFrontpage(Context ctx) {

            ctx.render("teamd/frontpage.html");
        }

        private void showCalculate(Context ctx) {
            ctx.render("teamd/calculate.html");
        }

        private void showMeasure(Context ctx) {
            ctx.render("teamd/measure.html");
        }

    }
