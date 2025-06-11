package demogame.view;

import java.awt.*;
import javax.swing.*;

public class GameView extends JFrame {
    private JLabel scoreLabel;
    private GamePanel gamePanel;

    // ✅ ADD: Track score internally
    private int currentScore = 0;

    public GameView() {
        setTitle("DemoGame - Play");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1200, 800));
        setContentPane(layeredPane);

        gamePanel = new GamePanel();
        gamePanel.setBounds(0, 0, 1200, 800);
        layeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(20, 10, 200, 30);
        layeredPane.add(scoreLabel, JLayeredPane.PALETTE_LAYER);
    }

    /**
     * ✅ MODIFIED: Updates both the label and internal score.
     */
    public void updateScore(int score) {
        this.currentScore = score;
        scoreLabel.setText("Score: " + score);
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    /**
     * ✅ NEW: Get the final score at the end of the game
     */
    public int getFinalScore() {
        return currentScore;
    }
}
