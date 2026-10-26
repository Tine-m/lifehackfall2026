package app.entities.teamc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UserEvent {

    private int event_id;
    private User user;
    private String eventName;
    private LocalDateTime creationDate;
    private LocalDateTime startDate;
    private LocalDateTime finishDate;
    private CommentList commentList;
    private List<Invitation> invitiation;
    private boolean commentVisibility;

    public UserEvent(int event_id, User user, String eventName, LocalDateTime creationDate, LocalDateTime startDate, LocalDateTime finishDate, boolean commentVisibility) {
        this.event_id = event_id;
        this.user = user;
        this.eventName = eventName;
        this.creationDate = creationDate;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.commentVisibility = commentVisibility;
    }

    public UserEvent(String eventName, LocalDateTime startDate, LocalDateTime finishDate) {
        this.eventName = eventName;
        this.startDate = startDate;
        this.finishDate = finishDate;
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

    public boolean isUpcoming() {
        return startDate != null &&
                !startDate.isBefore(LocalDateTime.now());
    }
}
