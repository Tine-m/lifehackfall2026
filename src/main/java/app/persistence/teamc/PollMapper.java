package app.persistence.teamc;

import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;

public class PollMapper {

    ConnectionPool connectionPool;
    private static final Logger logger =
            LoggerFactory.getLogger(PollMapper.class);

    public PollMapper(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    public int createPoll(int eventId, String question, String resultsVisibility) throws DatabaseException {
        String query = "INSERT INTO teamc_poll (event_id, question, results_visibility) VALUES (?, ?, ?)";


        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                 stmt.setInt(1, eventId);
                 stmt.setString(2, question);
                 stmt.setString(3, resultsVisibility);
                 stmt.executeUpdate();

                 try (ResultSet keys = stmt.getGeneratedKeys()){
                     if (keys.next()) {
                         return keys.getInt(1);
                     }
                 }
                 throw new DatabaseException("Afstemningen blev ikke gemt");
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DatabaseException("Afstemningen blev ikke gemt", e.getMessage());
        }

    }


}
