package demogame.view;

import javax.swing.*;

public class GameView extends JFrame {
    public GameView() {
        setTitle("DemoGame - Play");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // ⬅️ This is the critical line!
        add(new GamePanel());

        // Optional: setVisible(false); here if you want to delay showing
    }
}
