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
    
}
