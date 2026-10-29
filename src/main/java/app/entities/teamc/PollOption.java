package app.entities.teamc;

public class PollOption {
    private int option_id;
    private int poll_id;
    private String option_text;
    private int votes;

    public PollOption(int option_id, int poll_id, String option_text, int votes) {
        this.option_id = option_id;
        this.poll_id = poll_id;
        this.option_text = option_text;
        this.votes = votes;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public int getOption_id() {
        return option_id;
    }

    public int getPoll_id() {
        return poll_id;
    }

    public String getOption_text() {
        return option_text;
    }

    public void setOption_id(int option_id) {
        this.option_id = option_id;
    }

    public void setPoll_id(int poll_id) {
        this.poll_id = poll_id;
    }

    public void setOption_text(String option_text) {
        this.option_text = option_text;
    }
}
