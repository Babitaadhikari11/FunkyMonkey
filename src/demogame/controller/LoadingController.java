package demogame.controller;
import demogame.dao.GameTipDao;
import demogame.model.GameTip;
import demogame.view.LoadingView;
import demogame.view.GameView; //  actual game view
import javax.swing.*;
import java.util.List;
public class LoadingController {
     private LoadingView view;
    private GameTipDao tipDao;
    private List<GameTip> tips;
    private Timer progressTimer;
    private Timer tipTimer;
    private int progress = 0;
    private int currentTipIndex = 0;

    public LoadingController(LoadingView view) {
        this.view = view;
        this.tipDao = new GameTipDao();
        initializeLoading();
    }

    private void initializeLoading() {
        // Load tips from database
        tips = tipDao.getAllTips();
        
        // Progress timer
        progressTimer = new Timer(50, e -> updateProgress());
        progressTimer.start();

        // Tip rotation timer
        tipTimer = new Timer(1000, e -> rotateTips());
        tipTimer.start();
    }

    private void updateProgress() {
        progress += 1;
        view.updateProgress(progress);

        if (progress >= 100) {
            progressTimer.stop();
            tipTimer.stop();
            launchGame();
        }
    }

    private void rotateTips() {
        if (!tips.isEmpty()) {
            view.updateTip(tips.get(currentTipIndex).getTipText());
            currentTipIndex = (currentTipIndex + 1) % tips.size();
        }
    }

    private void launchGame() {
        SwingUtilities.invokeLater(() -> {
            view.dispose();
            // Launch your actual game view here
            GameView gameView = new GameView();
            new GameController(gameView);
            gameView.setVisible(true);
        });
    }
}
    

