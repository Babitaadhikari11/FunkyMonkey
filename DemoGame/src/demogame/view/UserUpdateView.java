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
    
}
