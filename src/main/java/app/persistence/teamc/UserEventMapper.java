package app.persistence.teamc;

import app.entities.teamc.Comment;
import app.entities.teamc.Invitation;
import app.entities.teamc.Response;
import app.entities.teamc.User;
import app.entities.teamc.UserEvent;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;
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

    public void inviteUser(int userId, int eventId) throws DatabaseException {
       String query = "INSERT INTO teamc_invitation (user_id, event_id, response_id)" +
               "VALUES (?, ?, (SELECT response_id FROM teamc_response WHERE response = 'NOT_RESPONDED'))";

       try (Connection conn = connectionPool.getConnection();
       PreparedStatement stmt = conn.prepareStatement(query)) {
           stmt.setInt(1, userId);
           stmt.setInt(2, eventId);
           stmt.executeUpdate();
       } catch (SQLException e) {
           logger.error(e.getMessage(), e);
           throw new DatabaseException("Invitattionen blev ikke gemt, " + e.getMessage());
       }
    }

    public UserEvent createEvent(User owner, String eventName, LocalDateTime startDate, LocalDateTime finishDate, boolean restrictComments) throws DatabaseException {
        String query = "INSERT INTO teamc_userevent (user_id, event_name, creation_date, start_date, finish_date, restrict_comments) " +
                "VALUES (?, ?, CURRENT_DATE, ?, ?, ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stm = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stm.setInt(1, owner.getUser_id());
            stm.setString(2, eventName);
            stm.setTimestamp(3, Timestamp.valueOf(startDate));
            stm.setTimestamp(4, Timestamp.valueOf(finishDate));
            stm.setBoolean(5, restrictComments);
            stm.executeUpdate();

            try (ResultSet keys = stm.getGeneratedKeys()) {
                if (keys.next()) {
                    int eventId = keys.getInt(1);
                    return new UserEvent(eventId, owner, eventName,
                            LocalDate.now().atStartOfDay(),
                            startDate,
                            finishDate,
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
                "FROM teamc_userevent WHERE user_id = ?  and start_date >= now() ORDER BY start_date, event_id";
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

    // Invitationer sendt TIL brugeren (brugeren er gæst)
    public List<Invitation> getInvitationsForUser(User guest) throws DatabaseException {
        String query = "SELECT i.invitation_id, r.response, " +
                "e.event_id, e.event_name, e.creation_date, e.start_date, e.finish_date, e.restrict_comments, " +
                "owner.user_id, owner.username, owner.email, owner.gender, owner.password " +
                "FROM teamc_invitation i " +
                "JOIN teamc_userevent e ON e.event_id = i.event_id " +
                "JOIN teamc_user owner ON owner.user_id = e.user_id " +
                "LEFT JOIN teamc_response r ON r.response_id = i.response_id " +
                "WHERE i.user_id = ? ORDER BY e.start_date desc";
        List<Invitation> invitations = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, guest.getUser_id());
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    User owner = new User(rs.getInt("user_id"), rs.getString("username"),
                            rs.getString("email"), rs.getString("gender"), rs.getString("password"));
                    UserEvent event = mapEvent(rs, owner);
                    Response response = toResponse(rs.getString("response"));
                    invitations.add(new Invitation(rs.getInt("invitation_id"), guest, event, response));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException("Kunne ikke hente dine invitationer", e.getMessage());
        }
        return invitations;
    }

    // Kommentarer skrevet på events som brugeren selv ejer
    public List<Comment> getCommentsForOwnersEvents(User owner) throws DatabaseException {
        String query = "SELECT c.comment_id, c.content, c.create_date, " +
                "e.event_id, e.event_name, e.start_date, e.finish_date, " +
                "commenter.user_id, commenter.username, commenter.email, commenter.gender, commenter.password " +
                "FROM teamc_comment c " +
                "JOIN teamc_userevent e ON e.event_id = c.event_id " +
                "JOIN teamc_user commenter ON commenter.user_id = c.user_id " +
                "WHERE e.user_id = ? ORDER BY c.create_date DESC, c.comment_id DESC";
        List<Comment> comments = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, owner.getUser_id());
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    User commenter = new User(rs.getInt("user_id"), rs.getString("username"),
                            rs.getString("email"), rs.getString("gender"), rs.getString("password"));
                    UserEvent event = new UserEvent(rs.getString("event_name"),
                            toDateTime(rs.getTimestamp("start_date")),
                            toDateTime(rs.getTimestamp("finish_date")));
                    event.setEvent_id(rs.getInt("event_id"));
                    comments.add(new Comment(rs.getInt("comment_id"), rs.getString("content"),
                            toDateTime(rs.getTimestamp("create_date")), commenter, event));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException("Kunne ikke hente kommentarer", e.getMessage());
        }
        return comments;
    }
    public List<Comment> getCommentsForEvent(int eventId) throws DatabaseException {
        String query = "SELECT c.comment_id, c.content, c.create_date, " +
                "e.event_id, e.event_name, e.creation_date, e.start_date, e.finish_date, e.restrict_comments, " +
                "owner.user_id AS owner_id, owner.username AS owner_username, owner.email AS owner_email, " +
                "owner.gender AS owner_gender, owner.password AS owner_password, " +
                "commenter.user_id, commenter.username, commenter.email, commenter.gender, commenter.password " +
                "FROM teamc_comment c " +
                "JOIN teamc_userevent e ON e.event_id = c.event_id " +
                "JOIN teamc_user owner ON owner.user_id = e.user_id " +
                "JOIN teamc_user commenter ON commenter.user_id = c.user_id " +
                "WHERE c.event_id = ? ORDER BY c.create_date, c.comment_id";


        List<Comment> comments = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, eventId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    User owner = new User(rs.getInt("owner_id"), rs.getString("owner_username"),
                            rs.getString("owner_email"), rs.getString("owner_gender"), rs.getString("owner_password"));
                    User commenter = new User(rs.getInt("user_id"), rs.getString("username"),
                            rs.getString("email"), rs.getString("gender"), rs.getString("password"));
                    UserEvent event = mapEvent(rs, owner);
                    comments.add(new Comment(rs.getInt("comment_id"), rs.getString("content"),
                            toDateTime(rs.getTimestamp("create_date")), commenter, event));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException("Kunne ikke hente kommentarer", e.getMessage());
        }
        return comments;
    }

    // True hvis brugeren er inviteret til eventet, uanset svar (også ikke besvaret)
    public boolean isInvited(int userId, int eventId) throws DatabaseException {
        String query = "SELECT 1 FROM teamc_invitation WHERE event_id = ? AND user_id = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, eventId);
            stm.setInt(2, userId);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException("Kunne ikke tjekke invitation", e.getMessage());
        }
    }

    public  boolean canComment(int userId, int eventId)throws DatabaseException {
        String query = "SELECT 1 " +
                "FROM teamc_userevent e " +
                "WHERE e.event_id = ? " +
                "AND ( e.user_id = ? " +
                "OR EXISTS ( " +
                "SELECT 1 FROM teamc_invitation i " +
                "LEFT JOIN teamc_response r ON r.response_id = i.response_id " +
                "WHERE i.event_id = e.event_id " +
                "AND i.user_id = ? " +
                "AND ( COALESCE(e.restrict_comments, false) = false " +
                "OR r.response IN ('YES', 'UNSURE', 'NO') ) ) )";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, eventId);
            stm.setInt(2, userId);
            stm.setInt(3, userId);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next();   // en række = må gerne kommentere
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException("Kunne ikke tjekke kommentar-rettigheder", e.getMessage());
        }
    }

    // Opdaterer kun hvis invitationen tilhører brugeren. Returnerer antal opdaterede rækker.
    public int respondToInvitation(int invitationId, int userId, Response response) throws DatabaseException {
        String query = "UPDATE teamc_invitation " +
                "SET response_id = (SELECT response_id FROM teamc_response WHERE response = ?) " +
                "WHERE invitation_id = ? AND user_id = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, toDbResponse(response));
            stm.setInt(2, invitationId);
            stm.setInt(3, userId);
            return stm.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException("Kunne ikke gemme dit svar", e.getMessage());
        }
    }

    private String toDbResponse(Response response) {
        return switch (response) {
            case YES -> "YES";
            case NO -> "NO";
            case DONT_KNOW -> "UNSURE";
            case NO_RESPONSE -> "NOT_RESPONDED";
        };
    }

    private UserEvent mapEvent(ResultSet rs, User owner) throws SQLException {
        return new UserEvent(
                rs.getInt("event_id"),
                owner,
                rs.getString("event_name"),
                toDateTime(rs.getTimestamp("creation_date")),
                toDateTime(rs.getTimestamp("start_date")),
                toDateTime(rs.getTimestamp("finish_date")),
                !rs.getBoolean("restrict_comments"));   // commentVisibility er det modsatte af restrict_comments
    }

    private LocalDateTime toDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
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


    public List<UserEvent> getAcceptedEvents(User user) throws DatabaseException {
        String query = "SELECT e.event_id, e.event_name, e.creation_date, e.start_date, e.finish_date, e.restrict_comments, " +
                "owner.user_id, owner.username, owner.email, owner.gender, owner.password " +
                "FROM teamc_invitation i " +
                "JOIN teamc_userevent e ON e.event_id = i.event_id " +
                "JOIN teamc_user owner ON owner.user_id = e.user_id " +
                "JOIN teamc_response r ON r.response_id = i.response_id " +
                "WHERE i.user_id = ? AND r.response = 'YES' AND e.start_date >= now() " +
                "ORDER BY e.start_date, e.event_id";
        List<UserEvent> events = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, user.getUser_id());
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    User owner = new User(rs.getInt("user_id"), rs.getString("username"),
                            rs.getString("email"), rs.getString("gender"), rs.getString("password"));
                    events.add(mapEvent(rs, owner));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException("Kunne ikke hente dine accepterede events", e.getMessage());
        }
        return events;
    }

    public List<UserEvent> getPastEvents(User user) throws DatabaseException {
        String query = "SELECT\n" +
                "    teamc_userevent.event_id,\n" +
                "    teamc_userevent.event_name,\n" +
                "    start_date,\n" +
                "    finish_date\n" +
                "FROM public.teamc_invitation\n" +
                "inner JOIN public.teamc_user  ON teamc_invitation.user_id = teamc_user.user_id\n" +
                "inner join public.teamc_userevent on  teamc_userevent.event_id = teamc_invitation.event_id\n" +
                "inner join public.teamc_response on teamc_invitation.response_id = teamc_response.response_id\n" +
                "where teamc_user.user_id = ? and start_date < now() and response = 'YES'\n" +
                "union all\n" +
                "select teamc_userevent.event_id,\n" +
                "       teamc_userevent.event_name,\n" +
                "       start_date,\n" +
                "       finish_date\n" +
                "from teamc_userevent\n" +
                "where teamc_userevent.user_id = ? and start_date < now()";
        List<UserEvent> events = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, user.getUser_id());
            stm.setInt(2, user.getUser_id());
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    String eventName = rs.getString("event_name");
                    LocalDateTime startDate = rs.getTimestamp("start_date").toLocalDateTime();
                    LocalDateTime finishDate = rs.getTimestamp("finish_date").toLocalDateTime();
                    events.add(new UserEvent(eventName, startDate, finishDate));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException("Kunne ikke hente dine events", e.getMessage());
        }
        return events;
    }


    public Comment createComment(User author, int eventId, String content) throws DatabaseException{

        String query = "INSERT INTO teamc_comment (user_id, event_id, content, create_date) " +
                "VALUES (?, ?, ?, CURRENT_DATE)";


        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stm = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stm.setInt(1, author.getUser_id());
            stm.setInt(2, eventId);
            stm.setString(3, content);
            stm.executeUpdate();

            try(ResultSet keys = stm.getGeneratedKeys()){
                if (keys.next()) {
                    Comment comment = new Comment(keys.getInt(1), content, LocalDate.now().atStartOfDay());
                    comment.setUser(author);
                    return comment;

                }

            }
            throw new DatabaseException("Kommentaren blev ikke gemt");

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException("Kommentaren blev ikke gemt", e.getMessage());
        }
    }

    public List<UserEvent> getCommentableEvents(User user) throws DatabaseException{
        String query = "SELECT e.event_id, e.event_name, e.creation_date, e.start_date, e.finish_date, e.restrict_comments, " +
                "owner.user_id, owner.username, owner.email, owner.gender, owner.password " +
                "FROM teamc_userevent e JOIN teamc_user owner ON owner.user_id = owner.user_id " +
                "WHERE e.user_id = ? OR e.event_id IN (SELECT event_id FROM teamc_invitation WHERE user_id = ?)";
        List<UserEvent> events = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, user.getUser_id());
            stm.setInt(2, user.getUser_id());
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    User owner = new User(rs.getInt("user_id"), rs.getString("username"),
                            rs.getString("email"), rs.getString("gender"), rs.getString("password"));
                    events.add(mapEvent(rs,owner));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException("Kunne ikke hente eventet", e.getMessage());
        }
        return events;
    }



}
