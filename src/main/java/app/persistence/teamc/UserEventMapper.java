package app.persistence.teamc;

import app.entities.teamc.Invitation;
import app.entities.teamc.Response;
import app.entities.teamc.User;
import app.entities.teamc.UserEvent;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserEventMapper {

    ConnectionPool connectionPool;
    private static final Logger logger =
            LoggerFactory.getLogger(UserEventMapper.class);

    public UserEventMapper(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    public UserEvent createEvent(User owner, String eventName, LocalDate startDate, LocalDate finishDate, boolean restrictComments) throws DatabaseException {
        String query = "INSERT INTO teamc_userevent (user_id, event_name, creation_date, start_date, finish_date, restrict_comments) " +
                "VALUES (?, ?, CURRENT_DATE, ?, ?, ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stm = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stm.setInt(1, owner.getUser_id());
            stm.setString(2, eventName);
            stm.setDate(3, Date.valueOf(startDate));
            stm.setDate(4, Date.valueOf(finishDate));
            stm.setBoolean(5, restrictComments);
            stm.executeUpdate();

            try (ResultSet keys = stm.getGeneratedKeys()) {
                if (keys.next()) {
                    int eventId = keys.getInt(1);
                    return new UserEvent(eventId, owner, eventName,
                            LocalDate.now().atStartOfDay(),
                            startDate.atStartOfDay(),
                            finishDate.atStartOfDay(),
                            !restrictComments);
                }
            }
            throw new DatabaseException("Eventet blev ikke gemt");
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException("Eventet blev ikke gemt", e.getMessage());
        }
    }

    // Alle events som brugeren selv har oprettet ("Mine events")
    public List<UserEvent> getEventsCreatedBy(User owner) throws DatabaseException {
        String query = "SELECT event_id, event_name, creation_date, start_date, finish_date, restrict_comments " +
                "FROM teamc_userevent WHERE user_id = ? ORDER BY start_date, event_id";
        List<UserEvent> events = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, owner.getUser_id());
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    events.add(mapEvent(rs, owner));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException("Kunne ikke hente dine events", e.getMessage());
        }
        return events;
    }

    public UserEvent getEventById(int eventId) throws DatabaseException {
        String query = "SELECT e.event_id, e.event_name, e.creation_date, e.start_date, e.finish_date, e.restrict_comments, " +
                "u.user_id, u.username, u.email, u.gender, u.password " +
                "FROM teamc_userevent e JOIN teamc_user u ON u.user_id = e.user_id " +
                "WHERE e.event_id = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, eventId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    User owner = new User(rs.getInt("user_id"), rs.getString("username"),
                            rs.getString("email"), rs.getString("gender"), rs.getString("password"));
                    return mapEvent(rs, owner);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException("Kunne ikke hente eventet", e.getMessage());
        }
        return null;
    }

    public List<Invitation> getInvitationsForEvent(UserEvent event) throws DatabaseException {
        String query = "SELECT i.invitation_id, r.response, " +
                "u.user_id, u.username, u.email, u.gender, u.password " +
                "FROM teamc_invitation i " +
                "JOIN teamc_user u ON u.user_id = i.user_id " +
                "LEFT JOIN teamc_response r ON r.response_id = i.response_id " +
                "WHERE i.event_id = ? ORDER BY u.username";
        List<Invitation> invitations = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, event.getEvent_id());
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    User guest = new User(rs.getInt("user_id"), rs.getString("username"),
                            rs.getString("email"), rs.getString("gender"), rs.getString("password"));
                    Response response = toResponse(rs.getString("response"));
                    invitations.add(new Invitation(rs.getInt("invitation_id"), guest, event, response));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException("Kunne ikke hente deltagere", e.getMessage());
        }
        return invitations;
    }

    private UserEvent mapEvent(ResultSet rs, User owner) throws SQLException {
        return new UserEvent(
                rs.getInt("event_id"),
                owner,
                rs.getString("event_name"),
                toDateTime(rs.getDate("creation_date")),
                toDateTime(rs.getDate("start_date")),
                toDateTime(rs.getDate("finish_date")),
                !rs.getBoolean("restrict_comments"));   // commentVisibility er det modsatte af restrict_comments
    }

    private LocalDateTime toDateTime(Date date) {
        return date == null ? null : date.toLocalDate().atStartOfDay();
    }

    private Response toResponse(String dbValue) {
        if (dbValue == null) {
            return Response.NO_RESPONSE;
        }
        return switch (dbValue) {
            case "YES" -> Response.YES;
            case "NO" -> Response.NO;
            case "UNSURE" -> Response.DONT_KNOW;
            default -> Response.NO_RESPONSE;
        };
    }

}
