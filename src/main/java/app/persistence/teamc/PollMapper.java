package app.persistence.teamc;

import app.entities.teamc.Poll;
import app.entities.teamc.PollOption;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public void addOption(int pollId, String optionText) throws DatabaseException {
        String query = "INSERT INTO teamc_poll_option (poll_id, option_text) VALUES (?, ?)";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
                 stmt.setInt(1, pollId);
                 stmt.setString(2, optionText);
                 stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DatabaseException("Svarmuligheden blev ikke gemt,", e.getMessage());
        }
    }


    public void addVoter(int pollId, int userId) throws DatabaseException {
        String query = "INSERT INTO teamc_poll_voter (poll_id, user_id) VALUES (?, ?)";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, pollId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DatabaseException("Stemmer blev ikke gemt.", e.getMessage());
        }
    }

    public List<PollOption> getResults(int pollId) throws DatabaseException {
        String query = "SELECT o.option_id, o.option_text, COUNT(v.vote_id) AS votes " +
                "FROM teamc_poll_option o " +
                "LEFT JOIN teamc_vote v ON v.option_id = o.option_id " +
                "WHERE o.poll_id = ? " +
                "GROUP BY o.option_id, o.option_text " +
                "ORDER BY o.option_id";

        List<PollOption> pollOptions = new ArrayList<>();

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stm = conn.prepareStatement(query)) {
                 stm.setInt(1, pollId);
                 try (ResultSet rs = stm.executeQuery()) {
                     while(rs.next()) {
                         pollOptions.add(new PollOption(
                                 rs.getInt("option_id"),
                                 pollId,
                                 rs.getString("option_text"),
                                 rs.getInt("votes")));
                     }
                 }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DatabaseException("Resultater kunne ikke hentes", e.getMessage());
        }
        return pollOptions;
    }

    public void vote(int pollId, int optionId, int userId) throws DatabaseException {
        String query = "INSERT INTO teamc_vote (poll_id, option_id, user_id) VALUES (?, ?, ?)";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, pollId);
            stmt.setInt(2, optionId);
            stmt.setInt(3, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DatabaseException("Kunne ikke gemme stemmer", e.getMessage());
        }
    }

    public boolean canVote(int pollId, int userId) throws DatabaseException {
        String query = "SELECT 1 FROM teamc_poll_voter WHERE poll_id = ? AND user_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, pollId);
            stmt.setInt(2, userId);
           try(ResultSet rs = stmt.executeQuery()){
               return rs.next();
           }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DatabaseException("Kunne ikke tjekke om du må stemme", e.getMessage());
        }
    }

    public boolean hasVoted(int pollId, int userId) throws DatabaseException {
        String query = "SELECT 1 FROM teamc_vote WHERE poll_id = ? AND user_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, pollId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()){
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DatabaseException("Kunne ikke tjekke om du har stemt", e.getMessage());
        }
    }

    public Poll getPollById(int pollId) throws DatabaseException {
        String query = "SELECT poll_id, event_id, question, results_visibility FROM teamc_poll WHERE poll_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, pollId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Poll(
                            rs.getInt("poll_id"),
                            rs.getInt("event_id"),
                            rs.getString("question"),
                            rs.getString("results_visibility"),
                            getResults(pollId));      // svarmulighederne med antal stemmer
                }
                return null;                          // findes ikke
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DatabaseException("Afstemningen kunne ikke hentes", e.getMessage());
        }
    }

    public List<Poll> getPollsForEvent(int eventId) throws DatabaseException {
        String query = "SELECT poll_id, event_id, question, results_visibility FROM teamc_poll WHERE event_id = ? ORDER BY poll_id";
        List<Poll> polls = new ArrayList<>();

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int pollId = rs.getInt("poll_id");
                    polls.add(new Poll(
                            pollId,
                            rs.getInt("event_id"),
                            rs.getString("question"),
                            rs.getString("results_visibility"),
                            getResults(pollId)));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DatabaseException("Afstemningerne kunne ikke hentes", e.getMessage());
        }
        return polls;
    }



}
