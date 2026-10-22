package app.entities.teamc;

import java.time.LocalDateTime;
import java.util.UUID;

public class Comment {
    private UUID comment_id;
    private String comment_body;
    private LocalDateTime creationDate;
    private User user;
    private UserEvent userEvent;


    public Comment(UUID comment_id, String comment_body, LocalDateTime creationDate ) {
        this.comment_id = comment_id;
        this.comment_body = comment_body;
        this.creationDate = creationDate;
    }




    public UUID getComment_id() {
        return comment_id;
    }

    public void setComment_id(UUID comment_id) {
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
}
