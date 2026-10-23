package app.entities.teamc;

import java.util.ArrayList;
import java.util.List;

public class CommentList {

    private List<Comment> userEvents;
    public CommentList() {
        userEvents = new ArrayList<>();
    }
    public void addComment(Comment comment){
        userEvents.add(comment);
    }
}
