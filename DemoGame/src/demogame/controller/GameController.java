package demogame.controller;

import demogame.dao.ScoreDao;
import demogame.dao.UserDao;
import demogame.dao.NotificationDao;
import demogame.model.GameOverListener;
import demogame.model.UserData;
import demogame.model.Notification;
import demogame.util.DatabaseConnection;
import demogame.view.GameView;
import demogame.view.MenuView;
import javax.swing.*;
import java.awt.Font;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

// Controller for managing game flow and notifications
public class GameController {
    private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());

    private final int userId;
    private int currentScore;
    private final AtomicBoolean isGameRunning;
    private GameView gameView;
    private MenuView menuView;
    private Timer scoreUpdateTimer;
    private ScoreDao scoreDao;
    private NotificationDao notificationDao;
    private GameOverListener gameOverListener;
    private BananaController bananaController;

    public GameController(int userId) {
        this.userId = userId;
        this.currentScore = 0;
        this.isGameRunning = new AtomicBoolean(false);
        try {
            this.scoreDao = new ScoreDao();
            this.notificationDao = new NotificationDao();
            LOGGER.info("ScoreDao and NotificationDao initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing DAOs", e);
        }
    }

    public String getRandomNotification() {
        Notification notification = notificationDao.getRandomNotification();
        if (notification == null) {
            LOGGER.warning("No notification returned from NotificationDao");
            return null;
        }
        LOGGER.info("Returning notification: " + notification.getMessage());
        return notification.getMessage();
    }

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
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Database error fetching UserData for userId: " + userId, e);
            return null;
        }
    }

    public void setBananaController(BananaController bananaController) {
        this.bananaController = bananaController;
        bananaController.addScoreListener(newScore -> {
            currentScore = newScore;
            LOGGER.info("GameController score updated: " + currentScore);
        });
    }

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
                    // showLoadingScreen();
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

    // private void showLoadingScreen() {
    //     JFrame loadingFrame = new JFrame("Loading...🐒");
    //     loadingFrame.setSize(300, 100);
    //     loadingFrame.setLocationRelativeTo(null);
    //     loadingFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    //     JLabel loadingLabel = new JLabel("Loading game...", SwingConstants.CENTER);
    //     loadingLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
    //     loadingFrame.add(loadingLabel);

    //     loadingFrame.setVisible(true);

    //     try {
    //         Thread.sleep(1500);
    //     } catch (InterruptedException e) {
    //         LOGGER.log(Level.WARNING, "Loading screen interrupted", e);
    //     }

    //     loadingFrame.dispose();
    // }

    private void initializeGameView() {
        try {
            gameView = new GameView(this);
            gameView.setVisible(true);
            LOGGER.info("GameView initialized for userId: " + userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing GameView", e);
            isGameRunning.set(false);
        }
    }

    private void startScoreUpdateTimer() {
        scoreUpdateTimer = new Timer();
        scoreUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (currentScore > 0) {
                    saveScore(currentScore);
                }
            }
        }, 10_000, 10_000);
        LOGGER.info("Score update timer started");
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

    public void endGame() {
        if (isGameRunning.get()) {
            isGameRunning.set(false);
            saveScore(currentScore);
            cleanup();
            if (gameOverListener != null) {
                gameOverListener.onGameOver();
            }
            LOGGER.info("Game ended for userId: " + userId);
        }
    }

    public void restartGame() {
        cleanup();
        currentScore = 0;
        startGame();
        LOGGER.info("Game restarted for userId: " + userId);
    }

    public void quitGame() {
        if (isGameRunning.get()) {
            saveScore(currentScore);
            cleanup();
            isGameRunning.set(false);
            showMenu();
            LOGGER.info("Game quit for userId: " + userId);
        }
    }

    private void cleanup() {
        LOGGER.info("Starting GameController cleanup for userId: " + userId);
        if (scoreUpdateTimer != null) {
            try {
                scoreUpdateTimer.cancel();
                LOGGER.info("Score update timer canceled");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error canceling scoreUpdateTimer", e);
            } finally {
                scoreUpdateTimer = null;
            }
        }
        if (gameView != null) {
            try {
                LOGGER.info("Attempting to clean up GameView");
                gameView.cleanup(); // Call GameView's cleanup method
                gameView.dispose();
                LOGGER.info("GameView cleaned up and disposed");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error cleaning up GameView: " + e.getMessage(), e);
            } finally {
                gameView = null;
                LOGGER.info("GameView set to null");
            }
        }
        LOGGER.info("GameController cleanup completed");
    }

    public void showMenu() {
        SwingUtilities.invokeLater(() -> {
            try {
                if (menuView != null) {
                    menuView.dispose();
                }
                menuView = new MenuView(this);
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
        LOGGER.info("GameOverListener set for userId: " + userId);
    }
    
    public int getUserId() {
        return userId;
    }

    public int getCurrentScore() {
        return currentScore;
    }
}