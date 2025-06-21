package demogame.view;

import javax.swing.*;
import java.awt.*;
import demogame.controller.AdminController;
import java.util.logging.Logger;
import java.util.logging.Level;

public class AdminLoginView extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(AdminLoginView.class.getName());
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private AdminController adminController;

    public AdminLoginView() {
        initializeComponents();
    }

    private void initializeComponents() {
        try {
            setTitle("Admin Login");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(400, 200);
            setLocationRelativeTo(null);
            setResizable(false);

            // Main panel with padding
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Login panel
            JPanel loginPanel = new JPanel(new GridLayout(2, 2, 10, 10));

            // Username field
            JLabel usernameLabel = new JLabel("Username:");
            usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            loginPanel.add(usernameLabel);
            
            usernameField = new JTextField();
            usernameField.setPreferredSize(new Dimension(150, 25));
            loginPanel.add(usernameField);

            // Password field
            JLabel passwordLabel = new JLabel("Password:");
            passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
            loginPanel.add(passwordLabel);
            
            passwordField = new JPasswordField();
            passwordField.setPreferredSize(new Dimension(150, 25));
            loginPanel.add(passwordField);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            
            JButton loginButton = new JButton("Login");
            loginButton.setFont(new Font("Arial", Font.BOLD, 14));
            loginButton.setPreferredSize(new Dimension(100, 35));
            
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
            cancelButton.setPreferredSize(new Dimension(100, 35));

            buttonPanel.add(loginButton);
            buttonPanel.add(cancelButton);

            // Add panels to main panel
            mainPanel.add(loginPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            // Add main panel to frame
            add(mainPanel);

            // Add action listeners
            loginButton.addActionListener(e -> login());
            cancelButton.addActionListener(e -> System.exit(0));

            // Make Enter key work for login
            getRootPane().setDefaultButton(loginButton);

            // Set background colors
            mainPanel.setBackground(new Color(240, 240, 240));
            loginPanel.setBackground(new Color(240, 240, 240));
            buttonPanel.setBackground(new Color(240, 240, 240));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing admin login view", e);
            showError("Error initializing login window: " + e.getMessage());
        }
    }
private void login() {
    String username = usernameField.getText();
    String password = new String(passwordField.getPassword());

    LOGGER.info("Login attempt with username: " + username);

    if (username.equals("admin") && password.equals("admin123")) {
        try {
            LOGGER.info("Admin credentials verified");
            dispose();

            SwingUtilities.invokeLater(() -> {
                try {
                    LOGGER.info("Creating AdminController...");
                    adminController = new AdminController();
                    
                    JFrame dashboard = adminController.getCorrectDashboardFrame();
                    if (dashboard != null) {
                        dashboard.setVisible(true);
                        dashboard.toFront();
                        LOGGER.info("Admin dashboard opened successfully");
                    } else {
                        LOGGER.severe("Dashboard frame is null");
                        showError("Error opening dashboard: Frame is null");
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error creating AdminController", e);
                    e.printStackTrace();
                    showError("Error starting admin panel: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during login process", e);
            e.printStackTrace();
            showError("Error during login process: " + e.getMessage());
        }
    } else {
        LOGGER.warning("Invalid login attempt");
        JOptionPane.showMessageDialog(this,
            "Invalid admin credentials",
            "Login Failed",
            JOptionPane.ERROR_MESSAGE);
        passwordField.setText("");
    }
}

    private void showError(String message) {
        SwingUtilities.invokeLater(() -> 
            JOptionPane.showMessageDialog(null,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE));
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            SwingUtilities.invokeLater(() -> {
                try {
                    AdminLoginView loginView = new AdminLoginView();
                    loginView.setVisible(true);
                    LOGGER.info("Admin login view started");
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error starting application", e);
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                        "Error starting application: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error setting look and feel", e);
            e.printStackTrace();
        }
    }
}