package demogame.controller;

import demogame.dao.ScoreDao;
import javax.swing.*;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScoreController {

    private static final Logger LOGGER = Logger.getLogger(ScoreController.class.getName());
    
    private final ScoreDao scoreDao;
    private final int userId;
    private int score;
    private int lastSavedScore;
    private final JLabel scoreLabel;
    private Timer autoSaveTimer;
    // constants for auto-saving 
    private static final int AUTO_SAVE_INTERVAL = 30_000; // 30 seconds
    private static final int SIGNIFICANT_SCORE_CHANGE = 50; // save every 50 points


    public ScoreController(int userId, JLabel scoreLabel) {
        this.userId = userId;
        this.scoreLabel = scoreLabel;
        this.scoreDao = new ScoreDao();
        this.score = 0;
        this.lastSavedScore = 0;
        loadBestScore();
        startAutoSaveTimer();
    }

    // updates score from bananacontroller and refreshes ui
    public void updateScore(int newScore) {
        this.score = newScore;
        updateScoreLabel();
        checkSignificantScoreChange();
        LOGGER.info("Score updated to: " + score + " for userId: " + userId);
    }

    // give user  best score from database ignoring previous
    private void loadBestScore() {
        try {
            int bestScore = scoreDao.getUserBestScore(userId);
            LOGGER.info("Loaded best score: " + bestScore + " for userId: " + userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading best score", e);
        }
    }

    // updates score display 
    private void updateScoreLabel() {
        if (scoreLabel != null) {
            SwingUtilities.invokeLater(() -> scoreLabel.setText("Score: " + score));
        }
    }

    // checks if score change is large enough to save/optional
    private void checkSignificantScoreChange() {
        // save if score differs by 50 or more
        if (Math.abs(score - lastSavedScore) >= SIGNIFICANT_SCORE_CHANGE) {
            saveScore();
        }
    }

    // saves current score to database
    private void saveScore() {
        try {
            boolean saved = scoreDao.updateOrInsertScore(userId, score);
            if (saved) {
                lastSavedScore = score;
                LOGGER.info("Score saved: " + score + " for userId: " + userId);
            } else {
                LOGGER.warning("Failed to save score: " + score + " for userId: " + userId);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving score", e);
        }
    }

  
    private void startAutoSaveTimer() {
        autoSaveTimer = new Timer();
        autoSaveTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                saveScore();
            }
        }, AUTO_SAVE_INTERVAL, AUTO_SAVE_INTERVAL);
    }

    // stops timer and saves final score
    public void dispose() {
        if (autoSaveTimer != null) {
            autoSaveTimer.cancel();
        }
        saveScore();
    }

  
    public int getScore() {
        return score;
    }
}