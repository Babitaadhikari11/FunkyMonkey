package demogame.controller;

import demogame.dao.NotificationDao;
import demogame.dao.ScoreDao;
import demogame.dao.UserDao;
import demogame.model.Notification;
import demogame.model.UserData;
import demogame.util.DatabaseConnection;
import demogame.view.AdminView1;
import demogame.view.AdminView2;
import demogame.view.AdminView3;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminController implements ScoreDao.ScoreUpdateListener {
    private static final Logger LOGGER = Logger.getLogger(AdminController.class.getName());

    private final UserDao userDao;
    private ScoreDao scoreDao;
    private final NotificationDao notificationDao; // Added for notification handling
    private final AdminView1 dashboardFrame;
    private final AdminView2 userFrame;
    private final AdminView3 notificationFrame;
    private AdminView1 adminView1;
    private AdminView2 adminView2;
    private AdminView3 adminView3;
    private boolean listenerRegistered = false;

    public AdminController() {
        userDao = new UserDao();
        notificationDao = new NotificationDao(); // Initialize NotificationDao
        userFrame = new AdminView2(this);
        try {
            userDao.ensureAdminUser();
            LOGGER.info("Admin user ensured successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error ensuring admin user", e);
            e.printStackTrace();
        }

        // Initialize ScoreDao
        scoreDao = null;
        int scoreDaoAttempts = 0;
        final int MAX_SCORE_DAO_ATTEMPTS = 2;
        while (scoreDao == null && scoreDaoAttempts < MAX_SCORE_DAO_ATTEMPTS) {
            scoreDaoAttempts++;
            try {
                scoreDao = new ScoreDao();
                LOGGER.info("ScoreDao initialized successfully on attempt " + scoreDaoAttempts);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error initializing ScoreDao on attempt " + scoreDaoAttempts, e);
                e.printStackTrace();
            }
        }
        if (scoreDao == null) {
            LOGGER.severe("Failed to initialize ScoreDao after " + MAX_SCORE_DAO_ATTEMPTS + " attempts");
        }

        // Register score update listener
        try {
            if (scoreDao != null) {
                try {
                    LOGGER.info("Attempting to add score update listener, AdminController instance: " + this + ", type: " + this.getClass().getName());
                    scoreDao.addUpdateListener(this);
                    listenerRegistered = true;
                    LOGGER.info("Score update listener added successfully");
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to add score update listener, proceeding with initialization", e);
                    e.printStackTrace();
                    listenerRegistered = false;
                    LOGGER.info("Continuing AdminController initialization despite listener registration failure");
                }
            } else {
                LOGGER.severe("ScoreDao is null, cannot add score update listener, proceeding with initialization");
                listenerRegistered = false;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding score update listener, proceeding with initialization", e);
            e.printStackTrace();
            listenerRegistered = false;
            LOGGER.info("Continuing AdminController initialization despite outer listener error");
        }

        // Initialize adminView1
        adminView1 = null;
        try {
            adminView1 = new AdminView1();
            LOGGER.info("AdminView1 initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing AdminView1", e);
            e.printStackTrace();
            try {
                adminView1 = new AdminView1();
                LOGGER.info("AdminView1 fallback initialization successful");
            } catch (Exception fallbackException) {
                LOGGER.log(Level.SEVERE, "AdminView1 fallback initialization failed", fallbackException);
                fallbackException.printStackTrace();
            }
        }

        dashboardFrame = adminView1;
        notificationFrame = new AdminView3(this); // Pass 'this' instead of null

        // Initialize adminView2
        adminView2 = null;
        try {
            adminView2 = new AdminView2(this);
            LOGGER.info("AdminView2 initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing AdminView2", e);
            e.printStackTrace();
            try {
                adminView2 = new AdminView2(this);
                LOGGER.info("AdminView2 fallback initialization successful");
            } catch (Exception fallbackException) {
                LOGGER.log(Level.SEVERE, "AdminView2 fallback initialization failed", fallbackException);
                fallbackException.printStackTrace();
            }
        }

        // Initialize adminView3
        adminView3 = null;
        try {
            adminView3 = new AdminView3(this); // Pass 'this' instead of null
            LOGGER.info("AdminView3 initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing AdminView3", e);
            e.printStackTrace();
            try {
                adminView3 = new AdminView3(this);
                LOGGER.info("AdminView3 fallback initialization successful");
            } catch (Exception fallbackException) {
                LOGGER.log(Level.SEVERE, "AdminView3 fallback initialization failed", fallbackException);
                fallbackException.printStackTrace();
            }
        }

        // Verify database connection
        try {
            verifyDatabaseConnection();
            LOGGER.info("Database connection verified successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error verifying database connection", e);
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                    "Database connection failed: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            });
        }

        // Setup navigation
        try {
            setupNavigation();
            LOGGER.info("Navigation setup completed successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error setting up navigation", e);
            e.printStackTrace();
        }

        if (adminView1 != null) {
            try {
                adminView1.setAdminController(this);
                adminView1.setVisible(true);
                LOGGER.info("AdminView1 set visible successfully");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error setting AdminView1 controller or visibility", e);
                e.printStackTrace();
            }
        } else {
            LOGGER.severe("AdminView1 is null, cannot set controller or visibility");
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                    "Failed to initialize AdminView1",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            });
        }

        LOGGER.info("AdminController initialized, adminView1: " + (adminView1 != null));
        LOGGER.info("DashboardFrame initialized: " + (dashboardFrame != null));
        LOGGER.info("Listener registered: " + listenerRegistered);
        if (dashboardFrame == null) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                    "Failed to initialize admin dashboard",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            });
        }
        LOGGER.info("AdminController constructor completed");
    }

    // Get all notifications from NotificationDao
    public List<Notification> getNotifications() {
        try {
            return notificationDao.getAllNotifications();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving notifications", e);
            throw new RuntimeException("Failed to retrieve notifications", e);
        }
    }

    // Add a new notification via NotificationDao
    public void addNotification(String message) {
        try {
            boolean success = notificationDao.addNotification(message);
            if (!success) {
                LOGGER.warning("Failed to add notification: " + message);
                throw new RuntimeException("Failed to add notification");
            }
            LOGGER.info("Notification added successfully: " + message);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding notification", e);
            throw new RuntimeException("Failed to add notification", e);
        }
    }

    public JFrame getDashboardFrame() {
        LOGGER.info("Returning dashboard frame: " + (adminView1 != null));
        if (adminView1 != null) {
            adminView1.setVisible(true);
        }
        return adminView1;
    }

    public JFrame getCorrectDashboardFrame() {
        LOGGER.info("Returning correct dashboard frame: " + (dashboardFrame != null));
        if (dashboardFrame != null) {
            dashboardFrame.setVisible(true);
            configureFrame(dashboardFrame, "Admin Dashboard");
        }
        return dashboardFrame;
    }

    private void verifyDatabaseConnection() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                System.out.println("Database connection successful");
                conn.close();
            } else {
                throw new SQLException("Failed to establish database connection");
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            throw new RuntimeException("Database connection failed", e);
        }
    }

    private void loadDashboardData() {
        try {
            int totalUsers = userDao.getTotalUserCount();
            int activeUsers = (scoreDao != null && listenerRegistered) ? scoreDao.getActiveUserCount() : 0;
            String newestUser = userDao.getNewestUser();
            List<ScoreDao.ActiveUserScore> activeUsersList = (scoreDao != null && listenerRegistered) ? scoreDao.getRecentActiveUsers() : new ArrayList<>();

            SwingUtilities.invokeLater(() -> {
                if (dashboardFrame != null) {
                    dashboardFrame.updateDashboardStats(totalUsers, activeUsers, newestUser);
                    dashboardFrame.updateActiveUsersList(activeUsersList);
                    LOGGER.info("Dashboard data updated successfully");
                } else {
                    LOGGER.severe("Cannot update dashboard data: dashboardFrame is null");
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading dashboard data", e);
            SwingUtilities.invokeLater(() -> {
                if (dashboardFrame != null) {
                    dashboardFrame.updateDashboardStats(-1, -1, "Error");
                } else {
                    LOGGER.severe("Cannot update dashboard stats: dashboardFrame is null");
                }
            });
        }
    }

    private void loadAndDisplayUsers() {
        try {
            List<UserData> userList = userDao.getAllUsers();
            if (userFrame != null) {
                userFrame.displayUsers(userList);
                LOGGER.info("User list loaded successfully");
            } else {
                LOGGER.severe("userFrame is null, cannot display users");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load users", e);
            if (userFrame != null) {
                JOptionPane.showMessageDialog(userFrame,
                    "Error loading users: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public UserData getUserDetails(String username) {
        try {
            return userDao.getUserByUsername(username);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting user details for: " + username, e);
            return null;
        }
    }

    public void updateUser(String originalUsername, String newUsername, String newEmail, String newPassword) {
        try {
            UserData originalUser = userDao.getUserByUsername(originalUsername);
            if (originalUser == null) {
                if (userFrame != null) {
                    JOptionPane.showMessageDialog(userFrame,
                        "Original user not found. Cannot update.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
                return;
            }

            UserData updatedUser = new UserData(
                originalUser.getId(),
                newUsername,
                newEmail,
                newPassword,
                originalUser.getRole()
            );

            boolean success = userDao.updateUser(updatedUser);
            if (success) {
                if (userFrame != null) {
                    JOptionPane.showMessageDialog(userFrame, "User updated successfully!");
                    loadAndDisplayUsers();
                    loadDashboardData();
                }
            } else {
                if (userFrame != null) {
                    JOptionPane.showMessageDialog(userFrame,
                        "Failed to update user: " + userDao.getErrorMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating user", e);
            if (userFrame != null) {
                JOptionPane.showMessageDialog(userFrame,
                    "Error updating user: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void addUser(String name, String username, String email, String password) {
        try {
            UserData newUser = new UserData(username, email, password);
            newUser.setRole("player");
            boolean success = userDao.register(newUser);
            if (success) {
                if (userFrame != null) {
                    JOptionPane.showMessageDialog(userFrame, "User added successfully!");
                    loadAndDisplayUsers();
                    loadDashboardData();
                }
            } else {
                if (userFrame != null) {
                    JOptionPane.showMessageDialog(userFrame,
                        "Error adding user: " + userDao.getErrorMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding user", e);
            if (userFrame != null) {
                JOptionPane.showMessageDialog(userFrame,
                    "Error adding user: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void deleteUser(String username) {
        try {
            if (username == null || username.isEmpty()) {
                if (userFrame != null) {
                    JOptionPane.showMessageDialog(userFrame,
                        "Please select a user to delete.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                }
                return;
            }

            if (username.equals("admin")) {
                if (userFrame != null) {
                    JOptionPane.showMessageDialog(userFrame,
                        "Cannot delete admin account.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                }
                return;
            }

            if (userFrame != null) {
                int response = JOptionPane.showConfirmDialog(userFrame,
                    "Are you sure you want to delete user '" + username + "'?\nThis action cannot be undone.",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    boolean success = userDao.deleteUserByUsername(username);
                    if (success) {
                        JOptionPane.showMessageDialog(userFrame, "User deleted successfully!");
                        loadAndDisplayUsers();
                        loadDashboardData();
                    } else {
                        JOptionPane.showMessageDialog(userFrame,
                            "Error deleting user: " + userDao.getErrorMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting user: " + username, e);
            if (userFrame != null) {
                JOptionPane.showMessageDialog(userFrame,
                    "Error deleting user: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void configureFrame(JFrame frame, String title) {
        try {
            if (frame == null) {
                throw new IllegalArgumentException("Frame is null for title: " + title);
            }
            frame.setTitle(title);
            frame.setLocationRelativeTo(null);
            frame.setMinimumSize(new Dimension(1200, 600));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            System.out.println("Frame configured successfully: " + title);
        } catch (Exception e) {
            System.err.println("Error configuring frame '" + title + "': " + e.getMessage());
            throw e;
        }
    }

    private void setupNavigation() {
        if (dashboardFrame != null) {
            dashboardFrame.jButton1.addActionListener(e -> navigateToDashboard(e));
            dashboardFrame.jButton2.addActionListener(e -> navigateToUser(e));
            dashboardFrame.jButton3.addActionListener(e -> navigateToNotification(e));
            dashboardFrame.addRefreshButtonListener(e -> refreshDashboard());
            LOGGER.info("Navigation set up for dashboardFrame");
        } else {
            LOGGER.severe("dashboardFrame is null, cannot set up navigation for dashboard");
        }

        if (userFrame != null) {
            UIManager.put("OptionPane.okButtonText", "OK");
            userFrame.jButton1.addActionListener(e -> navigateToDashboard(e));
            userFrame.jButton2.addActionListener(e -> navigateToUser(e));
            userFrame.jButton3.addActionListener(e -> navigateToNotification(e));
            LOGGER.info("Navigation set up for userFrame");
        } else {
            LOGGER.severe("userFrame is null, cannot set up navigation for user frame");
        }

        if (notificationFrame != null) {
            notificationFrame.jButton1.addActionListener(e -> navigateToDashboard(e));
            notificationFrame.jButton2.addActionListener(e -> navigateToUser(e));
            notificationFrame.jButton3.addActionListener(e -> navigateToNotification(e));
            LOGGER.info("Navigation set up for notificationFrame");
        } else {
            LOGGER.severe("notificationFrame is null, cannot set up navigation for notification frame");
        }
    }

    private void navigateToDashboard(ActionEvent e) {
        showDashboard();
    }

    private void navigateToUser(ActionEvent e) {
        loadAndDisplayUsers();
        showUser();
    }

    private void navigateToNotification(ActionEvent e) {
        showNotification();
    }

    private void showDashboard() {
        LOGGER.info("Showing dashboard");
        hideAllFrames();
        loadDashboardData();
        if (dashboardFrame != null) {
            dashboardFrame.setVisible(true);
        } else {
            LOGGER.severe("dashboardFrame is null, cannot show dashboard");
        }
    }

    private void showUser() {
        hideAllFrames();
        if (userFrame != null) {
            userFrame.setVisible(true);
        } else {
            LOGGER.severe("userFrame is null, cannot show user frame");
        }
    }

    private void showNotification() {
        hideAllFrames();
        if (notificationFrame != null) {
            notificationFrame.setVisible(true);
        } else {
            LOGGER.severe("notificationFrame is null, cannot show notification frame");
        }
    }

    private void hideAllFrames() {
        if (dashboardFrame != null) {
            dashboardFrame.setVisible(false);
        }
        if (userFrame != null) {
            userFrame.setVisible(false);
        }
        if (notificationFrame != null) {
            notificationFrame.setVisible(false);
        }
    }

    public void refreshDashboard() {
        LOGGER.info("Refreshing dashboard");
        loadDashboardData();
    }

    @Override
    public void onScoreUpdated() {
        LOGGER.info("Score updated, triggering dashboard refresh");
        refreshDashboard();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new AdminController();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "A critical error occurred trying to start the application.", e);
                System.exit(1);
            }
        });
    }
}