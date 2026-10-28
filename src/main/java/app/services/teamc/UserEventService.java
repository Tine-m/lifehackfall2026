package app.services.teamc;

import app.entities.teamc.Comment;
import app.entities.teamc.Invitation;
import app.entities.teamc.Response;
import app.entities.teamc.User;
import app.entities.teamc.UserEvent;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.teamc.UserEventMapper;

import javax.xml.crypto.Data;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

public class UserEventService {

    private ConnectionPool connectionPool;
    private UserEventMapper userEventMapper;

    public UserEventService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.userEventMapper = new UserEventMapper(connectionPool);
    }

    public UserEvent createEvent(User owner, String eventName, String startDate, String finishDate, boolean restrictComments, List<String> invites) throws DatabaseException {
        if (eventName == null || eventName.isBlank()){
            throw new IllegalArgumentException("Eventet skal have et navn");
        }
        LocalDateTime start;
        LocalDateTime finish;
        try {
            start = LocalDateTime.parse(startDate);  // <input type="date"> sender yyyy-MM-dd
            finish = (finishDate == null || finishDate.isBlank() ? start : LocalDateTime.parse(finishDate));
        } catch (DateTimeParseException | NullPointerException e) {
            throw new IllegalArgumentException("Vælg en gyldige startdato!");
        }

        if (start.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Startdato kan ikke ligge i fortiden");
        }

        if (finish.isBefore(start)){
            throw new IllegalArgumentException("Slutdato skal være samme dag eller efter startdatoen");
        }

        UserEvent event = userEventMapper.createEvent(owner, eventName.trim(), start, finish, restrictComments);
        for(String id : invites){
            int userId = Integer.parseInt(id);
            if ((userId != owner.getUser_id())) {
                userEventMapper.inviteUser(userId, event.getEvent_id());
            }
        }
        return event;
    }

    public List<UserEvent> getMyEvents(User owner) throws DatabaseException {
        return userEventMapper.getEventsCreatedBy(owner);
    }

    public UserEvent getEventForOwner(int eventId, User user) throws DatabaseException {
        UserEvent event = userEventMapper.getEventById(eventId);
        if (event == null || event.getUser().getUser_id() != user.getUser_id()){
            return null;
        }
        return event;
    }

    // Ejeren eller en inviteret (uanset svar) må se deltagerne
    public UserEvent getEventForParticipant(int eventId, User user) throws DatabaseException {
        UserEvent event = userEventMapper.getEventById(eventId);
        if (event == null) {
            return null;
        }
        boolean isOwner = event.getUser().getUser_id() == user.getUser_id();
        if (isOwner || userEventMapper.isInvited(user.getUser_id(), eventId)) {
            return event;
        }
        return null;
    }

    public List<Invitation> getParticipants(UserEvent event) throws DatabaseException {
        return userEventMapper.getInvitationsForEvent(event);
    }

    public List<UserEvent> getPastEvents(User user) throws DatabaseException {
        return userEventMapper.getPastEvents(user);
    }

    public List<Invitation> getMyInvitations(User user) throws DatabaseException {
        return userEventMapper.getInvitationsForUser(user);
    }

    public List<Comment> getCommentsOnMyEvents(User owner) throws DatabaseException {
        return userEventMapper.getCommentsForOwnersEvents(owner);
    }

    public boolean respondToInvitation(User user, int invitationId, Response response) throws DatabaseException {
        return userEventMapper.respondToInvitation(invitationId, user.getUser_id(), response) > 0;
    }
    public List<UserEvent> getCommentableEvents(User user) throws DatabaseException {
        return userEventMapper.getCommentableEvents(user);
    }
    public boolean canComment(User user, int eventId) throws DatabaseException {
        return userEventMapper.canComment(user.getUser_id(), eventId);
    }
    public List<Comment> getComments(User user, int eventId) throws DatabaseException{
        boolean hasAccess = userEventMapper.canComment(user.getUser_id(), eventId);
        if (hasAccess) {
            return userEventMapper.getCommentsForEvent(eventId);
        } else return null;
    }

    public Comment addComment(User user, int eventId, String content) throws DatabaseException {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Kommentaren må ikke være tom");
            }
        if (content.length() > 500) {
            throw new IllegalArgumentException("Kommentaren må højst være 500 tegn");
        }
        if (!userEventMapper.canComment(user.getUser_id(), eventId)) {
            throw new IllegalArgumentException("Du skal svare på invitationen før du kan kommentere");
        }
        return userEventMapper.createComment(user, eventId, content.trim());
        }
    public List<UserEvent> getAcceptedEvents(User user) throws DatabaseException{
        return userEventMapper.getAcceptedEvents(user);
    }
    public String editEventName(int eventId, String newname, User user) throws DatabaseException {
        userEventMapper.editEventName(eventId,user.getUser_id(), newname);
            return newname;
    }
    public String editEventStart(int eventId, LocalDateTime newdate, User user) throws DatabaseException {
        userEventMapper.editEventStart(eventId,user.getUser_id(), newdate);
        return null;

    }
    public String editEventFinish(int eventId, LocalDateTime newdate, User user) throws DatabaseException {
        userEventMapper.editEventFinish(eventId, user.getUser_id(),newdate);
        return null;
    }

    public void inviteUser(int userId, int eventId)
            throws DatabaseException {
        userEventMapper.inviteUser(userId, eventId);
    }

    public boolean isInvited(int userId, int eventId)
            throws DatabaseException {
        return userEventMapper.isInvited(userId, eventId);
    }

    public int removeInvitation(int eventId, int userId)
            throws DatabaseException {
        return userEventMapper.removeInvitation(eventId, userId);
    }

}
