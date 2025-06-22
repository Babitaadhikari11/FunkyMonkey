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
    private JButton viewHistoryButton;
    private JLabel monkeyLabel;
    private Timer monkeyTimer;
    private int monkeyX = 0;
    private boolean movingRight = true;
    private final int PROFILE_PIC_SIZE = 150;

    public UserUpdateView(String currentUsername, String currentEmail) {
        initializeFrame();
        createMainPanel(currentUsername, currentEmail);
    }

    private void initializeFrame() {
        setTitle("Funky Profile Update!");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(255, 223, 186));
        setLayout(new BorderLayout()); // Use BorderLayout for main frame
    }

    private void createMainPanel(String currentUsername, String currentEmail) {
        RoundedPanel mainPanel = createRoundedPanel();
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        // Set BoxLayout to stack components vertically
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        addSwingingMonkey(mainPanel);
        addTitle(mainPanel);
        addProfileSection(mainPanel, currentUsername, currentEmail);
        addButtons(mainPanel);
    }
      // Update dispose() to stop timer only
    public void cleanup() {
        if (monkeyTimer != null && monkeyTimer.isRunning()) {
            monkeyTimer.stop();
        }
    }

    private RoundedPanel createRoundedPanel() {
        RoundedPanel mainPanel = new RoundedPanel(30);
        mainPanel.setBackground(new Color(255, 250, 205));
        mainPanel.setPreferredSize(new Dimension(600, 700));
        mainPanel.setMaximumSize(new Dimension(600, 700));
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return mainPanel;
    }

    private void addSwingingMonkey(RoundedPanel mainPanel) {
        try {
            // Create a panel for the monkey animation
            JPanel monkeyPanel = new JPanel();
            monkeyPanel.setOpaque(false);
            monkeyPanel.setLayout(null); // Use null layout for animation
            monkeyPanel.setPreferredSize(new Dimension(600, 100));
            monkeyPanel.setMaximumSize(new Dimension(600, 100));
            monkeyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            ImageIcon monkeyIcon = new ImageIcon(getClass().getResource("/resources/mon.png"));
            Image scaledMonkey = monkeyIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            monkeyLabel = new JLabel(new ImageIcon(scaledMonkey));
            monkeyLabel.setBounds(monkeyX, 10, 80, 80);
            monkeyPanel.add(monkeyLabel);

            mainPanel.add(monkeyPanel);
            mainPanel.add(Box.createVerticalStrut(10)); // Spacer

            setupMonkeyAnimation();
        } catch (Exception e) {
            System.err.println("Monkey image not found: " + e.getMessage());
        }
    }

    private void setupMonkeyAnimation() {
        monkeyTimer = new Timer(50, e -> {
            if (movingRight) {
                monkeyX += 5;
                if (monkeyX >= 520) movingRight = false;
            } else {
                monkeyX -= 5;
                if (monkeyX <= 0) movingRight = true;
            }
            monkeyLabel.setLocation(monkeyX, 10);
        });
        monkeyTimer.start();
    }

    private void addTitle(RoundedPanel mainPanel) {
        JLabel titleLabel = new JLabel("Your Funky Profile!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        titleLabel.setForeground(new Color(255, 140, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20)); // Spacer
    }

    private void addProfileSection(RoundedPanel mainPanel, String username, String email) {
        // Profile Picture
        profilePicLabel = new JLabel();
        profilePicLabel.setPreferredSize(new Dimension(PROFILE_PIC_SIZE, PROFILE_PIC_SIZE));
        profilePicLabel.setMaximumSize(new Dimension(PROFILE_PIC_SIZE, PROFILE_PIC_SIZE));
        profilePicLabel.setBorder(BorderFactory.createLineBorder(new Color(255, 140, 0), 3));
        profilePicLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        setDefaultProfilePic();
        mainPanel.add(profilePicLabel);
        mainPanel.add(Box.createVerticalStrut(20)); // Spacer

        // Username
        usernameLabel = new JLabel("Username: " + username, SwingConstants.CENTER);
        usernameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        usernameLabel.setForeground(new Color(139, 69, 19));
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(usernameLabel);
        mainPanel.add(Box.createVerticalStrut(10)); // Spacer

        // Email
        emailLabel = new JLabel("Email: " + email, SwingConstants.CENTER);
        emailLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        emailLabel.setForeground(new Color(139, 69, 19));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(emailLabel);
        mainPanel.add(Box.createVerticalStrut(20)); // Spacer
    }

    private void addButtons(RoundedPanel mainPanel) {
        // Create a panel for buttons to arrange them in a grid
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(2, 2, 20, 20)); // 2 rows, 2 columns
        buttonPanel.setPreferredSize(new Dimension(450, 100));
        buttonPanel.setMaximumSize(new Dimension(450, 100));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons
        uploadPhotoButton = createFunkyButton("📸 Upload Photo");
        editNameButton = createFunkyButton("✏️ Edit Name");
        viewHistoryButton = createFunkyButton("📋 View History");
        deleteAccountButton = createFunkyButton("❌ Delete Account");

        buttonPanel.add(uploadPhotoButton);
        buttonPanel.add(viewHistoryButton);
        buttonPanel.add(editNameButton);
        buttonPanel.add(deleteAccountButton);

        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(30)); // Spacer

        // Back Button
        backButton = createFunkyButton("🔙 Back to Menu");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(backButton);
    }

    private JButton createFunkyButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        button.setBackground(new Color(255, 223, 0));
        button.setForeground(new Color(139, 69, 19));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 40));
        button.setMaximumSize(new Dimension(200, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        addButtonHoverEffect(button);
        return button;
    }

    private void addButtonHoverEffect(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 200, 0));
                button.setFont(new Font("Comic Sans MS", Font.BOLD, 17));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 223, 0));
                button.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
            }
        });
    }
    

    private void setDefaultProfilePic() {
        BufferedImage defaultPic = new BufferedImage(PROFILE_PIC_SIZE, PROFILE_PIC_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = defaultPic.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(200, 200, 200));
        g2d.fillOval(0, 0, PROFILE_PIC_SIZE - 1, PROFILE_PIC_SIZE - 1);

        g2d.setColor(new Color(150, 150, 150));
        g2d.fillOval(PROFILE_PIC_SIZE / 3 - 10, PROFILE_PIC_SIZE / 3, 20, 20);
        g2d.fillOval(2 * PROFILE_PIC_SIZE / 3 - 10, PROFILE_PIC_SIZE / 3, 20, 20);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawArc(PROFILE_PIC_SIZE / 4, PROFILE_PIC_SIZE / 3, PROFILE_PIC_SIZE / 2, PROFILE_PIC_SIZE / 2, 0, -180);

        g2d.dispose();
        profilePicLabel.setIcon(new ImageIcon(defaultPic));
    }

    // Public methods
    public void setProfilePicture(Image image) {
        Image scaled = image.getScaledInstance(PROFILE_PIC_SIZE, PROFILE_PIC_SIZE, Image.SCALE_SMOOTH);
        profilePicLabel.setIcon(new ImageIcon(scaled));
    }

    public void updateUsername(String newUsername) {
        usernameLabel.setText("Username: " + newUsername);
    }

    // Getters
    public JButton getUploadPhotoButton() { return uploadPhotoButton; }
    public JButton getEditNameButton() { return editNameButton; }
    public JButton getDeleteAccountButton() { return deleteAccountButton; }
    public JButton getBackButton() { return backButton; }
    public JButton getViewHistoryButton() { return viewHistoryButton; }
    public JLabel getProfilePicLabel() { return profilePicLabel; }
    public String getCurrentUsername() {
        return usernameLabel.getText().replace("Username: ", "");
    }

    // Dialog methods
    public void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE));
    }

    public void showSuccess(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE));
    }

    public int showConfirmDialog(String message) {
        return JOptionPane.showConfirmDialog(this, message, "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void dispose() {
        if (monkeyTimer != null && monkeyTimer.isRunning()) {
            monkeyTimer.stop();
        }
        super.dispose();
    }
}