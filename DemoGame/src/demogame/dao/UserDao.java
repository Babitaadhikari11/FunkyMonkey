package demogame.dao;

import demogame.model.UserData;
import demogame.util.DatabaseConnection;
import java.awt.Image;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.sql.*;


public class UserDao {
    private String errorMessage;

    // Authenticate user
    public UserData authenticate(String username, String password) {
        Connection connection = null;
        this.errorMessage = null;

        if (username == null || username.trim().isEmpty()) {
            this.errorMessage = "Username cannot be empty.";
            return null;
        }
        if (password == null || password.trim().isEmpty()) {
            this.errorMessage = "Password cannot be empty.";
            return null;
        }

        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username.trim());
                stmt.setString(2, password);
                
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {

                    return new UserData(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password")
                    );
                } else {
                    this.errorMessage = "Invalid username or password.";
                    return null;
                }
            }
        } catch (SQLException e) {
            handleSQLException("Authentication failed", e);

            return null;
        } finally {
            closeConnection(connection);
        }
    }


    // Register new user

    public boolean register(UserData user) {
        Connection connection = null;
        this.errorMessage = null;

        if (!validateUserData(user)) {
            return false;
        }

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            if (isUsernameTaken(user.getUsername(), connection)) {
                this.errorMessage = "Username already exists.";
                return false;
            }

            if (isEmailTaken(user.getEmail(), connection)) {
                this.errorMessage = "Email already exists.";
                return false;
            }

            String query = "INSERT INTO users (username, email, password, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())";
            
            try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, user.getUsername().trim());
                stmt.setString(2, user.getEmail().trim());
                stmt.setString(3, user.getPassword());

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            user.setId(generatedKeys.getInt(1));
                            connection.commit();
                            return true;
                        }
                    }
                }
                connection.rollback();
                this.errorMessage = "Failed to create user account.";
                return false;
            }
        } catch (SQLException e) {
            rollbackTransaction(connection);
            handleSQLException("Registration failed", e);
            return false;
        } finally {
            closeConnection(connection);
        }
    }

    // Update username
    public boolean updateUsername(int userId, String newUsername) {
        Connection connection = null;
        this.errorMessage = null;

        if (newUsername == null || newUsername.trim().isEmpty()) {
            this.errorMessage = "Username cannot be empty.";
            return false;
        }

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            if (isUsernameTaken(newUsername, connection) && !isCurrentUsername(userId, newUsername, connection)) {
                this.errorMessage = "Username already exists.";
                return false;
            }

            String oldUsername = getCurrentUsername(userId, connection);
            
            String updateSql = "UPDATE users SET username = ?, updated_at = NOW() WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateSql)) {
                stmt.setString(1, newUsername.trim());
                stmt.setInt(2, userId);
                int result = stmt.executeUpdate();

                if (result > 0) {
                    recordUsernameChange(userId, oldUsername, newUsername, connection);
                    connection.commit();
                    return true;
                } else {
                    connection.rollback();
                    this.errorMessage = "Failed to update username.";
                    return false;
                }
            }
        } catch (SQLException e) {
            rollbackTransaction(connection);
            handleSQLException("Username update failed", e);

            return false;
        } finally {
            closeConnection(connection);
        }
    }

    // Update profile picture
    public boolean updateProfilePicture(int userId, File imageFile) {
        Connection connection = null;
        this.errorMessage = null;

        try {
            connection = DatabaseConnection.getConnection();
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Image image = ImageIO.read(imageFile);
            if (image == null) {
                this.errorMessage = "Invalid image file.";
                return false;
            }

            java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(
                image.getWidth(null), image.getHeight(null), 
                java.awt.image.BufferedImage.TYPE_INT_RGB);
            
            bufferedImage.getGraphics().drawImage(image, 0, 0, null);
            ImageIO.write(bufferedImage, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();

            String sql = "UPDATE users SET profile_picture = ?, updated_at = NOW() WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setBytes(1, imageBytes);
                stmt.setInt(2, userId);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            this.errorMessage = "Failed to update profile picture: " + e.getMessage();
            return false;
        } finally {
            closeConnection(connection);
        }
    }

    // Get profile picture
    public Image getProfilePicture(int userId) {
        Connection connection = null;
        this.errorMessage = null;

        try {
            connection = DatabaseConnection.getConnection();
            String sql = "SELECT profile_picture FROM users WHERE id = ?";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    byte[] imageBytes = rs.getBytes("profile_picture");
                    if (imageBytes != null) {
                        return ImageIO.read(new ByteArrayInputStream(imageBytes));
                    }
                }
                return null;
            }
        } catch (Exception e) {
            this.errorMessage = "Failed to get profile picture: " + e.getMessage();
            return null;
        } finally {
            closeConnection(connection);
        }
    }

    // Delete user
    public boolean deleteUser(int userId) {
        Connection connection = null;
        this.errorMessage = null;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            // Delete related records first
            deleteUserRelatedRecords(userId, connection);

            String sql = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                int result = stmt.executeUpdate();
                connection.commit();
                return result > 0;
            }
        } catch (SQLException e) {
            rollbackTransaction(connection);
            handleSQLException("Failed to delete user", e);

            return false;
        } finally {
            closeConnection(connection);
        }
    }

    // Get user change history
    public ResultSet getUserChangeHistory(int userId) {
        Connection connection = null;
        this.errorMessage = null;

        try {
            connection = DatabaseConnection.getConnection();
            String sql = "SELECT change_type, old_value, new_value, changed_at " +
                        "FROM user_changes " +
                        "WHERE user_id = ? " +
                        "ORDER BY changed_at DESC";
            
            PreparedStatement stmt = connection.prepareStatement(sql,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
            stmt.setInt(1, userId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            handleSQLException("Failed to get change history", e);
            return null;
        }
    }

    // Helper methods
    private boolean validateUserData(UserData user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            this.errorMessage = "Username cannot be empty.";
            return false;
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            this.errorMessage = "Email cannot be empty.";
            return false;
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            this.errorMessage = "Password cannot be empty.";
            return false;
        }
        if (!isValidEmail(user.getEmail())) {
            this.errorMessage = "Invalid email format.";
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private boolean isUsernameTaken(String username, Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private boolean isEmailTaken(String email, Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private boolean isCurrentUsername(int userId, String username, Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ? AND username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private String getCurrentUsername(int userId, Connection connection) throws SQLException {
        String sql = "SELECT username FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getString("username") : null;
        }
    }

    private void recordUsernameChange(int userId, String oldUsername, String newUsername, Connection connection) throws SQLException {
        String sql = "INSERT INTO user_changes (user_id, change_type, old_value, new_value) VALUES (?, 'USERNAME', ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, oldUsername);
            stmt.setString(3, newUsername);
            stmt.executeUpdate();
        }
    }

    private void deleteUserRelatedRecords(int userId, Connection connection) throws SQLException {
        String[] deleteSqls = {
            "DELETE FROM user_changes WHERE user_id = ?",
            "DELETE FROM scores WHERE user_id = ?"
        };
        
        for (String sql : deleteSqls) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }
        }
    }

    private void rollbackTransaction(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleSQLException(String message, SQLException e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
        this.errorMessage = "Database error: " + e.getMessage();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}