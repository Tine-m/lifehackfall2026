package app.services.teamc;

import app.entities.teamc.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.teamc.EventMapper;

import java.util.List;

public class EventService {

    private ConnectionPool connectionPool;
    private EventMapper eventMapper;

    public EventService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.eventMapper = new EventMapper(connectionPool);
    }

    public List<User> getUsers() throws DatabaseException {
        return eventMapper.getUsers();
    }

    public User getUser(String mail) throws DatabaseException {
        return eventMapper.getUserByUserName(mail);
    }


    public User login(String username, String password) throws DatabaseException {
        return eventMapper.login(username, password);
    }

    public User createUser(String username, String password,String email) throws DatabaseException {
        if (username == null || username.isEmpty()) {
            return null;
        }

       /* if (password == null || !validatePassword(password)) {
            return null;
        }*/

        if (email == null || email.isEmpty()) {
            return null;
        }

        if (getUser(email) != null) {
            return null; // mail er allerede i brug
        }

        User newUser = new User(username, password, email);
        return eventMapper.createUser(newUser);
    }

    public static boolean validatePassword(String password){
        if(password.length() < 8 || password.length() > 15){
            return false;
        }

        // Mindst et bogstav skal være med stort, et tal og 8-15 tegn i alt.
        String regex = "^(?=.*[A-Z])(?=.*[0-9]).{8,15}$";
        return password.matches(regex);
    }

}
