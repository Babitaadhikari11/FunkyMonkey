package demogame.dao;

import demogame.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class LoginDAO {
    /**
     * Validates user credentials by checking the username and password against the users table.
     * @param username The username to validate.
     * @param password The password to validate.
     * @return The user's ID if credentials are valid, -1 otherwise.
     * @throws SQLException If a database error occurs.
     */
    public int validateCredentials(String username, String password) throws SQLException {
        String query = "SELECT id, password FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (storedPassword.equals(password)) { // Note: In a real app, use password hashing
                    return rs.getInt("id");
                }
            }
            return -1; // Return -1 if validation fails
        }
    }

    /**
     * Logs a successful login attempt by updating the last_login and login_count in the users table.
     * @param userId The ID of the user who logged in.
     * @throws SQLException If a database error occurs.
     */
    public void logLoginAttempt(int userId) throws SQLException {
        String query = "UPDATE users SET last_login = ?, login_count = login_count + 1 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, userId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected != 1) {
                throw new SQLException("Failed to log login attempt for user ID: " + userId);
            }
        }
    }
}
