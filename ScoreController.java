package demogame.controller;

import demogame.model.Score;
import javax.swing.JLabel;

public class ScoreController {
    private Score score;
    private JLabel scoreLabel;

    public ScoreController(JLabel scoreLabel) {
        this.score = new Score();
        this.scoreLabel = scoreLabel;
        updateLabel();
    }

    public void increaseScore(int points) {
        score.addPoints(points);
        updateLabel();
    }

    public void resetScore() {
        score.reset();
        updateLabel();
    }

    public int getScore() {
        return score.getScore();
    }

    private void updateLabel() {
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + score.getScore());
        }
    }
}
