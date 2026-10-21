package app.entities.team.c;

import java.util.UUID;

public class User {
    private UUID user_id;
    private String username;
    private String email;
    private String sex;
    private String password;

    public User(UUID user_id, String username, String email, String sex, String password) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.sex = sex;
        this.password = password;
    }






    public UUID getUser_id() {
        return user_id;
    }

    public void setUser_id(UUID user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
