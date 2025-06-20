package demogame.controller;

import demogame.dao.GameTipDao;
import demogame.model.GameTip;
import demogame.view.GameView;
import demogame.view.LoadingView;
import java.util.List;
import javax.swing.*;

public class LoadingController {
    private LoadingView view;
    private GameTipDao tipDao;
    private List<GameTip> tips;
    private Timer progressTimer;
    private Timer tipTimer;
    private int progress = 0;
    private int currentTipIndex = 0;
    private int userId; // Add userId field

    // Constructor with userId
    public LoadingController(LoadingView view, int userId) {
        this.view = view;
        this.userId = userId;
        this.tipDao = new GameTipDao();
        initializeLoading();
    }

    // Default constructor
    public LoadingController(LoadingView view) {
        this(view, 0); // Default userId of 0 for non-logged in users
    }

    private void initializeLoading() {
        try {
            // Load tips from database
            tips = tipDao.getAllTips();
            
            // Progress timer
            progressTimer = new Timer(50, e -> updateProgress());
            progressTimer.start();

            // Tip rotation timer
            tipTimer = new Timer(1000, e -> rotateTips());
            tipTimer.start();
        } catch (Exception e) {
            handleError("Failed to initialize loading screen", e);
        }
    }

    private void updateProgress() {
        try {
            progress += 1;
            view.updateProgress(progress);

            if (progress >= 100) {
                progressTimer.stop();
                tipTimer.stop();
                launchGame();
            }
        } catch (Exception e) {
            handleError("Error updating progress", e);
        }
    }

    private void rotateTips() {
        try {
            if (!tips.isEmpty()) {
                view.updateTip(tips.get(currentTipIndex).getTipText());
                currentTipIndex = (currentTipIndex + 1) % tips.size();
            }
        } catch (Exception e) {
            handleError("Error rotating tips", e);
        }
    }

    private void launchGame() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Clean up loading view
                cleanup();
                
                // Create game controller with userId
                GameController gameController = new GameController(userId);
                
                // Create game view with controller
                GameView gameView = new GameView(gameController);
                
                // Set up game controller listeners and show view
                gameController.setGameOverListener(gameView);
                gameView.setVisible(true);
                
            } catch (Exception e) {
                handleError("Error launching game", e);
            }
        });
    }

    private void cleanup() {
        // Stop timers
        if (progressTimer != null && progressTimer.isRunning()) {
            progressTimer.stop();
        }
        if (tipTimer != null && tipTimer.isRunning()) {
            tipTimer.stop();
        }

        // Dispose view
        if (view != null) {
            view.dispose();
        }
    }

    private void handleError(String message, Exception e) {
        e.printStackTrace();
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                view,
                message + "\nError: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            cleanup();
        });
    }

    // Getters and setters
    public int getProgress() {
        return progress;
    }

    public List<GameTip> getTips() {
        return tips;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Method to force game launch (for testing)
    public void forceGameLaunch() {
        cleanup();
        launchGame();
    }
}