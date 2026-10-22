package app.persistence.teamc;

import app.entities.teamc.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventMapper {


    ConnectionPool connectionPool;
    private static final Logger logger =
            LoggerFactory.getLogger(EventMapper.class);

    public EventMapper(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    public User login (String mail, String password) throws DatabaseException {

        User user = getUserByUserName(mail);
        if(user != null && user.getPassword().equals(password)){
            return user;
        }
        else return null;
    }


    public User createUser(User user) throws DatabaseException {

        int userLength = getUsers().size();

        String query = "INSERT INTO teamc_user (user_id, username,  email , gender, password) VALUES (?,?, ?, ?, ?)";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stm = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stm.setInt(1, userLength + 1);
            stm.setString(2, user.getUsername());
            stm.setString(3, user.getEmail());
            stm.setString(4, user.getSex());
            stm.setString(5, user.getPassword());

            stm.executeUpdate();


        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            if ("23505".equals(e.getSQLState())) {
                throw new DatabaseException("Brugernavn eller e-mail er allerede i brug");
            }
            throw new DatabaseException("Brugeren blev ikke gemt");
        }
        return user;
    }

    public User getUserByUserName(String inputemail) throws DatabaseException {
        User user = null;
        String query = "SELECT user_id, username,  email , gender, password FROM teamc_user WHERE email = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, inputemail);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("user_id");
                    String username = rs.getString("username");
                    String email = rs.getString("email");
                    String gender = rs.getString("gender");
                    String password = rs.getString("password");

                    user = new User(id, username, email, gender,password);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException("Søgning efter brugeren fejlede");
        }
        return user;
    }

    public List<User> getUsers() throws DatabaseException {
        String query = "SELECT user_id, username,  email , gender, password FROM teamc_user";
        List<User> users = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement stm = connection.prepareStatement(query)) {
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("user_id");
                    String username = rs.getString("username");
                    String email = rs.getString("email");
                    String gender = rs.getString("gender");
                    String password = rs.getString("password");
                    users.add(new User(id, username, email, gender,password));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DatabaseException("Søgning efter brugeren fejlede");
        }
        return users;
    }

}
