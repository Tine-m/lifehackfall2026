package app.services.teamc;

import app.dto.teamc.UserAndInvitation;
import app.entities.teamc.Comment;
import app.entities.teamc.User;
import app.entities.teamc.UserEvent;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.teamc.CommentMapper;
import app.persistence.teamc.UserEventMapper;

import java.util.List;

public class CommentService {

    private static final int MAX_LENGTH = 500;

    private ConnectionPool connectionPool;
    private CommentMapper commentMapper;
    private UserEventMapper userEventMapper;

    public CommentService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.commentMapper = new CommentMapper(connectionPool);
        this.userEventMapper = new UserEventMapper(connectionPool);
    }

    public List<UserAndInvitation> getInvitedEvents(User user) throws DatabaseException {
        return commentMapper.getEventsInvitedTo(user);
    }

    public UserEvent getEventIfAllowed(int eventId, User user) throws DatabaseException {
        UserEvent event = userEventMapper.getEventById(eventId);
        if (event == null) {
            return null;
        }
        boolean isOwner = event.getUser().getUser_id() == user.getUser_id();
        if (isOwner || commentMapper.isInvited(eventId, user.getUser_id())) {
            return event;
        }
        return null;

    }

    public List<Comment> getComments(UserEvent event) throws DatabaseException {
        return commentMapper.getCommentsForEvent(event.getEvent_id());
    }

    // Kaster IllegalArgumentException med en besked brugeren kan se, hvis noget er galt.
    public void addComment(int eventId, User user, String content) throws DatabaseException {
        if (getEventIfAllowed(eventId, user) == null) {
            throw new IllegalArgumentException("Du kan kun kommentere på events du er inviteret til.");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Kommentaren må ikke være tom.");
        }
        if (content.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Kommentaren må højst være " + MAX_LENGTH + " tegn.");
        }
        commentMapper.addComment(eventId, user, content.trim());
    }


}
