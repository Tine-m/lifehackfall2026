package app.entities.teamb;

public class Game {

    private int gameId;
    private String playerName;
    private boolean isLifehack;
    private int score;
    private int durationSeconds;

    public Game(int gameId, String playerName, boolean isLifehack, int score, int durationSeconds) {
        this.gameId = gameId;
        this.playerName = playerName;
        this.isLifehack = isLifehack;
        this.score = score;
        this.durationSeconds = durationSeconds;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isLifehack() {
        return isLifehack;
    }

    public void setLifehack(boolean lifehack) {
        isLifehack = lifehack;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

}
