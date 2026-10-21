    package app.controllers.teamd;

    import app.entities.teamd.Brand;
    import app.exceptions.DatabaseException;
    import app.persistence.ConnectionPool;
    import app.persistence.teamd.BrandMapper;
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
            config.routes.get("/perfectfit/calculate", ctx -> showCalculator(ctx));
        }

        // Viser beregn-siden med mærkerne fra databasen i dropdown'en
        private void showCalculator(Context ctx) {
            try {
                List<Brand> brands = BrandMapper.getAllBrands(connectionPool);
                ctx.attribute("brands", brands);
                ctx.render("teamd/calculate.html");
            } catch (DatabaseException e) {
                ctx.attribute("message", "Mærkerne kunne ikke hentes. Prøv igen senere.");
                ctx.render("teamd/calculate.html");
            }
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
