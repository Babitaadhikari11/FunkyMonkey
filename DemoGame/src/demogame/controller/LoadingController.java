package demogame.controller;

import demogame.dao.GameTipDao;
import demogame.model.GameTip;
import demogame.view.GameView;
import demogame.view.LoadingView;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.*;

public class LoadingController {
    private static final Logger LOGGER = Logger.getLogger(LoadingController.class.getName());
    private LoadingView view;
    private GameTipDao tipDao;
    private List<GameTip> tips;
    private Timer progressTimer;
    private Timer tipTimer;
    private int progress = 0;
    private int currentTipIndex = 0;
    private int userId;
// this controlls loading screen before game starts
    public LoadingController(LoadingView view, int userId) {
        this.view = view;
        this.userId = userId;
        this.tipDao = new GameTipDao();
        initializeLoading();
    }
// user pass  na huda run hune constrcutor
    public LoadingController(LoadingView view) {
        this(view, 0);
    }

    private void initializeLoading() {
        try {
            LOGGER.info("Initializing LoadingController for userId: " + userId);
            // Load tips from database
            tips = tipDao.getAllTips();
            if (tips.isEmpty()) {
                LOGGER.warning("No game tips loaded, displaying default tip");
                SwingUtilities.invokeLater(() -> view.updateTip("Welcome to Funky Monkey! Collect bananas to score points!"));
            }

            // Ensure view is visible
            SwingUtilities.invokeLater(() -> {
                view.setVisible(true);
                view.toFront(); // Bring to front
                LOGGER.info("LoadingView set visible for userId: " + userId);
            });

            // Progress timer (100ms interval, 5 seconds total)
            progressTimer = new Timer(100, e -> updateProgress());
            progressTimer.start();

            // Tip rotation timer (every 2 seconds)
            tipTimer = new Timer(2000, e -> rotateTips());
            tipTimer.start();
        } catch (Exception e) {
            handleError("Failed to initialize loading screen", e);
        }
    }
// progress bar 
    private void updateProgress() {
        try {
            progress += 2; // Reach 100 in ~5 seconds (50 * 100ms)
            SwingUtilities.invokeLater(() -> {
                view.updateProgress(progress);
                LOGGER.fine("Progress updated to: " + progress);
            });

            if (progress >= 100) {
                progressTimer.stop();
                tipTimer.stop();
                launchGame();
            }
        } catch (Exception e) {
            handleError("Error updating progress", e);
        }
    }
// due to limited game tips it keeps repeating the tips till the progress bar reach to 100
    private void rotateTips() {
        try {
            if (!tips.isEmpty()) {
                SwingUtilities.invokeLater(() -> {
                    String tipText = tips.get(currentTipIndex).getTipText();
                    view.updateTip(tipText);
                    LOGGER.fine("Displaying tip: " + tipText);
                    currentTipIndex = (currentTipIndex + 1) % tips.size();
                });
            }
        } catch (Exception e) {
            handleError("Error rotating tips", e);
        }
    }
// launch game once loading screen completes
    private void launchGame() {
        SwingUtilities.invokeLater(() -> {
            try {
                LOGGER.info("Launching game for userId: " + userId);
                cleanup();
                
                GameController gameController = new GameController(userId);
                GameView gameView = new GameView(gameController);
                gameController.startGame();
            } catch (Exception e) {
                handleError("Error launching game", e);
            }
        });
    }

    private void cleanup() {
        if (progressTimer != null && progressTimer.isRunning()) {
            progressTimer.stop();
        }
        if (tipTimer != null && tipTimer.isRunning()) {
            tipTimer.stop();
        }
        if (view != null) {
            SwingUtilities.invokeLater(() -> {
                view.dispose();
                LOGGER.info("LoadingView disposed for userId: " + userId);
            });
        }
    }

    private void handleError(String message, Exception e) {
        LOGGER.log(Level.SEVERE, message + " for userId: " + userId, e);
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

    public void forceGameLaunch() {
        cleanup();
        launchGame();
    }
}