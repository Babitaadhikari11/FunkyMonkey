package demogame.view;

import javax.swing.*;
import java.awt.*;
import demogame.controller.ScoreController;

public class GameScreen extends JFrame {
    private JLabel scoreLabel;
    private ScoreController scoreController;

    public GameScreen() {
        setTitle("Game Screen");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Score label
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(20, 20, 200, 30);
        add(scoreLabel);

        getContentPane().setBackground(Color.BLACK);

        // Score controller
        scoreController = new ScoreController(scoreLabel);

        // Simulate gameplay
        JButton addScoreButton = new JButton("Earn 10 Points");
        addScoreButton.setBounds(300, 250, 200, 50);
        add(addScoreButton);
        addScoreButton.addActionListener(e -> scoreController.increaseScore(10));

        setVisible(true);
    }

    public ScoreController getScoreController() {
        return scoreController;
    }
}
