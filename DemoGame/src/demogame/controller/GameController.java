package demogame.controller;

import demogame.dao.ScoreDao;
import demogame.model.GameOverListener;
import demogame.util.DatabaseConnection;
import demogame.view.GameView;
import demogame.view.LoadingView;
import demogame.view.MenuView;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.*;

public class GameController {
    private GameView gameView;
    private int userId;
    private ScoreDao scoreDao;
    private GameOverListener gameOverListener;
    private boolean isGameRunning;

    // Default constructor
    public GameController() {
        initDatabase();
        showMenu();
    }

    // Constructor with userId
    public GameController(int userId) {
        this.userId = userId;
        initDatabase();
        isGameRunning = false;
    }


    private void initDatabase() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            this.scoreDao = new ScoreDao(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Database connection failed!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }

    }

    public void showMenu() {
        SwingUtilities.invokeLater(() -> {
            MenuView menuView = new MenuView("Player");

            menuView.getStartButton().addActionListener(e -> {
                menuView.dispose();
                showLoadingScreen();
            });

            menuView.getQuitButton().addActionListener(e -> {
                cleanup();
                System.exit(0);
            });

            menuView.setVisible(true);
        });
    }

    private void showLoadingScreen() {
        LoadingView loadingView = new LoadingView();
        new LoadingController(loadingView, userId);
        loadingView.setVisible(true);
    }

    public void startGame() {
        if (!isGameRunning) {
            isGameRunning = true;
            gameView = new GameView(this);
            gameView.setVisible(true);
        }
    }

    public void pauseGame() {
        if (isGameRunning && gameView != null) {
            gameView.showPauseMenu();
        }
    }

    public void resumeGame() {
        if (isGameRunning && gameView != null) {
            gameView.getGamePanel().togglePause();
        }
    }

    public void restartGame() {
        if (gameView != null) {
            gameView.restartGame();
        }
    }

    public void endGame() {
        isGameRunning = false;
        if (gameOverListener != null) {
            gameOverListener.onGameOver();
        }
    }

    public void quitGame() {
        if (gameView != null) {
            int finalScore = gameView.getFinalScore();
            saveScore(finalScore);
            gameView.dispose();
        }
        isGameRunning = false;
        showMenu();
    }

    public void saveScore(int score) {
        if (scoreDao != null && userId > 0) {
            try {
                boolean success = scoreDao.insertScore(userId, score);
                if (success) {
                    System.out.println("✅ Score saved successfully!");
                } else {
                    System.err.println("❌ Failed to save score.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("❌ Error saving score: " + e.getMessage());
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
        return isGameRunning;
    }

    public int getUserId() {
        return userId;
    }

    private void cleanup() {
        if (gameView != null) {
            gameView.dispose();
        }
        try {
            if (DatabaseConnection.getConnection() != null) {
                DatabaseConnection.closeConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to handle window closing
    public void handleWindowClosing() {
        if (isGameRunning) {
            int choice = JOptionPane.showConfirmDialog(
                gameView,
                "Are you sure you want to quit?",
                "Quit Game",
                JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                quitGame();
            }
        }
    }
}