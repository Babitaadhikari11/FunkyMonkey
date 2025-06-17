package demogame.controller;

import demogame.dao.ScoreDao;
import demogame.dao.UserDao;
import demogame.model.UserData;
import demogame.view.AdminView1;
import demogame.view.AdminView2;
import demogame.view.AdminView3;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class AdminController {
    private static final Logger LOGGER = Logger.getLogger(AdminController.class.getName());

    // All final fields are declared here
    private final UserDao userDao;
    private final ScoreDao scoreDao;
    private final AdminView1 dashboardFrame;
    private final AdminView2 userFrame;
    private final AdminView3 notificationFrame;

    public AdminController() {
        // CORRECTED CONSTRUCTOR: No try-catch block around initializations.
        LOGGER.info("Initializing AdminController");

        // Initialize DAOs and Views directly.
        this.userDao = new UserDao();
        this.scoreDao = new ScoreDao();
        this.dashboardFrame = new AdminView1();
        this.userFrame = new AdminView2();
        this.notificationFrame = new AdminView3();

        // Now that everything is created, configure them
        configureFrame(dashboardFrame, "Dashboard");
        configureFrame(userFrame, "User Management");
        configureFrame(notificationFrame, "Notifications");

        // Set up navigation button listeners
        setupNavigation();

        // Load initial data for the dashboard
        loadDashboardData();

        // Show the first frame
        showDashboard();
    }

    private void loadDashboardData() {
        // The try-catch here is fine because it doesn't involve final fields
        try {
            LOGGER.info("Loading dashboard data...");
            int totalUsers = userDao.getTotalUserCount();
            int activeUsers = scoreDao.getActiveUserCount();
            String newestUser = userDao.getNewestUser();
            dashboardFrame.updateDashboardStats(totalUsers, activeUsers, newestUser);
            LOGGER.info("Dashboard data loaded successfully.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading dashboard data", e);
            dashboardFrame.updateDashboardStats(-1, -1, "Error");
        }
    }

    private void loadAndDisplayUsers() {
        try {
            LOGGER.info("Loading all users...");
            List<UserData> userList = userDao.getAllUsers();
            userFrame.displayUsers(userList);
            LOGGER.info("Successfully loaded and displayed " + userList.size() + " users.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load users", e);
        }
    }
    
    // ... [ The rest of your methods like addUser, deleteUser, navigation methods, etc. go here ] ...
    // ... [ They are unchanged from the previous complete version I sent ] ...
    
    public void addUser(String name, String username, String email, String password) {
        UserData newUser = new UserData(0, username, email, password);
        boolean success = userDao.register(newUser);
        if (success) {
            JOptionPane.showMessageDialog(userFrame, "User added successfully!");
            loadAndDisplayUsers();
        } else {
            JOptionPane.showMessageDialog(userFrame, "Error adding user: " + userDao.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteUser(String username) {
        if (username == null || username.isEmpty()) {
            JOptionPane.showMessageDialog(userFrame, "Please select a user to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int response = JOptionPane.showConfirmDialog(userFrame, "Are you sure you want to delete the user '" + username + "'?\nThis action cannot be undone.", "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            boolean success = userDao.deleteUserByUsername(username);
            if (success) {
                JOptionPane.showMessageDialog(userFrame, "User '" + username + "' has been deleted.");
                loadAndDisplayUsers();
            } else {
                JOptionPane.showMessageDialog(userFrame, "Error deleting user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void configureFrame(JFrame frame, String title) {
        frame.setTitle(title);
        frame.setLocationRelativeTo(null);
        frame.setMinimumSize(new java.awt.Dimension(800, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setupNavigation() {
        dashboardFrame.jButton1.addActionListener(e -> navigateToDashboard());
        userFrame.jButton1.addActionListener(e -> navigateToDashboard());
        notificationFrame.jButton1.addActionListener(e -> navigateToDashboard());

        dashboardFrame.jButton2.addActionListener(e -> navigateToUser());
        userFrame.jButton2.addActionListener(e -> navigateToUser());
        notificationFrame.jButton2.addActionListener(e -> navigateToUser());

        dashboardFrame.jButton3.addActionListener(e -> navigateToNotification());
        userFrame.jButton3.addActionListener(e -> navigateToNotification());
        notificationFrame.jButton3.addActionListener(e -> navigateToNotification());
    }

    private void navigateToDashboard() { showDashboard(); }
    private void navigateToUser() { loadAndDisplayUsers(); showUser(); }
    private void navigateToNotification() { showNotification(); }
    private void showDashboard() { hideAllFrames(); dashboardFrame.setVisible(true); }
    private void showUser() { hideAllFrames(); userFrame.setVisible(true); }
    private void showNotification() { hideAllFrames(); notificationFrame.setVisible(true); }
    private void hideAllFrames() {
        dashboardFrame.setVisible(false);
        userFrame.setVisible(false);
        notificationFrame.setVisible(false);
    }

    public static void main(String[] args) {
        // The try-catch for starting the whole application belongs here, in main.
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new AdminController();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "A critical error occurred trying to start the application.", e);
                System.exit(1);
            }
        });
    }
}