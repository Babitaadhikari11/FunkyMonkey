package demogame.view;

import demogame.controller.GameController;
import demogame.model.GameOverListener;
import java.awt.*;
import java.util.logging.Logger;
import javax.swing.*;

public class GameView extends JFrame implements GameOverListener {
    private static final Logger LOGGER = Logger.getLogger(GameView.class.getName());
    
    private JLabel scoreLabel;
    private GamePanel gamePanel;
    private GameController gameController;
    private int currentScore = 0;

    // Constructor
    public GameView(GameController controller) {
        this.gameController = controller;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("DemoGame - Play");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1200, 800));
        setContentPane(layeredPane);

        gamePanel = new GamePanel(this);
        gamePanel.setBounds(0, 0, 1200, 800);
        layeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(20, 10, 200, 30);
        layeredPane.add(scoreLabel, JLayeredPane.PALETTE_LAYER);

        // Set BananaController in GameController
        gameController.setBananaController(gamePanel.getBananaController());

        setupWindowListener();
    }

    private void setupWindowListener() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                LOGGER.info("Window closing, saving score: " + currentScore);
                handleGameExit();
            }
        });
    }

    @Override
    public void onGameOver() {
        int finalScore = getCurrentScore();
        LOGGER.info("Game over, saving final score: " + finalScore);
        gameController.saveScore(finalScore);
        
        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Game Over!\nFinal Score: " + finalScore + "\nWould you like to play again?",
                "Game Over",
                JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                restartGame();
            } else {
                handleGameExit();
            }
        });
    }

    public void showPauseMenu() {
        if (gamePanel != null) {
            Object[] options = {"Resume", "Restart", "Quit"};
            int choice = JOptionPane.showOptionDialog(
                this,
                "Game Paused",
                "Pause Menu",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );

            switch (choice) {
                case 0: // Resume
                    gamePanel.togglePause();
                    break;
                case 1: // Restart
                    restartGame();
                    break;
                case 2: // Quit
                    handleGameExit();
                    break;
            }
        }
    }

    public void updateScore(int score) {
        this.currentScore = score;
        LOGGER.info("Updating score display: " + score);
        SwingUtilities.invokeLater(() -> {
            scoreLabel.setText("Score: " + score);
        });
    }

    public void updateScoreDisplay(int score) {
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + score);
        }
    }

    public void restartGame() {
        currentScore = 0;
        updateScore(0);
        if (gamePanel != null) {
            gamePanel.restartGame();
        }
    }

    private void handleGameExit() {
        LOGGER.info("Exiting game, saving score: " + currentScore);
        gameController.saveScore(currentScore);
        dispose();
        gameController.showMenu();
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public int getFinalScore() {
        return currentScore;
    }

    public GameController getGameController() {
        return gameController;
    }

    public JLabel getScoreLabel() {
        return scoreLabel;
    }

    @Override
    public void dispose() {
        if (gamePanel != null) {
            gamePanel.cleanup();
        }
        super.dispose();
    }
}