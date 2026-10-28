package app.controllers.teamc;

import app.entities.teamc.Poll;
import app.entities.teamc.User;
import app.entities.teamc.UserEvent;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.services.teamc.PollService;
import app.services.teamc.UserEventService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PollController {
    private PollService pollService;
    private UserEventService userEventService;

    public PollController(ConnectionPool connectionPool) {
        this.pollService = new PollService(connectionPool);
        this.userEventService = new UserEventService(connectionPool);
    }

    public void setRoutes(JavalinConfig config) {
        config.routes.get("/eventPlanner/event/{id}/polls", ctx -> showPolls(ctx));
        config.routes.get("/eventPlanner/event/{id}/poll/new", ctx -> showCreatePoll(ctx));
        config.routes.post("/eventPlanner/event/{id}/poll/new", ctx -> createPoll(ctx));
        config.routes.post("/eventPlanner/poll/{pollId}/vote", ctx -> vote(ctx));

    }

    public void vote(@NotNull Context ctx) throws DatabaseException {
        User user = requireLogin(ctx);
        if (user == null) return;

        int pollId = Integer.parseInt(ctx.pathParam("pollId"));
        int optionId = Integer.parseInt(ctx.formParam("optionId"));
        Poll poll = pollService.getPoll(pollId);

        try {
            pollService.vote(user, pollId, optionId);
            ctx.sessionAttribute("msg", "Tak for din stemme");
        } catch (IllegalArgumentException e){
            ctx.sessionAttribute("msg", e.getMessage());
        }
        ctx.redirect("/eventPlanner/event/" + poll.getEvent_id() + "/polls");

    }

    public void showCreatePoll(@NotNull Context ctx) throws DatabaseException {
        User user = requireLogin(ctx);
        if (user == null) return;
        int eventId = Integer.parseInt(ctx.pathParam("id"));
        UserEvent event = userEventService.getEventForOwner(eventId, user);

        if (event == null) {
            ctx.status(403);
            ctx.result("Kun ejeren af eventet kan lave en afstemning.");
            return;
        }

        ctx.attribute("event", event);
        ctx.attribute("invitations", userEventService.getParticipants(event));
        ctx.render("teamc/createpoll");
    }

    // 3. Gem den nye afstemning
    public void createPoll(Context ctx) throws DatabaseException {
        User user = ctx.sessionAttribute("user");
        if (user == null) {
            ctx.redirect("/eventPlanner");
            return;
        }

        int eventId = Integer.parseInt(ctx.pathParam("id"));
        String question = ctx.formParam("question");
        List<String> options = ctx.formParams("options");     // alle svar-felterne
        List<String> voters = ctx.formParams("voters");
        String visibility = ctx.formParam("visibility");

        try {
            pollService.createPoll(user, eventId, question, options, voters, visibility);
            ctx.sessionAttribute("msg", "Afstemningen er oprettet.");
        } catch (IllegalArgumentException e) {
            ctx.sessionAttribute("msg", e.getMessage());      // fx "Der skal være mindst 2 svarmuligheder"
        }
        ctx.redirect("/eventPlanner/event/" + eventId + "/polls");
    }

    public void showPolls(@NotNull Context ctx) throws DatabaseException {
        User user = requireLogin(ctx);
        if(user == null) return;

        int eventId = Integer.parseInt(ctx.pathParam("id"));

        if (!pollService.canSeeEvent(user, eventId)) {
            ctx.status(401);
            ctx.result("Du har ikke adgnag til dette event.");
            return;
        }

        UserEvent event = pollService.getEvent(eventId);
        List<Poll> polls = pollService.getPollsForEvent(eventId);
        for(Poll poll : polls){
            poll.setCanVote(pollService.canVoteNow(user, poll));
            poll.setCanSeeResults(pollService.canSeeResults(user, poll));
        }

        boolean isOwner = event.getUser().getUser_id() == user.getUser_id();

        ctx.attribute("event", event);
        ctx.attribute("polls", polls);
        ctx.attribute("isOwner", isOwner);
        ctx.attribute("msg", ctx.consumeSessionAttribute("msg"));
        ctx.render("teamc/polls");
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

    // Læser {id} fra URL'en. Giver -1 hvis det ikke er et tal
    private int readId(Context ctx, String name) {
        try {
            return Integer.parseInt(ctx.pathParam(name));
        } catch (NumberFormatException e) {
            return -1;
        }
    }




}
