package app.entities.team.c;

import javax.xml.stream.events.Comment;
import java.util.ArrayList;
import java.util.List;

public class commentList {
    private List<comment> userEvents;

    public commentList() {
        userEvents = new ArrayList<>();

    }
    public void addComment(comment comment){
            userEvents.add(comment);


    }
}
