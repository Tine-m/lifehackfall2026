package app.entities.teamc;

import java.util.List;

public class Poll {
    private int poll_id;
    private int event_id;
    private String question;
    private String results_visibility;
    private List<PollOption> pollOptionList;
    private boolean canVote;
    private boolean canSeeResults;

    public Poll(int poll_id, int event_id, String question, String results_visibility, List<PollOption> pollOptionList) {
        this.poll_id = poll_id;
        this.event_id = event_id;
        this.question = question;
        this.results_visibility = results_visibility;
        this.pollOptionList = pollOptionList;
    }

    public int getPoll_id() {
        return poll_id;
    }

    public int getEvent_id() {
        return event_id;
    }

    public String getQuestion() {
        return question;
    }

    public String getResults_visibility() {
        return results_visibility;
    }

    public List<PollOption> getPollOptionList() {
        return pollOptionList;
    }

    public void setPoll_id(int poll_id) {
        this.poll_id = poll_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setResults_visibility(String results_visibility) {
        this.results_visibility = results_visibility;
    }

    public void setPollOptionList(List<PollOption> pollOptionList) {
        this.pollOptionList = pollOptionList;
    }

    public boolean isCanVote() {
        return canVote;
    }

    public boolean isCanSeeResults() {
        return canSeeResults;
    }

    public void setCanVote(boolean canVote) {
        this.canVote = canVote;
    }

    public void setCanSeeResults(boolean canSeeResults) {
        this.canSeeResults = canSeeResults;
    }
}
