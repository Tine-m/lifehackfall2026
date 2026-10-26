package app.dto.teamc;

import app.entities.teamc.CommentList;
import app.entities.teamc.Invitation;
import app.entities.teamc.User;

import java.time.LocalDateTime;
import java.util.List;

public class UserAndInvitation {


    private int event_id;
    private User user;
    private String eventName;
    private LocalDateTime creationDate;
    private LocalDateTime startDate;
    private LocalDateTime finishDate;
    private String response;
    private boolean commentVisibility;

    public UserAndInvitation(int event_id, User user, String eventName, LocalDateTime creationDate, LocalDateTime startDate, LocalDateTime finishDate, boolean commentVisibility,String response) {
        this.event_id = event_id;
        this.user = user;
        this.eventName = eventName;
        this.creationDate = creationDate;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.commentVisibility = commentVisibility;
        this.response = response;
    }


    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }

    public boolean isCommentVisibility() {
        return commentVisibility;
    }

    public void setCommentVisibility(boolean commentVisibility) {
        this.commentVisibility = commentVisibility;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
