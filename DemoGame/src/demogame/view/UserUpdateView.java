package demogame.view;
import demogame.util.RoundedPanel;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.util.List;

import java.awt.*;
import java.awt.image.BufferedImage;

import demogame.model.Feedback;
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
    // for feedback
    public JSlider ratingSlider;
    public JTextArea feedbackTextArea;
    public JButton submitFeedbackButton;
    public JPanel feedbackListPanel;
    public JScrollPane feedbackScrollPane;

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
    // Create main container with BorderLayout
    JPanel mainContainer = new JPanel(new BorderLayout(20, 0));
    mainContainer.setBackground(new Color(255, 250, 205));
    mainContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Left panel for profile
    RoundedPanel leftPanel = new RoundedPanel(30);
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
    leftPanel.setBackground(new Color(255, 250, 205));
    leftPanel.setPreferredSize(new Dimension(500, 800));
    leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Add profile components to left panel
    addSwingingMonkey(leftPanel);
    addTitle(leftPanel);
    addProfileSection(leftPanel, currentUsername, currentEmail);
    addButtons(leftPanel);

    // Right panel for feedback
    RoundedPanel rightPanel = new RoundedPanel(30);
    rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
    rightPanel.setBackground(new Color(255, 250, 205));
    rightPanel.setPreferredSize(new Dimension(600, 800));
    rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Add feedback section to right panel
    addFeedbackSection(rightPanel);

    // Add panels to main container
    mainContainer.add(leftPanel, BorderLayout.WEST);
    mainContainer.add(rightPanel, BorderLayout.EAST);

    // Add main container to frame
    getContentPane().add(mainContainer, BorderLayout.CENTER);
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
        mainPanel.setPreferredSize(new Dimension(600, 1000));
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

            ImageIcon monkeyIcon = new ImageIcon(getClass().getResource("/resources/1.png"));
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
    // FOR FEEDBACKINPUT
    // Add this method to display feedback items
public void updateFeedbackList(List<Feedback> feedbackList) {
    feedbackListPanel.removeAll();
    
    for (Feedback feedback : feedbackList) {
        JPanel feedbackItemPanel = new JPanel();
        feedbackItemPanel.setLayout(new BoxLayout(feedbackItemPanel, BoxLayout.Y_AXIS));
        feedbackItemPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 140, 0), 1));
        feedbackItemPanel.setBackground(new Color(255, 250, 205));
        
        // Rating display with stars
        JLabel ratingLabel = new JLabel("Rating: " + "⭐".repeat(feedback.getRating()));
        ratingLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        feedbackItemPanel.add(ratingLabel);
        
        // Feedback text display
        JTextArea textArea = new JTextArea(feedback.getFeedbackText());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        textArea.setBackground(new Color(255, 250, 205));
        feedbackItemPanel.add(textArea);
        
        // Buttons for edit and delete
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton editButton = createFunkyButton("Edit");
        editButton.setActionCommand("edit_" + feedback.getId());
        editButton.setPreferredSize(new Dimension(100, 30));
        
        JButton deleteButton = createFunkyButton("Delete");
        deleteButton.setActionCommand("delete_" + feedback.getId());
        deleteButton.setPreferredSize(new Dimension(100, 30));
        
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        feedbackItemPanel.add(buttonPanel);
        
        feedbackListPanel.add(feedbackItemPanel);
        feedbackListPanel.add(Box.createVerticalStrut(10));
    }
    
    feedbackListPanel.revalidate();
    feedbackListPanel.repaint();
}


