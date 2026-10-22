package app.entities.teamc;

import java.util.UUID;

public class Invitation {
    private int inviteList_id;
    private User user;
    private UserEvent userEvent;
    private Response response;



    public Invitation(int inviteList_id, User user, UserEvent userEvent, Response response) {
        this.inviteList_id = inviteList_id;
        this.user = user;
        this.userEvent = userEvent;
        this.response = response;
    }

    public int getInviteList_id() {
        return inviteList_id;
    }

    public void setInviteList_id(int inviteList_id) {
        this.inviteList_id = inviteList_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserEvent getUserEvent() {
        return userEvent;
    }

    public void setUserEvent(UserEvent userEvent) {
        this.userEvent = userEvent;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
