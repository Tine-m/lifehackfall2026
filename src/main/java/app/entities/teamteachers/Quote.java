package app.entities.teamteachers;

public class Quote {

    private String philosopher;
    private String quote;
    private String picture;

    public Quote(String philosopher, String quote, String picture) {
        this.philosopher = philosopher;
        this.quote = quote;
        this.picture = picture;
    }

    public String getPicture() {
        return picture;
    }

    public String getPhilosopher() {
        return philosopher;
    }

    public String getQuote() {
        return quote;
    }

}