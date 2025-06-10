package demogame.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;


public class GamePanel extends JPanel implements ActionListener {

    private Image jungle1;
    private Image jungle2;
    private int x1 = 0;
    private int x2;
    private Timer timer;
    private final int SPEED = 2;

    public GamePanel() {
        setPreferredSize(new Dimension(1200, 800)); // Match your JFrame
        setDoubleBuffered(true);
        loadImages();
        x2 = jungle1.getWidth(null); // Start second image right after first

        timer = new Timer(25, this); // 60 FPS
        timer.start();
    }

    private void loadImages() {
        jungle1 = new ImageIcon(getClass().getResource("/resources/Bg1.jpg")).getImage();
        jungle2 = new ImageIcon(getClass().getResource("/resources/Bg2.jpg")).getImage();

        if (jungle1 == null || jungle2 == null) {
        System.err.println("Images not loaded! Check the file paths and resource locations.");
    }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(jungle1, x1, 0, this);
        g.drawImage(jungle2, x2, 0, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        x1 -= SPEED;
        x2 -= SPEED;

        int width = jungle1.getWidth(null);

        if (x1 + width < 0) x1 = x2 + width;
        if (x2 + width < 0) x2 = x1 + width;

        repaint();
    }
    public static void main(String[] args) {
    JFrame testFrame = new JFrame("Test Jungle Background");
    testFrame.setSize(1200, 800);
    testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    testFrame.setLocationRelativeTo(null);
    testFrame.add(new GamePanel());
    testFrame.setVisible(true);
    }
    
    

}
