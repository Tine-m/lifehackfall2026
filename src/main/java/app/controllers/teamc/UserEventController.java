package app.controllers.teamc;

import app.entities.teamc.Invitation;
import app.entities.teamc.Response;
import app.entities.teamc.User;
import app.entities.teamc.UserEvent;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.services.teamc.UserEventService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class UserEventController {
    private ConnectionPool connectionPool;
    private UserEventService userEventService;

    public UserEventController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.userEventService = new UserEventService(connectionPool);
    }

    public void setRoutes(JavalinConfig config){
        config.routes.get("/eventPlanner/createEvent", ctx -> showCreateEvent(ctx));
        config.routes.post("/eventPlanner/createEvent", ctx -> createEvent(ctx));
        config.routes.get("/eventPlanner/myEvents", ctx -> myEvents(ctx));
        config.routes.get("/eventPlanner/event/{id}/participants", ctx -> participants(ctx));
        config.routes.get("/eventPlanner/profile", ctx -> profil(ctx));
        config.routes.get("/eventPlanner/mainpage", ctx -> showMainPage(ctx));
    }

    private void profil(Context ctx) {
        User user = requireLogin(ctx);
        if (user == null) return;

        ctx.attribute("user", user);
        ctx.render("teamc/profil.html");
    }

    private void showMainPage(@NotNull Context ctx) {
        User user = requireLogin(ctx);
        if (user == null) return;
        ctx.render("teamc/mainpage");
    }

    private User requireLogin(Context ctx) {
        User user = ctx.sessionAttribute("user");
        if (user == null) {
            ctx.status(401);
            ctx.attribute("msg", "Du skal være logget ind for at se den side.");
            ctx.render("teamc/login.html");
        }
        return user;
    }

    public void showCreateEvent(Context ctx) {
        User user = requireLogin(ctx);
        if (user == null) return;

        LocalDateTime now = LocalDateTime.now()
                .withSecond(0)
                .withNano(0);

        // Forslag: i morgen kl. 18-21. Så er året allerede udfyldt, og man skal kun rette dag og tid
        LocalDateTime start = LocalDate.now().plusDays(1).atTime(18, 0);
        ctx.attribute("user", user);
        ctx.attribute("now", now);
        ctx.attribute("startDate", start.toString());               // fx 2026-10-24T18:00
        ctx.attribute("finishDate", start.plusHours(3).toString()); // fx 2026-10-24T21:00
        ctx.render("teamc/createevent.html");
    }

    public void createEvent(Context ctx) throws DatabaseException {
        User user = requireLogin(ctx);
        if (user == null) return;

        String eventName = ctx.formParam("eventName");
        String startDate = ctx.formParam("startDate");
        String finishDate = ctx.formParam("finishDate");
        boolean restrictComments = ctx.formParam("restrictComments") != null;   // checkbox sendes kun når den er sat

        try {
            UserEvent event = userEventService.createEvent(user, eventName, startDate, finishDate, restrictComments);
            ctx.sessionAttribute("flash", "Eventet \"" + event.getEventName() + "\" er oprettet.");
            ctx.redirect("/eventPlanner/myEvents");
        } catch (IllegalArgumentException e) {
            // Vis formularen igen med fejlbesked og det brugeren allerede havde skrevet
            ctx.attribute("user", user);
            ctx.attribute("msg", e.getMessage());
            ctx.attribute("eventName", eventName);
            ctx.attribute("startDate", startDate);
            ctx.attribute("finishDate", finishDate);
            ctx.attribute("restrictComments", restrictComments);
            ctx.render("teamc/createevent.html");
        }
    }

    public void myEvents(Context ctx) throws DatabaseException {
        User user = requireLogin(ctx);
        if (user == null) return;

        ctx.attribute("user", user);
        ctx.attribute("events", userEventService.getMyEvents(user));
        ctx.attribute("flash", ctx.consumeSessionAttribute("flash"));   // vises én gang efter oprettelse
        ctx.render("teamc/myevents.html");
    }

    public void participants(Context ctx) throws DatabaseException {
        User user = requireLogin(ctx);
        if (user == null) return;

        int eventId;
        try {
            eventId = Integer.parseInt(ctx.pathParam("id"));
        } catch (NumberFormatException e) {
            ctx.status(404);
            ctx.attribute("user", user);
            ctx.attribute("events", userEventService.getMyEvents(user));
            ctx.attribute("flash", "Eventet findes ikke.");
            ctx.render("teamc/myevents.html");
            return;
        }

        // Kun den der har oprettet eventet må se deltagerne (acceptkriterie 2)
        UserEvent event = userEventService.getEventForOwner(eventId, user);
        if (event == null) {
            ctx.status(403);
            ctx.attribute("user", user);
            ctx.attribute("events", userEventService.getMyEvents(user));
            ctx.attribute("flash", "Du kan kun se deltagere på dine egne events.");
            ctx.render("teamc/myevents.html");
            return;
        }

        List<Invitation> invitations = userEventService.getParticipants(event);
        long yesCount = invitations.stream()
                .filter(i -> i.getResponse() == Response.YES)
                .count();

        ctx.attribute("user", user);
        ctx.attribute("event", event);
        ctx.attribute("invitations", invitations);
        ctx.attribute("yesCount", yesCount);
        ctx.render("teamc/participants.html");
    }

}

