package app.controllers.teamc;

import app.entities.teamc.User;
import app.entities.teamc.UserEvent;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.services.teamc.CommentService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

public class CommentController {
    private ConnectionPool connectionPool;
    private CommentService commentService;

    public CommentController(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
        this.commentService = new CommentService(connectionPool);
    }

    public void setRoutes(JavalinConfig config){
        config.routes.get("/eventPlanner/invitedEvents", ctx -> InvitedEvents(ctx));
        config.routes.get("/eventPlanner/event/{id}", ctx -> showEvent(ctx));
        config.routes.post("/eventPlanner/event/{id}/comment", ctx -> addComment(ctx));
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

    // Finder eventet ud fra {id} og tjekker at brugeren må se det. Viser "Inviteret til" med en besked hvis ikke.
    private UserEvent findEvent(Context ctx, User user) throws DatabaseException {
        UserEvent event = null;
        try {
            event = commentService.getEventIfAllowed(Integer.parseInt(ctx.pathParam("id")), user);
        } catch (NumberFormatException e) {

        }
        if (event == null){
            ctx.status(401);
            ctx.attribute("user", user);
            ctx.attribute("events", commentService.getInvitedEvents(user));
            ctx.attribute("msg", "Du kan kun se events du er inviteret til.");
            ctx.render("teamc/invitedevents.html");
        }
        return event;
    }

    private void renderEvent(Context ctx, User user, UserEvent event, String msg) throws DatabaseException {
        ctx.attribute("user", user);
        ctx.attribute("event", event);
        ctx.attribute("comments", commentService.getComments(event));
        ctx.attribute("msg", msg);
        ctx.render("teamc/event.html");
    }

    private void addComment(@NotNull Context ctx) throws DatabaseException {
        User user = requireLogin(ctx);
        if (user == null) return;

        UserEvent event = findEvent(ctx, user);
        if (event == null) return;

        try {
            commentService.addComment(event.getEvent_id(), user, ctx.formParam("content"));
            ctx.redirect("/eventPlanner/event/" + event.getEvent_id());   // redirect så refresh ikke sender kommentaren igen
        } catch (IllegalArgumentException e) {
            renderEvent(ctx, user, event, e.getMessage());
        }

    }

    private void showEvent(@NotNull Context ctx) throws DatabaseException {
        User user = requireLogin(ctx);
        if (user == null) return;

        UserEvent event = findEvent(ctx, user);
        if (event == null) return;

        renderEvent(ctx, user, event, null);
    }

    private void InvitedEvents(@NotNull Context ctx) throws DatabaseException {
        User user = requireLogin(ctx);
        if(user == null) return;

        ctx.attribute("user", user);
        ctx.attribute("events", commentService.getInvitedEvents(user));
        ctx.render("teamc/invitedevents");
    }
}
