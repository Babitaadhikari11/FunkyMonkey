package demogame.view;
import demogame.util.RoundedPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class UserUpdateView {
    private JLabel profilePicLabel;
    private JLabel usernameLabel;
    private JLabel emailLabel;
    private JButton uploadPhotoButton;
    private JButton editNameButton;
    private JButton deleteAccountButton;
    private JButton backButton;
    private JButton viewHistoryButton; // Added for change history
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
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Changed to DISPOSE_ON_CLOSE
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(255, 223, 186));
    }

    private void createMainPanel(String currentUsername, String currentEmail) {
        RoundedPanel mainPanel = createRoundedPanel();
        add(mainPanel);

        addSwingingMonkey(mainPanel);
        addTitle(mainPanel);
        addProfileSection(mainPanel, currentUsername, currentEmail);
        addButtons(mainPanel);
    }
    private RoundedPanel createRoundedPanel() {
        RoundedPanel mainPanel = new RoundedPanel(30);
        mainPanel.setBackground(new Color(255, 250, 205));
        mainPanel.setLayout(null);
        
        int panelWidth = 600;
        int panelHeight = 700;
        int x = (1200 - panelWidth) / 2;
        int y = (800 - panelHeight) / 2;
        mainPanel.setBounds(x, y, panelWidth, panelHeight);
        
        return mainPanel;
    }
    //optional
       private void addSwingingMonkey(RoundedPanel mainPanel) {
        try {
            ImageIcon monkeyIcon = new ImageIcon(getClass().getResource("/resources/mon.png"));
            Image scaledMonkey = monkeyIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            monkeyLabel = new JLabel(new ImageIcon(scaledMonkey));
            monkeyLabel.setBounds(monkeyX, 20, 80, 80);
            mainPanel.add(monkeyLabel);

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
            monkeyLabel.setLocation(monkeyX, 20);
        });
        monkeyTimer.start();
    }
    private void addTitle(RoundedPanel mainPanel) {
        JLabel titleLabel = new JLabel("Your Funky Profile!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        titleLabel.setForeground(new Color(255, 140, 0));
        titleLabel.setBounds(0, 120, 600, 40);
        mainPanel.add(titleLabel);
    }

    private void addProfileSection(RoundedPanel mainPanel, String username, String email) {
        // Profile Picture
        profilePicLabel = new JLabel();
        profilePicLabel.setBounds(225, 180, PROFILE_PIC_SIZE, PROFILE_PIC_SIZE);
        profilePicLabel.setBorder(BorderFactory.createLineBorder(new Color(255, 140, 0), 3));
        setDefaultProfilePic();
        mainPanel.add(profilePicLabel);

        // Username
        usernameLabel = new JLabel("Username: " + username, SwingConstants.CENTER);
        usernameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        usernameLabel.setForeground(new Color(139, 69, 19));
        usernameLabel.setBounds(0, 350, 600, 30);
        mainPanel.add(usernameLabel);

        // Email
        emailLabel = new JLabel("Email: " + email, SwingConstants.CENTER);
        emailLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        emailLabel.setForeground(new Color(139, 69, 19));
        emailLabel.setBounds(0, 390, 600, 30);
        mainPanel.add(emailLabel);
    }
     private void addButtons(RoundedPanel mainPanel) {
        int buttonWidth = 300;
        int buttonX = 150;
        
        uploadPhotoButton = createFunkyButton("📸 Upload Photo", 450, buttonX, buttonWidth);
        editNameButton = createFunkyButton("✏️ Edit Name", 500, buttonX, buttonWidth);
        viewHistoryButton = createFunkyButton("📋 View History", 550, buttonX, buttonWidth);
        deleteAccountButton = createFunkyButton("❌ Delete Account", 600, buttonX, buttonWidth);
        backButton = createFunkyButton("🔙 Back to Menu", 650, buttonX, buttonWidth);

        mainPanel.add(uploadPhotoButton);
        mainPanel.add(editNameButton);
        mainPanel.add(viewHistoryButton);
        mainPanel.add(deleteAccountButton);
        mainPanel.add(backButton);
    }
     private JButton createFunkyButton(String text, int yPos, int xPos, int width) {
        JButton button = new JButton(text);
        button.setBounds(xPos, yPos, width, 40);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        button.setBackground(new Color(255, 223, 0));
        button.setForeground(new Color(139, 69, 19));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

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
        g2d.fillOval(PROFILE_PIC_SIZE/3 - 10, PROFILE_PIC_SIZE/3, 20, 20);
        g2d.fillOval(2*PROFILE_PIC_SIZE/3 - 10, PROFILE_PIC_SIZE/3, 20, 20);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawArc(PROFILE_PIC_SIZE/4, PROFILE_PIC_SIZE/3, PROFILE_PIC_SIZE/2, PROFILE_PIC_SIZE/2, 0, -180);
        
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

    
}
