package app.persistence.teamc;

import app.entities.teamc.Comment;
import app.entities.teamc.User;
import app.entities.teamc.UserEvent;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    ConnectionPool connectionPool;
    private static final Logger logger =
            LoggerFactory.getLogger(CommentMapper.class);

    public CommentMapper(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    public List<UserEvent> getEventsInvitedTo(User user) throws DatabaseException {
        String query = "SELECT e.event_id, e.event_name, e.creation_date, e.start_date, e.finish_date, e.restrict_comments, " +
                "u.user_id, u.username, u.email, u.gender, u.password " +
                "FROM teamc_invitation i " +
                "JOIN teamc_userevent e ON e.event_id = i.event_id " +
                "JOIN teamc_user u ON u.user_id = e.user_id " +      // u = den der har oprettet eventet
                "WHERE i.user_id = ? ORDER BY e.start_date, e.event_id";
        List<UserEvent> events = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, user.getUser_id());
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    User owner = new User(rs.getInt("user_id"), rs.getString("username"),
                            rs.getString("email"), rs.getString("gender"), rs.getString("password"));
                    events.add(new UserEvent(
                            rs.getInt("event_id"),
                            owner,
                            rs.getString("event_name"),
                            toDateTime(rs.getTimestamp("creation_date")),
                            toDateTime(rs.getTimestamp("start_date")),
                            toDateTime(rs.getTimestamp("finish_date")),
                            !rs.getBoolean("restrict_comments")));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException("Kunne ikke hente de events du er inviteret til", e.getMessage());
        }
        return events;
    }

    // Er brugeren inviteret til eventet?
    public boolean isInvited(int eventId, int userId) throws DatabaseException {
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
            throw new DatabaseException("Kunne ikke tjekke invitationen", e.getMessage());
        }
    }

    // ---------- KOMMENTARER ----------

    public void addComment(int eventId, User user, String content) throws DatabaseException {
        String query = "INSERT INTO teamc_comment (user_id, event_id, content, create_date) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, user.getUser_id());
            stm.setInt(2, eventId);
            stm.setString(3, content);
            stm.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException("Kommentaren blev ikke gemt", e.getMessage());
        }
    }

    // Alle kommentarer på et event, ældste først, med navnet på den der skrev dem
    public List<Comment> getCommentsForEvent(int eventId) throws DatabaseException {
        String query = "SELECT c.comment_id, c.content, c.create_date, " +
                "u.user_id, u.username, u.email, u.gender, u.password " +
                "FROM teamc_comment c JOIN teamc_user u ON u.user_id = c.user_id " +
                "WHERE c.event_id = ? ORDER BY c.create_date, c.comment_id";
        List<Comment> comments = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, eventId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Comment comment = new Comment(rs.getInt("comment_id"),
                            rs.getString("content"),
                            toDateTime(rs.getTimestamp("create_date")));
                    comment.setUser(new User(rs.getInt("user_id"), rs.getString("username"),
                            rs.getString("email"), rs.getString("gender"), rs.getString("password")));
                    comments.add(comment);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException("Kunne ikke hente kommentarer", e.getMessage());
        }
        return comments;
    }

    private LocalDateTime toDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
