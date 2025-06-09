package demogame.view;

import java.awt.*;
import javax.swing.*;

public class GameScreen extends JFrame {
    private JLabel scoreLabel;
    private int score = 0;

    public GameScreen() {
        setTitle("Game Screen");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Set layout to null for absolute positioning
        setLayout(null);

        // Background (optional)
        JLabel background = new JLabel(new ImageIcon(getClass().getResource("/resources/Background.jpg")));
        background.setBounds(0, 0, 1200, 800);
        setContentPane(background);
        background.setLayout(null); // Allow absolute layout on content

        // Score label setup
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        scoreLabel.setForeground(Color.YELLOW);
        scoreLabel.setBounds(20, 20, 200, 30); // Top-left corner

        background.add(scoreLabel);
    }

    // Method to update score
    public void updateScore(int newScore) {
        this.score = newScore;
        scoreLabel.setText("Score: " + score);
    }

    // Optional: increment method
    public void incrementScore(int delta) {
        updateScore(score + delta);
    }
}
