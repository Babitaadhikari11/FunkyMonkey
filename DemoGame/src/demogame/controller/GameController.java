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

    // Default constructor (used for testing/demo)
    public GameController() {
        initDatabase();
        showMenu();
    }

    // Overloaded constructor with GameView (not used, placeholder)
    public GameController(GameView gameView2) {
        this.gameView = gameView2;
        initDatabase();
    }

    // Overloaded constructor to receive logged-in user ID
    public GameController(int userId) {
        this.userId = userId;
        initDatabase();
        showMenu();
    }

   private void initDatabase() {
    try {
        Connection conn = DatabaseConnection.getConnection();
        this.scoreDao = new ScoreDao(conn);
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    private void showMenu() {
        MenuView menuView = new MenuView("Player");

        menuView.getStartButton().addActionListener(e -> {
            menuView.dispose();
            showLoadingScreen();
        });

        menuView.getQuitButton().addActionListener(e -> System.exit(0));

        menuView.setVisible(true);
    }

    private void showLoadingScreen() {
        LoadingView loadingView = new LoadingView();
        loadingView.setVisible(true);

        Timer loadingTimer = new Timer(100, null);
        loadingTimer.addActionListener(new AbstractAction() {
            int progress = 0;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                progress += 10;
                loadingView.updateProgress(progress);
                loadingView.updateTip("Tip: Use arrow keys to move 🕹️");

                if (progress >= 100) {
                    loadingTimer.stop();
                    loadingView.dispose();
                    launchGame();
                }
            }
        });
        loadingTimer.start();
    }

 private void launchGame() {
    gameView = new GameView();
    gameView.setVisible(true);
    
    // At the end of the game, call this:
    int finalScore = gameView.getFinalScore();
    saveScore(finalScore);
}


private GameOverListener gameOverListener;

    public void setGameOverListener(Object object) {
        if (object instanceof GameOverListener) {
            this.gameOverListener = (GameOverListener) object;
        } else {
            throw new IllegalArgumentException("Parameter must implement GameOverListener");
        }
    }

    // Method to trigger game over
    public void endGame() {
        if (gameOverListener != null) {
            gameOverListener.onGameOver();
        }
    }

    // Save score to DB
    public void saveScore(int score) {
    if (scoreDao != null) {
            boolean success = scoreDao.insertScore(userId, score);
            if (success) {
                System.out.println("✅ Score saved successfully!");
            } else {
                System.err.println("❌ Failed to save score.");
            }
        }
    }
}
