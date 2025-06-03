
package demogame.view;
import java.awt.*;
import javax.swing.*;

public class MenuView extends JFrame {
    private JLabel welcomeLabel;
    private JButton startButton;
    private JButton updateButton;
    private JButton quitButton;
    private JToggleButton soundButton;
    private Timer animationTimer;
    private int welcomeX = 20;
    private boolean movingRight = true;

    public MenuView(String username) {
        // Basic frame setup
        setTitle("DemoGame - Main Menu");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create layered pane
        JLayeredPane layeredPane = new JLayeredPane();
        getContentPane().add(layeredPane);

        // Set background
        try {
            ImageIcon bgIcon = new ImageIcon(getClass().getResource("/resources/menu.jpeg"));
            Image scaled = bgIcon.getImage().getScaledInstance(1200, 800, Image.SCALE_SMOOTH);
            JLabel background = new JLabel(new ImageIcon(scaled));
            background.setBounds(0, 0, 1200, 800);
            layeredPane.add(background, Integer.valueOf(0));
        } catch (Exception e) {
            System.err.println("Background image not found: " + e.getMessage());
            setBackground(Color.DARK_GRAY);
        }

        // Welcome message with animation
        welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.RED);
        welcomeLabel.setBounds(20, 20, 300, 40);
        layeredPane.add(welcomeLabel, Integer.valueOf(1));

        // Setup animation timer
        animationTimer = new Timer(50, e -> animateWelcomeMessage());
        animationTimer.start();

        // Setup buttons
        setupButtons(layeredPane);
    }

    private void setupButtons(JLayeredPane layeredPane) {
        // Create buttons with images
        startButton = createImageButton("/resources/buttons.png", "Start Game", 250);
        updateButton = createImageButton("/resources/buttons.png", "Update Profile", 380);
        quitButton = createImageButton("/resources/buttons.png", "Quit Game", 510);

        // Center buttons horizontally and position vertically
        int buttonX = (1200 - 300) / 2; // Center horizontally
        startButton.setBounds(buttonX, 280, 300, 80);
        updateButton.setBounds(buttonX, 400, 300, 80);
        quitButton.setBounds(buttonX, 520, 300, 80);

        layeredPane.add(startButton, Integer.valueOf(1));
        layeredPane.add(updateButton, Integer.valueOf(1));
        layeredPane.add(quitButton, Integer.valueOf(1));

        // Sound toggle button (bottom right corner)
        soundButton = new JToggleButton("Sound: ON");
        soundButton.setBounds(1050, 700, 120, 40);
        soundButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        soundButton.setFocusPainted(false);
        soundButton.addActionListener(e -> 
            soundButton.setText(soundButton.isSelected() ? "Sound: OFF" : "Sound: ON")
        );
        layeredPane.add(soundButton, Integer.valueOf(1));
    }

    private JButton createImageButton(String imagePath, String text, int yPosition) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Enable antialiasing for smoother text
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                                   RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Use Comic Sans MS font
                Font buttonFont = new Font("Comic Sans MS", Font.BOLD, 24);
                g2d.setFont(buttonFont);
                
                // Get metrics for precise centering
                FontMetrics fm = g2d.getFontMetrics(buttonFont);
                
                // Calculate center position
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                
                // Center text perfectly
                int textX = (getWidth() - textWidth) / 2;
                int textY = (getHeight() - textHeight) / 2 + fm.getAscent();
                
                // Set color (red for Quit Game, black for others)
                if (text.equals("Quit Game")) {
                    g2d.setColor(Color.RED);
                } else {
                    g2d.setColor(Color.BLACK);
                }
                
                g2d.drawString(text, textX, textY);
            }
        };

        try {
            // Load and scale the button image
            ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
            Image img = icon.getImage().getScaledInstance(300, 80, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            System.err.println("Button image not found: " + imagePath);
        }

        // Button properties
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }
    //animation of welcome message for moving horizontally

    private void animateWelcomeMessage() {
        if (movingRight) {
            welcomeX += 2;
            if (welcomeX >= 280) movingRight = false;
        } else {
            welcomeX -= 2;
            if (welcomeX <= 20) movingRight = true;
        }
        welcomeLabel.setBounds(welcomeX, 20, 300, 40);
    }

    // Getters for buttons
    public JButton getStartButton() { 
        return startButton; 
    }
    public JButton getUpdateButton() { 
        return updateButton; 
    }
    public JButton getQuitButton() { 
        return quitButton; 
    }
    public JToggleButton getSoundButton() {
         return soundButton; 
        }

    // Method to stop the animation timer when closing the frame
    public void dispose() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        super.dispose();
    }
}