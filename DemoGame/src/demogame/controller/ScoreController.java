package demogame.controller;

import demogame.model.Score;

public class ScoreController {
    private Score score;

    public ScoreController() {
        this.score = new Score();
    }

    public void increaseScore(int points) {
        score.addPoints(points);
        System.out.println("Score Updated: " + score.getScore());
    }

    public void resetScore() {
        score.reset();
        System.out.println("Score Reset.");
    }

    public int getScore() {
        return score.getScore();
    }
}
