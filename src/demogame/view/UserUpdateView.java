package demogame.view;

import demogame.util.RoundedPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class UserUpdateView extends JFrame {
    private JLabel profilePicLabel;
    private JLabel usernameLabel;
    private JLabel emailLabel;
    private JButton uploadPhotoButton;
    private JButton editNameButton;
    private JButton deleteAccountButton;
    private JButton backButton;
    private JLabel monkeyLabel;
    private Timer monkeyTimer;
    private int monkeyX = 0;
    private boolean movingRight = true;
    private final int PROFILE_PIC_SIZE = 150;

    public UserUpdateView(String currentUsername, String currentEmail) {
        // Basic frame setup
        setTitle("Funky Profile Update!");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Set playful background color
        getContentPane().setBackground(new Color(255, 223, 186));  // Peach color

        // Create main panel with fun colors - Centered
        RoundedPanel mainPanel = new RoundedPanel(30);
        mainPanel.setBackground(new Color(255, 250, 205));  // Light yellow
        mainPanel.setLayout(null);
        
        // Center the panel
        int panelWidth = 600;  // Increased width
        int panelHeight = 700;  // Increased height
        int x = (1200 - panelWidth) / 2;
        int y = (800 - panelHeight) / 2;
        mainPanel.setBounds(x, y, panelWidth, panelHeight);
        add(mainPanel);

        // Add swinging monkey at the top
        try {
            ImageIcon monkeyIcon = new ImageIcon(getClass().getResource("/resources/mon.png"));
            Image scaledMonkey = monkeyIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            monkeyLabel = new JLabel(new ImageIcon(scaledMonkey));
            monkeyLabel.setBounds(monkeyX, 20, 80, 80);
            mainPanel.add(monkeyLabel);

            // Animate monkey
            monkeyTimer = new Timer(50, e -> {
                if (movingRight) {
                    monkeyX += 5;
                    if (monkeyX >= mainPanel.getWidth() - 80) movingRight = false;
                } else {
                    monkeyX -= 5;
                    if (monkeyX <= 0) movingRight = true;
                }
                monkeyLabel.setLocation(monkeyX, 20);
            });
            monkeyTimer.start();
        } catch (Exception e) {
            System.err.println("Monkey image not found: " + e.getMessage());
        }

        // Fun title - Centered
        JLabel titleLabel = new JLabel("Your Funky Profile!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        titleLabel.setForeground(new Color(255, 140, 0));  // Dark Orange
        titleLabel.setBounds(0, 120, panelWidth, 40);
        mainPanel.add(titleLabel);

        // Profile Picture Section - Centered
        profilePicLabel = new JLabel();
        profilePicLabel.setBounds((panelWidth - PROFILE_PIC_SIZE) / 2, 180, PROFILE_PIC_SIZE, PROFILE_PIC_SIZE);
        profilePicLabel.setBorder(BorderFactory.createLineBorder(new Color(255, 140, 0), 3));
        setDefaultProfilePic();
        mainPanel.add(profilePicLabel);

        // Username display - Centered
        usernameLabel = new JLabel("Username: " + currentUsername, SwingConstants.CENTER);
        usernameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        usernameLabel.setForeground(new Color(139, 69, 19));  // Brown
        usernameLabel.setBounds(0, 350, panelWidth, 30);
        mainPanel.add(usernameLabel);

        // Email display - Centered
        emailLabel = new JLabel("Email: " + currentEmail, SwingConstants.CENTER);
        emailLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        emailLabel.setForeground(new Color(139, 69, 19));  // Brown
        emailLabel.setBounds(0, 390, panelWidth, 30);
        mainPanel.add(emailLabel);

        // Buttons - Centered with proper spacing
        int buttonWidth = 300;
        int buttonX = (panelWidth - buttonWidth) / 2;
        
        uploadPhotoButton = createFunkyButton("📸 Upload Photo", 450, buttonX, buttonWidth);
        editNameButton = createFunkyButton("✏️ Edit Name", 500, buttonX, buttonWidth);
        deleteAccountButton = createFunkyButton("❌ Delete Account", 550, buttonX, buttonWidth);
        backButton = createFunkyButton("🔙 Back to Menu", 600, buttonX, buttonWidth);

        mainPanel.add(uploadPhotoButton);
        mainPanel.add(editNameButton);
        mainPanel.add(deleteAccountButton);
        mainPanel.add(backButton);
    }

    private JButton createFunkyButton(String text, int yPos, int xPos, int width) {
        JButton button = new JButton(text);
        button.setBounds(xPos, yPos, width, 40);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        button.setBackground(new Color(255, 223, 0));  // Banana yellow
        button.setForeground(new Color(139, 69, 19));  // Brown text
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add fun hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 200, 0));  // Darker yellow
                button.setFont(new Font("Comic Sans MS", Font.BOLD, 17));  // Slightly bigger
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 223, 0));  // Back to original
                button.setFont(new Font("Comic Sans MS", Font.BOLD, 16));  // Original size
            }
        });

        return button;
    }

    private void setDefaultProfilePic() {
        BufferedImage defaultPic = new BufferedImage(PROFILE_PIC_SIZE, PROFILE_PIC_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = defaultPic.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fill background with light gray
        g2d.setColor(new Color(200, 200, 200));
        g2d.fillOval(0, 0, PROFILE_PIC_SIZE - 1, PROFILE_PIC_SIZE - 1);
        
        // Add a simple face sketch
        g2d.setColor(new Color(150, 150, 150));
        // Eyes
        g2d.fillOval(PROFILE_PIC_SIZE/3 - 10, PROFILE_PIC_SIZE/3, 20, 20);
        g2d.fillOval(2*PROFILE_PIC_SIZE/3 - 10, PROFILE_PIC_SIZE/3, 20, 20);
        // Smile
        g2d.setStroke(new BasicStroke(3));
        g2d.drawArc(PROFILE_PIC_SIZE/4, PROFILE_PIC_SIZE/3, PROFILE_PIC_SIZE/2, PROFILE_PIC_SIZE/2, 0, -180);
        
        g2d.dispose();
        profilePicLabel.setIcon(new ImageIcon(defaultPic));
    }

    // Method to update profile picture
    public void setProfilePicture(Image image) {
        Image scaled = image.getScaledInstance(PROFILE_PIC_SIZE, PROFILE_PIC_SIZE, Image.SCALE_SMOOTH);
        profilePicLabel.setIcon(new ImageIcon(scaled));
    }

    // Method to update username
    public void updateUsername(String newUsername) {
        usernameLabel.setText("Username: " + newUsername);
    }

    // Getters for the buttons and components
    public JButton getUploadPhotoButton() { return uploadPhotoButton; }
    public JButton getEditNameButton() { return editNameButton; }
    public JButton getDeleteAccountButton() { return deleteAccountButton; }
    public JButton getBackButton() { return backButton; }
    public JLabel getProfilePicLabel() { return profilePicLabel; }
    public String getCurrentUsername() { 
        return usernameLabel.getText().replace("Username: ", ""); 
    }

    // Dialog methods for user feedback
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public int showConfirmDialog(String message) {
        return JOptionPane.showConfirmDialog(this, message, "Confirm", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    }

    // Override dispose to stop the timer
    @Override
    public void dispose() {
        if (monkeyTimer != null && monkeyTimer.isRunning()) {
            monkeyTimer.stop();
        }
        super.dispose();
    }
}