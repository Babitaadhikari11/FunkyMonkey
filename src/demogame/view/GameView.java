package demogame.view;

import javax.swing.*;
import java.awt.*;

public class GameView extends JFrame {
    public GameView() {
        setTitle("DemoGame - Play");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JLabel label = new JLabel("Game Loaded! Ready to Play!", SwingConstants.CENTER);
        label.setFont(new Font("Comic Sans MS", Font.BOLD, 32));
        add(label, BorderLayout.CENTER);
    }
}
