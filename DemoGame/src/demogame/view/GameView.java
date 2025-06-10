package demogame.view;

import java.awt.*;
import javax.swing.*;

public class GameView extends JFrame {
    private JLabel scoreLabel;
    private GamePanel gamePanel; // Keep a reference to the game panel

    public GameView() {
        setTitle("DemoGame - Play");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 1. Create a JLayeredPane to hold all components
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1200, 800));
        // Set the layered pane as the content pane of the frame
        setContentPane(layeredPane);

        // 2. Add your dynamic GamePanel to the BOTTOM layer
        gamePanel = new GamePanel(); // <--- Create your original GamePanel
        gamePanel.setBounds(0, 0, 1200, 800); // Make it fill the entire frame
        // Add to the default layer (the bottom)
        layeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);

        // 3. Add the score label to a HIGHER layer so it appears on top
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        // Use a color that is visible against your game's background
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(20, 10, 200, 30); // Position in top-left corner
        // Add to the palette layer (a high-level layer for floating components)
        layeredPane.add(scoreLabel, JLayeredPane.PALETTE_LAYER);
    }

    /**
     * Public method to update the score displayed on the screen.
     * @param score The new score to display.
     */
    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    /**
     * Optional: A method to get the GamePanel if other parts of your
     * program need to interact with it directly.
     * @return The GamePanel instance.
     */
    public GamePanel getGamePanel() {
        return gamePanel;
    }
}