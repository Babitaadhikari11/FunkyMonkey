package demogame.controller;

import demogame.view.AdminView1;
import demogame.view.AdminView2;
import demogame.view.AdminView3;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class AdminController {
    private static final Logger LOGGER = Logger.getLogger(AdminController.class.getName());
    
    private AdminView1 dashboardFrame;
    private AdminView2 userFrame;
    private AdminView3 notificationFrame;

    public AdminController() {
        try {
            LOGGER.info("Initializing AdminController");
            
            // Initialize all frames
            dashboardFrame = new AdminView1();
            userFrame = new AdminView2();
            notificationFrame = new AdminView3();

            // Configure frames
            configureFrame(dashboardFrame, "Dashboard");
            configureFrame(userFrame, "User Management");
            configureFrame(notificationFrame, "Notifications");

            // Set up navigation button listeners
            setupNavigation();
            
            // Show dashboard by default
            showDashboard();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize AdminController", e);
            System.exit(1);
        }
    }

    private void configureFrame(JFrame frame, String title) {
        try {
            frame.setTitle(title);
            frame.setLocationRelativeTo(null); // Center the frame
            frame.setMinimumSize(new java.awt.Dimension(800, 600)); // Ensure minimum size
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensure consistent close behavior
            LOGGER.info("Configured frame: " + title);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error configuring frame: " + title, e);
        }
    }

    private void setupNavigation() {
        try {
            LOGGER.info("Setting up navigation listeners");

            // Dashboard button listeners
            dashboardFrame.jButton1.addActionListener(e -> navigateToDashboard(e));
            userFrame.jButton1.addActionListener(e -> navigateToDashboard(e));
            notificationFrame.jButton1.addActionListener(e -> navigateToDashboard(e));

            // User button listeners
            dashboardFrame.jButton2.addActionListener(e -> navigateToUser(e));
            userFrame.jButton2.addActionListener(e -> navigateToUser(e));
            notificationFrame.jButton2.addActionListener(e -> navigateToUser(e));

            // Notification button listeners
            dashboardFrame.jButton3.addActionListener(e -> navigateToNotification(e));
            userFrame.jButton3.addActionListener(e -> navigateToNotification(e));
            notificationFrame.jButton3.addActionListener(e -> navigateToNotification(e));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error setting up navigation listeners", e);
        }
    }

    private void navigateToDashboard(ActionEvent e) {
        LOGGER.info("Navigating to Dashboard from button: " + e.getSource());
        showDashboard();
    }

    private void navigateToUser(ActionEvent e) {
        LOGGER.info("Navigating to User from button: " + e.getSource());
        showUser();
    }

    private void navigateToNotification(ActionEvent e) {
        LOGGER.info("Navigating to Notification from button: " + e.getSource());
        showNotification();
    }

    private void showDashboard() {
        try {
            hideAllFrames();
            dashboardFrame.setVisible(true);
            LOGGER.info("Dashboard frame set visible");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error showing Dashboard frame", e);
        }
    }

    private void showUser() {
        try {
            hideAllFrames();
            userFrame.setVisible(true);
            LOGGER.info("User frame set visible");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error showing User frame", e);
        }
    }

    private void showNotification() {
        try {
            hideAllFrames();
            notificationFrame.setVisible(true);
            LOGGER.info("Notification frame set visible");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error showing Notification frame", e);
        }
    }

    private void hideAllFrames() {
        try {
            dashboardFrame.setVisible(false);
            userFrame.setVisible(false);
            notificationFrame.setVisible(false);
            LOGGER.info("All frames hidden");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error hiding frames", e);
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            try {
                LOGGER.info("Starting AdminController application");
                new AdminController();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error starting application", e);
                System.exit(1);
            }
        });
    }
}