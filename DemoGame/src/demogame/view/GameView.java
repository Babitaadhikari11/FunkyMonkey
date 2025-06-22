package demogame.view;

import demogame.controller.GameController;
import demogame.model.GameOverListener;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;


// View for the game window, hosting GamePanel and displaying score
public class GameView extends JFrame implements GameOverListener {
    private static final Logger LOGGER = Logger.getLogger(GameView.class.getName());

    private final GameController gameController;
    private GamePanel gamePanel;
    private JLabel scoreLabel;
    private int currentScore;

    public GameView(GameController controller) {
        this.gameController = controller;
        initializeComponents();
        setTitle("Monkey Game");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                gameController.quitGame();
            }
        });
        LOGGER.info("GameView initialized for userId: " + controller.getUserId());
    }

    private void initializeComponents() {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        try {
            gamePanel = new GamePanel(this);
            gamePanel.setBounds(0, 0, 1200, 800);
            layeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing GamePanel", e);
            JOptionPane.showMessageDialog(this, "Failed to initialize game.", "Error", JOptionPane.ERROR_MESSAGE);
            gameController.quitGame();
            return;
        }

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(10, 10, 200, 30);
        layeredPane.add(scoreLabel, JLayeredPane.PALETTE_LAYER);

        setContentPane(layeredPane);

        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    showPauseMenu();
                }
            }
        });
    }

    public void updateScore(int score) {
        this.currentScore = score;
        SwingUtilities.invokeLater(() -> {
            scoreLabel.setText("Score: " + score);
            LOGGER.info("Score updated: " + score);
        });
    }

    public void showPauseMenu() {
        if (gamePanel != null) {
            gamePanel.togglePause();
        }
        String[] options = {"Resume", "Restart", "Quit"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Game Paused",
            "Pause Menu",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );

        switch (choice) {
            case 0: // Resume
                if (gamePanel != null) {
                    gamePanel.togglePause();
                }
                break;
            case 1: // Restart
                gameController.restartGame();
                break;
            case 2: // Quit
                gameController.quitGame();
                break;
        }
    }

    @Override
    public void onGameOver() {
        gameController.saveScore(currentScore);
        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Game Over! Score: " + currentScore + "\nTry again?",
                "Game Over",
                JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                gameController.restartGame();
            } else {
                gameController.quitGame();
            }
        });
    }

    public GameController getGameController() {
        return gameController;
    }

    public JLabel getScoreLabel() {
        return scoreLabel;
    }

    public void cleanup() {
        LOGGER.info("Starting GameView cleanup for userId: " + gameController.getUserId());
        if (gamePanel != null) {
            try {
                LOGGER.info("Calling GamePanel cleanup");
                gamePanel.cleanup();
                LOGGER.info("GamePanel cleanup completed");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error cleaning up GamePanel: " + e.getMessage(), e);
            } finally {
                gamePanel = null;
                LOGGER.info("GamePanel set to null");
            }
        }
        try {
            removeAll();
            setContentPane(new JPanel());
            LOGGER.info("GameView components cleared");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error clearing GameView components: " + e.getMessage(), e);
        }
        LOGGER.info("GameView cleanup completed");
    }
}