package app.entities.team.c;

import java.util.UUID;

public class invitation {
    private UUID inviteList_id;
    private User user;
    private userEvent userEvent;
    private response response;



    public invitation(UUID inviteList_id, User user, userEvent userEvent, response response) {
        this.inviteList_id = inviteList_id;
        this.user = user;
        this.userEvent = userEvent;
        this.response = response;
    }

    public UUID getInviteList_id() {
        return inviteList_id;
    }

    public void setInviteList_id(UUID inviteList_id) {
        this.inviteList_id = inviteList_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public userEvent getUserEvent() {
        return userEvent;
    }

    public void setUserEvent(userEvent userEvent) {
        this.userEvent = userEvent;
    }

    public response getResponse() {
        return response;
    }

    public void setResponse(response response) {
        this.response = response;
    }
}
