package demogame.view;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class GameOverView extends JFrame {
    private JButton retryButton;
    private JButton menuButton;
    private JButton quitButton;
    private JLabel scoreLabel;

    public GameOverView(int finalScore) {
        setTitle("🐵 Game Over");
        setSize(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        JLabel titleLabel = new JLabel("Game Over!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(Color.RED);
        add(titleLabel, BorderLayout.NORTH);

        scoreLabel = new JLabel("Your Score: " + finalScore, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        add(scoreLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        retryButton = new JButton("Retry");
        menuButton = new JButton("Menu");
        quitButton = new JButton("Quit");

        buttonPanel.add(retryButton);
        buttonPanel.add(menuButton);
        buttonPanel.add(quitButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Button accessors for GameController
    public void setRetryAction(ActionListener retryAction) {
        retryButton.addActionListener(retryAction);
    }

    public void setMenuAction(ActionListener menuAction) {
        menuButton.addActionListener(menuAction);
    }

    public void setQuitAction(ActionListener quitAction) {
        quitButton.addActionListener(quitAction);
    }
}

