package demogame.controller;

import demogame.view.GameView;
import demogame.view.LoadingView;
import demogame.view.MenuView;
import javax.swing.*;

public class GameController {
    private GameView gameView;

    public GameController() {
        showMenu();
    }

    public GameController(GameView gameView2) {
        //TODO Auto-generated constructor stub
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

        // Simulate loading with a timer
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

        // You can now add more game logic using this controller
        // e.g., managing input, updating scores, etc.
    }
}
