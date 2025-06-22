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