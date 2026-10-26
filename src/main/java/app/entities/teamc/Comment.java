package app.entities.teamc;

import java.time.LocalDateTime;
import java.util.UUID;

public class Comment {
    private int comment_id;
    private String comment_body;
    private LocalDateTime creationDate;
    private User user;
    private UserEvent userEvent;

    public Comment(int comment_id, String comment_body, LocalDateTime creationDate ) {
        this.comment_id = comment_id;
        this.comment_body = comment_body;
        this.creationDate = creationDate;
    }

    public Comment(int comment_id, String comment_body, LocalDateTime creationDate, User user, UserEvent userEvent) {
        this.comment_id = comment_id;
        this.comment_body = comment_body;
        this.creationDate = creationDate;
        this.user = user;
        this.userEvent = userEvent;
    }


    public UserEvent getUserEvent() {
        return userEvent;
    }

    public void setUserEvent(UserEvent userEvent) {
        this.userEvent = userEvent;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public String getComment_body() {
        return comment_body;
    }

    public void setComment_body(String comment_body) {
        this.comment_body = comment_body;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
