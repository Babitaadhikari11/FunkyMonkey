
package demogame.controller;

import demogame.dao.ScoreDao;
import demogame.dao.UserDao; // added import for user data retrieval
import demogame.model.GameOverListener;
import demogame.model.UserData; // added import for user data
import demogame.util.DatabaseConnection;
import demogame.view.GameView;
import demogame.view.MenuView;
import javax.swing.*;
import java.awt.Font;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

// manages game flow
public class GameController {
    private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());
    
    private final int userId;
    private int currentScore;
    private final AtomicBoolean isGameRunning; // tracks game running status
    private GameView gameView;
    private MenuView menuView;
    private Timer scoreUpdateTimer;
    private ScoreDao scoreDao;
    private GameOverListener gameOverListener;
    private BananaController bananaController;

    public GameController(int userId) {
        this.userId = userId;
        this.currentScore = 0; // ensures score starts at 0
        this.isGameRunning = new AtomicBoolean(false);
        this.scoreDao = new ScoreDao();
    }

    // fetches user data for the current user
    public UserData getUserData() {
        try {
            UserDao userDao = new UserDao();
            UserData user = userDao.getUserById(userId);
            if (user != null) {
                LOGGER.info("Fetched UserData for userId: " + userId);
                return user;
            } else {
                LOGGER.warning("No UserData found for userId: " + userId);
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error fetching UserData for userId: " + userId, e);
            return null;
        }
    }

    // links banana controller and listens for score updates
    public void setBananaController(BananaController bananaController) {
        this.bananaController = bananaController;
        bananaController.addScoreListener(newScore -> {
            currentScore = newScore;
            LOGGER.info("GameController score updated: " + currentScore);
        });
    }

    // starts game
    public void startGame() {
        if (!isGameRunning.get()) {
            try {
                if (!DatabaseConnection.testConnection()) {
                    JOptionPane.showMessageDialog(null, 
                        "Cannot connect to database. Please check your connection.", 
                        "Database Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                isGameRunning.set(true);
                SwingUtilities.invokeLater(() -> {
                    showLoadingScreen();
                    initializeGameView();
                    startScoreUpdateTimer();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error starting game", e);
                JOptionPane.showMessageDialog(null, 
                    "An error occurred while starting the game.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // shows loading screen before starting game
    private void showLoadingScreen() {
        JFrame loadingFrame = new JFrame("Loading...🐒");
        loadingFrame.setSize(300, 100);
        loadingFrame.setLocationRelativeTo(null);
        loadingFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        JLabel loadingLabel = new JLabel("Loading game...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Arial", Font.BOLD, 16));
        loadingFrame.add(loadingLabel);
        
        loadingFrame.setVisible(true);
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Loading screen interrupted", e);
        }
        
        loadingFrame.dispose();
    }

    private void initializeGameView() {
        gameView = new GameView(this);
        gameView.setVisible(true);
        if (gameOverListener != null) {
            gameView.onGameOver();
        }
    }

    // updates score in database every 30 seconds
    private void startScoreUpdateTimer() {
        scoreUpdateTimer = new Timer();
        scoreUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                saveScore(currentScore);
            }
        }, 30_000, 30_000); // saves every 30 seconds
    }

    public void saveScore(int score) {
        try {
            boolean saved = scoreDao.updateOrInsertScore(userId, score);
            if (saved) {
                LOGGER.info("Score saved successfully: " + score + " for userId: " + userId);
            } else {
                LOGGER.warning("Failed to save score: " + score + " for userId: " + userId);
                JOptionPane.showMessageDialog(gameView, 
                    "Failed to save score to database.", 
                    "Database Error", 
                    JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving score for userId " + userId + ": " + score, e);
            JOptionPane.showMessageDialog(gameView, 
                "Error saving score: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // ends game and cleans up resources
    public void endGame() {
        if (isGameRunning.get()) {
            isGameRunning.set(false);
            saveScore(currentScore);
            cleanup();
            if (gameOverListener != null) {
                gameOverListener.onGameOver();
            }
        }
    }

    public void restartGame() {
        cleanup();
        currentScore = 0;
        startGame();
    }

    // quits game and returns to menu
    public void quitGame() {
        if (isGameRunning.get()) {
            saveScore(currentScore);
            cleanup();
            isGameRunning.set(false);
            if (gameView != null) {
                gameView.dispose();
            }
            showMenu();
        }
    }

    private void cleanup() {
        if (scoreUpdateTimer != null) {
            scoreUpdateTimer.cancel();
            scoreUpdateTimer = null;
        }
        if (gameView != null) {
            gameView.dispose();
            gameView = null;
        }
    }

    public void showMenu() {
        SwingUtilities.invokeLater(() -> {
            try {
                if (menuView == null) {
                    LOGGER.info("Creating new MenuView for userId: " + userId);
                    menuView = new MenuView(this);
                }
                menuView.setVisible(true);
                LOGGER.info("MenuView displayed for userId: " + userId);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error displaying MenuView for userId " + userId, e);
                JOptionPane.showMessageDialog(null,
                    "Error displaying menu: " + e.getMessage(),
                    "Menu Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }

    public int getUserId() {
        return userId;
    }

    public int getCurrentScore() {
        return currentScore;
    }
}