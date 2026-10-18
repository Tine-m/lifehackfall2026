package app.controllers.teamteachers;
import app.entities.teamteachers.Quote;
import app.persistence.ConnectionPool;
import app.services.teamteachers.QuoteService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

//Håndterer teamteachers' funktionalitet
public class QuoteController {

    private ConnectionPool connectionPool;
    private QuoteService quoteService;

    public QuoteController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.quoteService = new QuoteService(connectionPool);
    }

    public void setRoutes(JavalinConfig config) {
        config.routes.get("/philosophers", ctx -> ctx.render("teamteachers/frontpage.html"));
        config.routes.get("/philosophers/ask", ctx -> ask(ctx));
    }

    private void ask(@NotNull Context ctx) {
        String input = ctx.queryParam("ask");
        Quote philosophicalAnswer = quoteService.getPhilosophicalAnswer(input);
        ctx.attribute("quote", philosophicalAnswer.getQuote());
        ctx.attribute("philosopher", philosophicalAnswer.getPhilosopher());
        ctx.attribute("philosopherImage", philosophicalAnswer.getPicture());
        ctx.render("teamteachers/frontpage.html");
    }


}
