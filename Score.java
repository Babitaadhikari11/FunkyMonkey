package demogame.model;


public class Score {
    private int currentScore;

    public Score() {
        this.currentScore = 0;
    }

    public int getScore() {
        return currentScore;
    }

    public void addPoints(int points) {
        this.currentScore += points;
    }

    public void reset() {
        this.currentScore = 0;
    }
}

