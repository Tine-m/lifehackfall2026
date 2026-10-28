package app.entities.teama;

import java.time.LocalDate;

public class Highscore {
    // Score 1 er til niveau 1, Score 2 er til niveau 2 osv.
    private final int score1;
    private final int score2;
    private final int score3;
    private final String initials;
    private final LocalDate date;

    public Highscore(int score1, int score2, int score3, String initials) {
        this.score1 = score1;
        this.score2 = 0;
        this.score3 = 0;
        this.initials = initials;
        this.date = LocalDate.now();
    }

    public Highscore(int score1, String initials) {
        this.score1 = score1;
        this.score2 = 0;
        this.score3 = 0;
        this.initials = initials;
        this.date = LocalDate.now();
    }

    public Highscore(int score1, int score2, int score3, String initials, LocalDate date) {
        this.score1 = score1;
        this.score2 = 0;
        this.score3 = 0;
        this.initials = initials;
        this.date = date;
    }

    public int getScore1() {
        return score1;
    }

    public int getScore2() {
        return score2;
    }

    public int getScore3() {
        return score3;
    }

    public int getTotal() {
        return score1 + score2 + score3;
    }

    public String getInitials() {
        return initials;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getDateDay(){
        return date.getDayOfMonth();
    }

    public int getDateMonth(){
        return date.getMonthValue();
    }
}
