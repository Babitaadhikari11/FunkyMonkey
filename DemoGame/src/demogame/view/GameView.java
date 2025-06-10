package demogame.view;

import java.awt.*;
import javax.swing.*;

public class GameView extends JFrame {
    private JLabel scoreLabel;

    public GameView() {
        setTitle("DemoGame - Play");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create a layered pane
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1200, 800));
        setContentPane(layeredPane);

        // Add the center label (original content)
        JLabel label = new JLabel("Game Loaded! Ready to Play!", SwingConstants.CENTER);
        label.setFont(new Font("Comic Sans MS", Font.BOLD, 32));
        label.setBounds(300, 350, 600, 50); // center approximately
        layeredPane.add(label, Integer.valueOf(0));

        // Add the score label (top-left)
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        scoreLabel.setForeground(Color.BLACK);
        scoreLabel.setBounds(20, 20, 200, 30);
        layeredPane.add(scoreLabel, Integer.valueOf(1));
    }

    // Public method to update score
    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }
}
