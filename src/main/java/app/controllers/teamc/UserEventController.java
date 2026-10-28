package app.controllers.teamc;

import app.entities.teamc.*;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.services.teamc.EventService;
import app.services.teamc.UserEventService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class UserEventController {
    private ConnectionPool connectionPool;
    private UserEventService userEventService;
    private EventService eventService;

    public UserEventController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.userEventService = new UserEventService(connectionPool);
        this.eventService = new EventService(connectionPool);
    }

    public void setRoutes(JavalinConfig config){

        config.routes.get("/eventPlanner/createEvent", ctx -> showCreateEvent(ctx));
        config.routes.post("/eventPlanner/createEvent", ctx -> createEvent(ctx));
        config.routes.get("/eventPlanner/myEvents", ctx -> myEvents(ctx));
        config.routes.get("/eventPlanner/event/{id}/participants", ctx -> participants(ctx));
        config.routes.get("/eventPlanner/profile", ctx -> profil(ctx));
        config.routes.get("/eventPlanner/mainpage", ctx -> showMainPage(ctx));
        config.routes.get("/eventPlanner/inbox", ctx -> inbox(ctx));
        config.routes.post("/eventPlanner/inbox/{invitationId}/accept", ctx -> acceptInvitation(ctx));
        config.routes.post("/eventPlanner/inbox/{invitationId}/decline", ctx -> declineInvitation(ctx));
        config.routes.post("/eventPlanner/event/{id}/comments", ctx -> addComment(ctx));

    }

    private void profil(Context ctx) throws DatabaseException {

        User user = requireLogin(ctx);
        if (user == null) return;

        List<UserEvent> attendedEvents = userEventService.getPastEvents(user);
        ctx.attribute("attendedEvents", attendedEvents);
        ctx.attribute("user", user);
        ctx.render("teamc/profil.html");
    }

    private void showMainPage(Context ctx) throws DatabaseException {
        User user = requireLogin(ctx);
        if (user == null) return;

        String search = ctx.formParam("search");
        ctx.attribute("search", search);

        if (search == null) {

            List<UserEvent> events = userEventService.getAcceptedEvents(user);
            events.addAll(userEventService.getMyEvents(user));
            ctx.attribute("user", user);
            ctx.attribute("events", events);
            ctx.render("teamc/mainpage");

        } else if(search != null){

            List<UserEvent> events = userEventService.getAcceptedEvents(user);
            events.addAll(userEventService.getMyEvents(user));

            for (UserEvent event: events){
                if (event.getEventName().toLowerCase().startsWith(search.toLowerCase())){

                    ctx.attribute("user", user);
                    ctx.attribute("events", events);
                    ctx.render("teamc/mainpage");

                    //contains(search)
                }
            }
        }
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

    public void showCreateEvent(Context ctx) throws DatabaseException {
        User user = requireLogin(ctx);
        if (user == null) return;

        LocalDateTime now = LocalDateTime.now()
                .withSecond(0)
                .withNano(0);

        LocalDateTime start = LocalDate.now().plusDays(1).atTime(18, 0);
        ctx.attribute("user", user);
        ctx.attribute("now", now);
        ctx.attribute("startDate", start.toString());               // fx 2026-10-24T18:00
        ctx.attribute("finishDate", start.plusHours(3).toString()); // fx 2026-10-24T21:00
        ctx.attribute("users", eventService.getUsers());
        ctx.render("teamc/createevent.html");
    }

    public void createEvent(Context ctx) throws DatabaseException {
        User user = requireLogin(ctx);
        if (user == null) return;

        String eventName = ctx.formParam("eventName");
        String startDate = ctx.formParam("startDate");
        String finishDate = ctx.formParam("finishDate");
        boolean restrictComments = ctx.formParam("restrictComments") != null;   // checkbox sendes kun når den er sat
        List<String> invites = ctx.formParams("invitees");
        try {
            UserEvent event = userEventService.createEvent(user, eventName, startDate, finishDate, restrictComments, invites);
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
            ctx.attribute("users", eventService.getUsers());
            ctx.attribute("invitees", invites);
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
        // Ejeren og inviterede (uanset svar) må se deltagerne
        UserEvent event = userEventService.getEventForParticipant(eventId, user);
        if (event == null) {
            ctx.status(403);
            ctx.attribute("user", user);
            ctx.attribute("events", userEventService.getMyEvents(user));
            ctx.attribute("flash", "Du kan kun se deltagere på events, du ejer eller er inviteret til.");
            ctx.render("teamc/myevents.html");
            return;
        }
        boolean isOwner = event.getUser().getUser_id() == user.getUser_id();

        List<Invitation> invitations = userEventService.getParticipants(event);
        long yesCount = invitations.stream()
                .filter(i -> i.getResponse() == Response.YES)
                .count();

        ctx.attribute("user", user);
        ctx.attribute("event", event);
        ctx.attribute("invitations", invitations);
        ctx.attribute("yesCount", yesCount);
        ctx.attribute("isOwner", isOwner);
        ctx.render("teamc/participants.html");
    }


    public void inbox(Context ctx) throws DatabaseException {
        User user = requireLogin(ctx);
        if (user == null) return;

        ctx.attribute("user", user);
        ctx.attribute("invitations", userEventService.getMyInvitations(user));
        ctx.attribute("comments", userEventService.getCommentsOnMyEvents(user));
        ctx.render("teamc/inbox.html");
    }

    private void acceptInvitation(Context ctx) throws DatabaseException {
        respond(ctx, Response.YES);
    }

    private void declineInvitation(Context ctx) throws DatabaseException {
        respond(ctx, Response.NO);
    }

    private void respond(Context ctx, Response response) throws DatabaseException {
        User user = requireLogin(ctx);
        if (user == null) return;

        try {
            int invitationId = Integer.parseInt(ctx.pathParam("invitationId"));
            userEventService.respondToInvitation(user, invitationId, response);
        } catch (NumberFormatException e) {
            ctx.status(404);
        }
        ctx.redirect("/eventPlanner/inbox");
    }
    private void addComment(Context ctx) throws DatabaseException {
        User user = requireLogin(ctx);
        if (user == null) return;

        int eventId;
        try {
            eventId = Integer.parseInt(ctx.pathParam("id"));
        } catch (NumberFormatException e) {
            ctx.redirect("/eventPlanner/events");
            return;
        }

        String content = ctx.formParam("content");
        try {
            userEventService.addComment(user, eventId, content);
            ctx.sessionAttribute("flash", "Din kommentar er gemt.");
        } catch (IllegalArgumentException e) {
            ctx.sessionAttribute("flash", e.getMessage());
        }
        ctx.redirect("/eventPlanner/event/" + eventId + "/comments");
    }
}