private void addFeedbackSection(RoundedPanel mainPanel) {
    // Create main feedback panel
    JPanel feedbackInputPanel = new JPanel();
    feedbackInputPanel.setOpaque(false);
    feedbackInputPanel.setLayout(new BoxLayout(feedbackInputPanel, BoxLayout.Y_AXIS));
    feedbackInputPanel.setPreferredSize(new Dimension(500, 300));
    feedbackInputPanel.setMaximumSize(new Dimension(500, 300));
    feedbackInputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    feedbackInputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Title section
    JLabel feedbackTitle = new JLabel("Your Feedback Matters! 😊", SwingConstants.CENTER);
    feedbackTitle.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
    feedbackTitle.setForeground(new Color(255, 140, 0));
    feedbackTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
    feedbackInputPanel.add(feedbackTitle);
    feedbackInputPanel.add(Box.createVerticalStrut(10));

    // Rating section
    JPanel ratingPanel = new JPanel();
    ratingPanel.setOpaque(false);
    ratingPanel.setLayout(new BoxLayout(ratingPanel, BoxLayout.Y_AXIS));
    ratingPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel ratingLabel = new JLabel("Rate your experience (1-5):", SwingConstants.CENTER);
    ratingLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
    ratingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    ratingPanel.add(ratingLabel);

    // Rating slider with custom styling
    ratingSlider = new JSlider(JSlider.HORIZONTAL, 1, 5, 3);
    ratingSlider.setMajorTickSpacing(1);
    ratingSlider.setPaintTicks(true);
    ratingSlider.setPaintLabels(true);
    ratingSlider.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
    ratingSlider.setBackground(new Color(255, 250, 205));
    ratingSlider.setPreferredSize(new Dimension(300, 50));
    ratingSlider.setMaximumSize(new Dimension(300, 50));
    ratingSlider.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Add slider change listener for validation
    ratingSlider.addChangeListener(e -> {
        if (ratingSlider.getValue() < 1) {
            ratingSlider.setValue(1);
        } else if (ratingSlider.getValue() > 5) {
            ratingSlider.setValue(5);
        }
        // Update label to show stars based on rating
        ratingLabel.setText("Rate your experience: " + "⭐".repeat(ratingSlider.getValue()));
    });

    ratingPanel.add(ratingSlider);
    feedbackInputPanel.add(ratingPanel);
    feedbackInputPanel.add(Box.createVerticalStrut(10));

    // Feedback text area section
    JLabel textLabel = new JLabel("Share your thoughts (max 500 characters):", SwingConstants.LEFT);
    textLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
    textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    feedbackInputPanel.add(textLabel);

    // Text area with character limit
    feedbackTextArea = new JTextArea();
    feedbackTextArea.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
    feedbackTextArea.setLineWrap(true);
    feedbackTextArea.setWrapStyleWord(true);
    feedbackTextArea.setRows(4);
    
    
    // Add character limit to feedback text area
    feedbackTextArea.setDocument(new PlainDocument() {
        @Override
        public void insertString(int offs, String str, AttributeSet a) 
            throws BadLocationException {
            if (str == null) return;
            if ((getLength() + str.length()) <= 500) {
                super.insertString(offs, str, a);
            }
        }
    });

    // Character counter label
    JLabel charCountLabel = new JLabel("0/500");
    charCountLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
    charCountLabel.setForeground(Color.GRAY);

    // Add document listener to update character count
    feedbackTextArea.getDocument().addDocumentListener(new DocumentListener() {
        private void updateCount() {
            charCountLabel.setText(feedbackTextArea.getText().length() + "/500");
        }
        @Override
        public void insertUpdate(DocumentEvent e) { updateCount(); }
        @Override
        public void removeUpdate(DocumentEvent e) { updateCount(); }
        @Override
        public void changedUpdate(DocumentEvent e) { updateCount(); }
    });

    // Create scroll pane for text area
    JScrollPane scrollPane = new JScrollPane(feedbackTextArea);
    scrollPane.setPreferredSize(new Dimension(400, 80));
    scrollPane.setMaximumSize(new Dimension(400, 80));
    scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 140, 0), 1));
    feedbackInputPanel.add(scrollPane);
    feedbackInputPanel.add(charCountLabel);
    feedbackInputPanel.add(Box.createVerticalStrut(10));

    // Submit button
    submitFeedbackButton = createFunkyButton("Submit ");
    submitFeedbackButton.setPreferredSize(new Dimension(150, 40));
    submitFeedbackButton.setMaximumSize(new Dimension(150, 35));
    submitFeedbackButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    feedbackInputPanel.add(submitFeedbackButton);

    mainPanel.add(feedbackInputPanel);
    mainPanel.add(Box.createVerticalStrut(20));

    // Feedback list section, previous
    JLabel previousFeedbackLabel = new JLabel("Your Previous Feedback", SwingConstants.CENTER);
    previousFeedbackLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
    previousFeedbackLabel.setForeground(new Color(255, 140, 0));
    previousFeedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    mainPanel.add(previousFeedbackLabel);
    mainPanel.add(Box.createVerticalStrut(15));

    // Panel for listing feedback
    feedbackListPanel = new JPanel();
    feedbackListPanel.setLayout(new BoxLayout(feedbackListPanel, BoxLayout.Y_AXIS));
    feedbackListPanel.setOpaque(false);

    // Scroll pane for feedback list
    feedbackScrollPane = new JScrollPane(feedbackListPanel);
    feedbackScrollPane.setPreferredSize(new Dimension(450, 200));
    feedbackScrollPane.setMaximumSize(new Dimension(450, 200));
    feedbackScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
    feedbackScrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 140, 0), 1));
    feedbackScrollPane.getVerticalScrollBar().setUnitIncrement(16);
    
    // Custom scroll bar styling
    feedbackScrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(255, 140, 0);
            this.trackColor = new Color(255, 250, 205);
        }
        
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
    });

    mainPanel.add(feedbackScrollPane);
    mainPanel.add(Box.createVerticalStrut(20));
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
    // method for feedback
    public int getFeedbackRating(){
        return ratingSlider.getValue();
    }
    public String getFeedbackText(){
        return feedbackTextArea.getText();
    }
    public JButton getSubmitFeedbackButton(){
        return submitFeedbackButton;
    }
    public void clearFeedbackInput() {
        ratingSlider.setValue(3);
        feedbackTextArea.setText("");
    }
    




    @Override
    public void dispose() {
        if (monkeyTimer != null && monkeyTimer.isRunning()) {
            monkeyTimer.stop();
        }
        super.dispose();
    }
}