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

    private final UserDao userDao;
    private final ScoreDao scoreDao;
    private final AdminView1 dashboardFrame;
    private final AdminView2 userFrame;
    private final AdminView3 notificationFrame;

    public AdminController() {
        LOGGER.info("Initializing AdminController");

        this.userDao = new UserDao();
        this.scoreDao = new ScoreDao();
        
        // Pass 'this' controller to any view that needs to call its methods
        this.dashboardFrame = new AdminView1(); 
        this.userFrame = new AdminView2(this); // Pass controller to AdminView2
        this.notificationFrame = new AdminView3();

        configureFrame(dashboardFrame, "Dashboard");
        configureFrame(userFrame, "User Management");
        configureFrame(notificationFrame, "Notifications");

        setupNavigation();
        loadDashboardData();
        showDashboard();
    }

    // --- Public methods for AdminView2 to call ---
    
    public UserData getUserDetails(String username) {
        return userDao.getUserByUsername(username);
    }

    public void updateUser(String originalUsername, String newUsername, String newEmail, String newPassword) {
        UserData originalUser = userDao.getUserByUsername(originalUsername);
        if (originalUser == null) {
            JOptionPane.showMessageDialog(userFrame, "Original user not found. Cannot update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserData updatedUser = new UserData(originalUser.getId(), newUsername, newEmail, newPassword);
        
        boolean success = userDao.updateUser(updatedUser);
        if (success) {
            JOptionPane.showMessageDialog(userFrame, "User updated successfully!");
            loadAndDisplayUsers(); // Refresh the list
        } else {
            JOptionPane.showMessageDialog(userFrame, "Failed to update user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
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

    // --- Private helper methods ---

    private void loadDashboardData() {
        try {
            int totalUsers = userDao.getTotalUserCount();
            int activeUsers = scoreDao.getActiveUserCount();
            String newestUser = userDao.getNewestUser();
            dashboardFrame.updateDashboardStats(totalUsers, activeUsers, newestUser);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading dashboard data", e);
            dashboardFrame.updateDashboardStats(-1, -1, "Error");
        }
    }

    private void loadAndDisplayUsers() {
        try {
            List<UserData> userList = userDao.getAllUsers();
            userFrame.displayUsers(userList);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load users", e);
        }
    }

    private void configureFrame(JFrame frame, String title) {
        frame.setTitle(title);
        frame.setLocationRelativeTo(null);
        frame.setMinimumSize(new java.awt.Dimension(1200, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setupNavigation() {
        dashboardFrame.jButton1.addActionListener(e -> navigateToDashboard());
        userFrame.jButton1.addActionListener(e -> navigateToDashboard());
        // notificationFrame.jButton1.addActionListener(e -> navigateToDashboard()); // Assuming AdminView3 has jButton1

        dashboardFrame.jButton2.addActionListener(e -> navigateToUser());
        userFrame.jButton2.addActionListener(e -> navigateToUser());
        // notificationFrame.jButton2.addActionListener(e -> navigateToUser()); // Assuming AdminView3 has jButton2

        dashboardFrame.jButton3.addActionListener(e -> navigateToNotification());
        userFrame.jButton3.addActionListener(e -> navigateToNotification());
        // notificationFrame.jButton3.addActionListener(e -> navigateToNotification()); // Assuming AdminView3 has jButton3
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