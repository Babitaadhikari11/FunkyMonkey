import demogame.model.Notification;
import demogame.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
private static final Logger LOGGER = Logger.getLogger(NotificationDao.class.getName());
    private static final String TABLE_NAME = "notifications";

    // Initialize the notifications table and populate with default notifications
    public NotificationDao() {
        createTable();
        initializeDefaultNotifications();
    }
    // Create the notifications table if it doesn't exist
    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                     "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                     "message TEXT NOT NULL)";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            LOGGER.info("Notifications table created or already exists");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating notifications table", e);
        }
    }
    // Initialize default notifications if the table is empty
    private void initializeDefaultNotifications() {
        List<Notification> notifications = getAllNotifications();
        if (notifications.isEmpty()) {
            String[] defaultMessages = {
                "Tip: Jump early to avoid obstacles!",
                "Collect bananas for extra points!",
                "Watch out for obstacles ahead!",
                "Use forward jump to clear multiple obstacles!"
            };
            for (String message : defaultMessages) {
                if (addNotification(message)) {
                    LOGGER.info("Added default notification: " + message);
                } else {
                    LOGGER.warning("Failed to add default notification: " + message);
                }
            }
    // Insert a new notification into the database
    public boolean addNotification(String message) {
        String sql = "INSERT INTO " + TABLE_NAME + " (message) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, message);
            int rows = pstmt.executeUpdate();
            LOGGER.info("Notification added: " + message);
            return rows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding notification: " + message, e);
            return false;
        }
    }
    // Retrieve all notifications from the database
    public List<Notification> getAllNotifications() {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT id, message FROM " + TABLE_NAME;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                notifications.add(new Notification(rs.getInt("id"), rs.getString("message")));
            }
            LOGGER.info("Retrieved " + notifications.size() + " notifications");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving notifications", e);
        }
        return notifications;
    }