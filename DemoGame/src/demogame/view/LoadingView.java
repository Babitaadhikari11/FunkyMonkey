package demogame.view;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class LoadingView extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(LoadingView.class.getName());
    private JLabel logoLabel;
    private JProgressBar progressBar;
    private JLabel tipLabel;
    private JLabel loadingLabel;
    private Timer tipTimer;
    private int currentTipIndex = 0;

    public LoadingView() {
        try {
    setTitle("Loading");
    setSize(1200, 800);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setResizable(false);

    // Set background
    ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/resources/Loadingbg.jpg"));
    if (backgroundIcon.getImage() == null) {
        LOGGER.warning("Failed to load background image: /resources/Loadingbg.jpg");
        setContentPane(new JPanel()); // Fallback to empty panel
    } else {
        setContentPane(new JLabel(backgroundIcon));
    }
    getContentPane().setLayout(new GridBagLayout());

    initializeComponents();
    layoutComponents();
} catch (Exception e) {
    LOGGER.log(Level.SEVERE, "Error initializing LoadingView", e);
    // Do not show JOptionPane; proceed with fallback
    setContentPane(new JPanel()); // Ensure frame has a content pane
    getContentPane().setLayout(new GridBagLayout());
    initializeComponents(); // Retry initializing components
    layoutComponents();
}
    }

    private void initializeComponents() {
        try {
            // Logo setup
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/resources/logoo.png"));
            if (logoIcon.getImage() == null) {
                LOGGER.warning("Failed to load logo image: /resources/logoo.png");
                logoLabel = new JLabel("Funky Monkey");
            } else {
                Image scaledLogo = logoIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                logoLabel = new JLabel(new ImageIcon(scaledLogo));
            }

            // Progress bar
            progressBar = new JProgressBar(0, 100);
            progressBar.setStringPainted(true);
            progressBar.setForeground(Color.RED);
            progressBar.setBackground(Color.WHITE);
            progressBar.setPreferredSize(new Dimension(400, 30));

            // Loading text
            loadingLabel = new JLabel("Loading Game......🐒");
            loadingLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
            loadingLabel.setForeground(Color.BLACK);

            // Tips and tricks section
            tipLabel = new JLabel("Loading tips...");
            tipLabel.setFont(new Font("Comic Sans MS", Font.ITALIC, 16));
            tipLabel.setForeground(Color.BLACK);
            tipLabel.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing components", e);
            JOptionPane.showMessageDialog(null,
                "Error initializing loading screen: " + e.getMessage(),
                "Loading Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void layoutComponents() {
        try {
            GridBagConstraints gbc = new GridBagConstraints();

            // Central panel
            JPanel centerPanel = new JPanel(new GridBagLayout());
            centerPanel.setOpaque(false);

            // Logo label
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 0, 20, 0);
            centerPanel.add(logoLabel, gbc);

            // Loading label
            gbc.gridy = 1;
            gbc.insets = new Insets(0, 0, 10, 0);
            centerPanel.add(loadingLabel, gbc);

            // Progress bar
            gbc.gridy = 2;
            centerPanel.add(progressBar, gbc);

            // Add center panel to frame
            gbc.gridy = 0;
            add(centerPanel, gbc);

            // Tips and tricks at bottom
            gbc.gridy = 1;
            gbc.insets = new Insets(20, 0, 50, 0);
            add(tipLabel, gbc);

            LOGGER.info("LoadingView components laid out successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error laying out components", e);
            JOptionPane.showMessageDialog(null,
                "Error setting up loading screen layout: " + e.getMessage(),
                "Loading Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateProgress(int progress) {
        progressBar.setValue(progress);
        LOGGER.fine("Progress bar updated to: " + progress);
    }

    public void updateTip(String tip) {
        tipLabel.setText(tip);
        LOGGER.fine("Tip updated: " + tip);
    }

    @Override
    public void dispose() {
        if (tipTimer != null) {
            tipTimer.stop();
        }
        super.dispose();
        LOGGER.info("LoadingView disposed");
    }
}