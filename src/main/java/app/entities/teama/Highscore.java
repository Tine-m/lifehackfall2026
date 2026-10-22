package app.entities.teama;

import java.time.LocalDate;
import java.util.Date;

public class Highscore {
    private final int score;
    private final String initials;
    private final LocalDate date;

    public Highscore(int score, String initials, LocalDate date) {
        this.score = score;
        this.initials = initials;
        this.date = date;
    }

    public int getScore() {
        return score;
    }

    public String getInitials() {
        return initials;
    }

    public LocalDate getDate() {
        return date;
    }
}
