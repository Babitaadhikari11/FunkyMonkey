package demogame.controller;

import demogame.dao.ScoreDao;
import demogame.model.GameOverListener;
import demogame.util.DatabaseConnection;
import demogame.view.GameView;
import demogame.view.LoadingView;
import demogame.view.MenuView;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class GameController {
    private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());
    
    private volatile GameView gameView;
    private final int userId;
    private ScoreDao scoreDao;
    private GameOverListener gameOverListener;
    private final AtomicBoolean isGameRunning;
    private static final String DB_ERROR_MESSAGE = "Database connection failed!";
    private static final String ERROR_TITLE = "Error";

    // Default constructor
    public GameController() {
        this.userId = -1; // Default user ID
        this.isGameRunning = new AtomicBoolean(false);
        initializeController();
    }

    // Constructor with userId
    public GameController(int userId) {
        this.userId = userId;
        this.isGameRunning = new AtomicBoolean(false);
        initializeController();
    }

    private void initializeController() {
        initDatabase();
        if (userId == -1) {
            showMenu();
        }
    }

    private void initDatabase() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                this.scoreDao = new ScoreDao(conn);
                LOGGER.info("Database connection established successfully");
            } else {
                throw new SQLException("Failed to establish database connection");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database initialization failed", e);
            SwingUtilities.invokeLater(() -> 
                JOptionPane.showMessageDialog(null, 
                    DB_ERROR_MESSAGE, 
                    ERROR_TITLE, 
                    JOptionPane.ERROR_MESSAGE)
            );
        }
    }

    public void showMenu() {
        SwingUtilities.invokeLater(() -> {
            try {
                MenuView menuView = new MenuView("Player");
                setupMenuListeners(menuView);
                menuView.setVisible(true);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to show menu", e);
                showErrorDialog("Failed to load menu");
            }
        });
    }

    private void setupMenuListeners(MenuView menuView) {
        menuView.getStartButton().addActionListener(e -> {
            menuView.dispose();
            showLoadingScreen();
        });

        menuView.getQuitButton().addActionListener(e -> {
            cleanup();
            System.exit(0);
        });
    }

    private void showLoadingScreen() {
        SwingUtilities.invokeLater(() -> {
            try {
                LoadingView loadingView = new LoadingView();
                new LoadingController(loadingView, userId);
                loadingView.setVisible(true);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to show loading screen", e);
                showErrorDialog("Failed to load game");
            }
        });
    }

    public void startGame() {
        if (!isGameRunning.get()) {
            SwingUtilities.invokeLater(() -> {
                try {
                    isGameRunning.set(true);
                    gameView = new GameView(this);
                    gameView.setVisible(true);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to start game", e);
                    isGameRunning.set(false);
                    showErrorDialog("Failed to start game");
                }
            });
        }
    }

    public void pauseGame() {
        if (isGameRunning.get() && gameView != null) {
            SwingUtilities.invokeLater(() -> gameView.showPauseMenu());
        }
    }

    public void resumeGame() {
        if (isGameRunning.get() && gameView != null) {
            SwingUtilities.invokeLater(() -> gameView.getGamePanel().togglePause());
        }
    }

    public void restartGame() {
        if (gameView != null) {
            SwingUtilities.invokeLater(() -> gameView.restartGame());
        }
    }

    public void endGame() {
        isGameRunning.set(false);
        if (gameOverListener != null) {
            SwingUtilities.invokeLater(() -> gameOverListener.onGameOver());
        }
    }

    public void quitGame() {
        if (gameView != null) {
            final int finalScore = gameView.getFinalScore();
            saveScore(finalScore);
            SwingUtilities.invokeLater(() -> {
                gameView.dispose();
                isGameRunning.set(false);
                showMenu();
            });
        }
    }

    public void saveScore(int score) {
        if (scoreDao != null && userId > 0) {
            try {
                boolean success = scoreDao.insertScore(userId, score);
                if (success) {
                    LOGGER.info("Score saved successfully!");
                } else {
                    LOGGER.warning("Failed to save score");
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error saving score", e);
                showErrorDialog("Failed to save score");
            }
        }
    }

    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }

    public int getCurrentScore() {
        return gameView != null ? gameView.getCurrentScore() : 0;
    }

    public boolean isGameRunning() {
        return isGameRunning.get();
    }

    public int getUserId() {
        return userId;
    }

    private void cleanup() {
        try {
            if (gameView != null) {
                gameView.dispose();
            }
            if (DatabaseConnection.getConnection() != null) {
                DatabaseConnection.closeConnection();
                LOGGER.info("Database connection closed successfully");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during cleanup", e);
        }
    }

    public void handleWindowClosing() {
        if (isGameRunning.get()) {
            SwingUtilities.invokeLater(() -> {
                int choice = JOptionPane.showConfirmDialog(
                    gameView,
                    "Are you sure you want to quit?",
                    "Quit Game",
                    JOptionPane.YES_NO_OPTION
                );
                if (choice == JOptionPane.YES_OPTION) {
                    quitGame();
                }
            });
        }
    }

    private void showErrorDialog(String message) {
        SwingUtilities.invokeLater(() -> 
            JOptionPane.showMessageDialog(null, 
                message, 
                ERROR_TITLE, 
                JOptionPane.ERROR_MESSAGE)
        );
    }
}