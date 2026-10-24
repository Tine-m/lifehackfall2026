package app.services.teamc;

import app.entities.teamc.Invitation;
import app.entities.teamc.User;
import app.entities.teamc.UserEvent;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.teamc.UserEventMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class UserEventService {

    private ConnectionPool connectionPool;
    private UserEventMapper userEventMapper;

    public UserEventService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.userEventMapper = new UserEventMapper(connectionPool);
    }

    public UserEvent createEvent(User owner, String eventName, String startDate, String finishDate, boolean restrictComments) throws DatabaseException {
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

        return userEventMapper.createEvent(owner, eventName.trim(), start, finish, restrictComments);
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

    public List<Invitation> getParticipants(UserEvent event) throws DatabaseException {
        return userEventMapper.getInvitationsForEvent(event);
    }

    public List<UserEvent> getPastEvents(User user) throws DatabaseException {
        return userEventMapper.getPastEvents(user);
    }


}
