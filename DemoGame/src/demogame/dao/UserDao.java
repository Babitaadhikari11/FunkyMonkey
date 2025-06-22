package demogame.dao;

import demogame.model.UserData;
import demogame.util.DatabaseConnection;
import java.awt.Image;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.mindrot.jbcrypt.BCrypt; // Ensure jbcrypt-0.4.jar is in classpath

public class UserDao {
    private String errorMessage;
    private static final Logger LOGGER = Logger.getLogger(UserDao.class.getName());
    private static final String GET_USERNAME_QUERY = "SELECT username FROM users WHERE id = ?";

    // Authenticate user with admin check
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

        // Check for admin credentials first
        if (username.equals("admin") && password.equals("admin123")) {
            return new UserData(-1, "admin", "admin@system.com", "admin123", "admin");
        }

        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT id, username, email, password, role FROM users WHERE username = ? AND password = ?";
            
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username.trim());
                stmt.setString(2, password);
                
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new UserData(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
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

    public List<UserData> getAllUsers() {
        List<UserData> users = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM users";
            
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    users.add(new UserData(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password")
                    ));
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error getting all users", e);
        } finally {
            closeConnection(connection);
        }
        return users;
    }

    public void ensureAdminUser() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ? AND role = 'admin'";
            try (PreparedStatement stmt = connection.prepareStatement(checkQuery)) {
                stmt.setString(1, "admin");
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    UserData admin = new UserData(0, "admin", "admin@example.com", "admin123", "admin");
                    register(admin);
                    LOGGER.info("Admin user created in database");
                }
            }
        } catch (SQLException e) {
            errorMessage = "Error ensuring admin user: " + e.getMessage();
            LOGGER.log(Level.SEVERE, errorMessage, e);
        }
    }

    public int getTotalUserCount() {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT COUNT(*) as count FROM users";
            
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error getting total user count", e);
        } finally {
            closeConnection(connection);
        }
        return 0;
    }

    public String getNewestUser() {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT username FROM users ORDER BY created_at DESC LIMIT 1";
            
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error getting newest user", e);
        } finally {
            closeConnection(connection);
        }
        return "None";
    }

    public boolean updateUser(UserData user) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            // Get current user data for comparison
            UserData currentUser = getUserById(user.getId());
            if (currentUser == null) {
                errorMessage = "User not found.";
                return false;
            }

            // Check if username or email is taken by another user
            if (!currentUser.getUsername().equals(user.getUsername()) && isUsernameTaken(user.getUsername(), connection)) {
                errorMessage = "Username already exists.";
                return false;
            }
            if (!currentUser.getEmail().equals(user.getEmail()) && isEmailTaken(user.getEmail(), connection)) {
                errorMessage = "Email already exists.";
                return false;
            }

            String query = "UPDATE users SET username = ?, email = ?, password = ?, updated_at = NOW() WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, user.getPassword());
                stmt.setInt(4, user.getId());
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    // Record username change if different
                    if (!currentUser.getUsername().equals(user.getUsername())) {
                        recordUsernameChange(user.getId(), currentUser.getUsername(), user.getUsername(), connection);
                    }
                    // Record email change if different
                    if (!currentUser.getEmail().equals(user.getEmail())) {
                        String sql = "INSERT INTO user_changes (user_id, change_type, old_value, new_value) VALUES (?, 'EMAIL', ?, ?)";
                        try (PreparedStatement emailStmt = connection.prepareStatement(sql)) {
                            emailStmt.setInt(1, user.getId());
                            emailStmt.setString(2, currentUser.getEmail());
                            emailStmt.setString(3, user.getEmail());
                            emailStmt.executeUpdate();
                        }
                    }
                    connection.commit();
                    return true;
                } else {
                    connection.rollback();
                    errorMessage = "Failed to update user.";
                    return false;
                }
            }
        } catch (SQLException e) {
            rollbackTransaction(connection);
            handleSQLException("Error updating user", e);
            return false;
        } finally {
            closeConnection(connection);
        }
    }

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

            String query = "INSERT INTO users (username, email, password, role, created_at, updated_at) " +
                          "VALUES (?, ?, ?, ?, NOW(), NOW())";
            
            try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, user.getUsername().trim());
                stmt.setString(2, user.getEmail().trim());
                stmt.setString(3, user.getPassword());
                stmt.setString(4, user.getRole() != null ? user.getRole() : "player");

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

    public UserData getUserByUsername(String username) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM users WHERE username = ?";
            
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new UserData(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error getting user by username", e);
        } finally {
            closeConnection(connection);
        }
        return null;
    }

    public UserData getUserById(int userId) throws SQLException {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT id, username, email, password, role FROM users WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    UserData user = new UserData(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                    );
                    LOGGER.info("Retrieved UserData for userId: " + userId);
                    return user;
                } else {
                    LOGGER.warning("No user found for userId: " + userId);
                    return null;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error retrieving UserData for userId: " + userId, e);
            throw e;
        } finally {
            closeConnection(connection);
        }
    }

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

    public boolean deleteUser(int userId) {
        Connection connection = null;
        this.errorMessage = null;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            // Delete related records first
            try {
                // Delete from game_scores if exists
                try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM game_scores WHERE user_id = ?")) {
                    stmt.setInt(1, userId);
                    stmt.executeUpdate();
                }

                // Delete from user_changes if exists
                try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM user_changes WHERE user_id = ?")) {
                    stmt.setInt(1, userId);
                    stmt.executeUpdate();
                }

                // Finally delete the user
                String sql = "DELETE FROM users WHERE id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, userId);
                    int result = stmt.executeUpdate();
                    if (result > 0) {
                        connection.commit();
                        return true;
                    } else {
                        connection.rollback();
                        this.errorMessage = "User not found.";
                        return false;
                    }
                }
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            rollbackTransaction(connection);
            handleSQLException("Failed to delete user", e);
            return false;
        } finally {
            closeConnection(connection);
        }
    }

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

    public boolean deleteUserByUsername(String username) {
        if (username == null || username.isEmpty()) {
            this.errorMessage = "Username cannot be empty.";
            return false;
        }

        if (username.equals("admin")) {
            this.errorMessage = "Cannot delete admin account.";
            return false;
        }

        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            // First check if user exists
            String checkQuery = "SELECT id FROM users WHERE username = ? AND role != 'admin'";
            int userId;
            
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                
                if (!rs.next()) {
                    this.errorMessage = "User not found.";
                    return false;
                }
                userId = rs.getInt("id");
            }

            // Delete related records first
            try {
                // Delete from game_scores if exists
                try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM game_scores WHERE user_id = ?")) {
                    stmt.setInt(1, userId);
                    stmt.executeUpdate();
                }

                // Delete from user_changes if exists
                try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM user_changes WHERE user_id = ?")) {
                    stmt.setInt(1, userId);
                    stmt.executeUpdate();
                }

                // Finally delete the user
                try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM users WHERE username = ? AND role != 'admin'")) {
                    stmt.setString(1, username);
                    int result = stmt.executeUpdate();
                    
                    if (result > 0) {
                        connection.commit();
                        return true;
                    } else {
                        connection.rollback();
                        this.errorMessage = "Failed to delete user.";
                        return false;
                    }
                }
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            this.errorMessage = "Database error: " + e.getMessage();
            System.err.println("Error deleting user: " + username + " - " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public String getUsernameById(int userId) throws SQLException {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            if (connection == null) {
                LOGGER.severe("Failed to establish database connection for userId: " + userId);
                throw new SQLException("Database connection is null");
            }
            try (PreparedStatement stmt = connection.prepareStatement(GET_USERNAME_QUERY)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String username = rs.getString("username");
                    LOGGER.info("Retrieved username: " + username + " for userId: " + userId);
                    return username;
                } else {
                    LOGGER.warning("No user found for userId: " + userId);
                    return null;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error retrieving username for userId: " + userId, e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error retrieving username for userId: " + userId, e);
            throw new SQLException("Unexpected error retrieving username", e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    public boolean updatePassword(String username, String newPassword) {
        Connection connection = null;
        this.errorMessage = null;

        if (username == null || username.trim().isEmpty()) {
            this.errorMessage = "Username cannot be empty.";
            return false;
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            this.errorMessage = "New password cannot be empty.";
            return false;
        }
        if (username.equals("admin")) {
            this.errorMessage = "Cannot reset password for admin account.";
            return false;
        }

        try {
            connection = DatabaseConnection.getConnection();
            String sql = "UPDATE users SET password = ?, updated_at = NOW() WHERE username = ? AND role != 'admin'";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, newPassword);
                stmt.setString(2, username.trim());
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    return true;
                } else {
                    this.errorMessage = "Username not found.";
                    return false;
                }
            }
        } catch (SQLException e) {
            handleSQLException("Failed to update password for username: " + username, e);
            return false;
        } finally {
            closeConnection(connection);
        }
    }

    public String getEmailByUsername(String username) {
        Connection connection = null;
        this.errorMessage = null;

        try {
            connection = DatabaseConnection.getConnection();
            String sql = "SELECT email FROM users WHERE username = ?";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return rs.getString("email");
                } else {
                    this.errorMessage = "Username not found.";
                    return null;
                }
            }
        } catch (SQLException e) {
            handleSQLException("Failed to get email for username: " + username, e);
            return null;
        } finally {
            closeConnection(connection);
        }
    }

    public boolean updatePasswordByEmail(String email, String newPassword) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            String sql = "UPDATE users SET password = ?, updated_at = NOW() WHERE email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                // Hash the password with BCrypt
                String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                stmt.setString(1, hashedPassword);
                stmt.setString(2, email);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    LOGGER.info("Password updated successfully for email: " + email);
                    return true;
                } else {
                    this.errorMessage = "Email not found.";
                    LOGGER.warning("No user found with email: " + email);
                    return false;
                }
            }
        } catch (SQLException e) {
            this.errorMessage = "Database error updating password for email: " + email + " - " + e.getMessage();
            handleSQLException("Failed to update password for email: " + email, e);
            return false;
        } finally {
            closeConnection(connection);
        }
    }

    public boolean register(String email, String password, String role) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            // Validate input
            if (email == null || email.trim().isEmpty()) {
                this.errorMessage = "Email cannot be empty.";
                return false;
            }
            if (password == null || password.trim().isEmpty()) {
                this.errorMessage = "Password cannot be empty.";
                return false;
            }
            if (!isValidEmail(email)) {
                this.errorMessage = "Invalid email format.";
                return false;
            }

            // Check if email is already taken
            if (isEmailTaken(email, connection)) {
                this.errorMessage = "Email already exists.";
                return false;
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            String sql = "INSERT INTO users (email, password, role, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW()) " +
                        "ON DUPLICATE KEY UPDATE password = ?, role = ?, updated_at = NOW()";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, email.trim());
                stmt.setString(2, hashedPassword);
                stmt.setString(3, role != null ? role : "player");
                stmt.setString(4, hashedPassword);
                stmt.setString(5, role != null ? role : "player");
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    connection.commit();
                    LOGGER.info("User registered or updated successfully for email: " + email);
                    return true;
                } else {
                    connection.rollback();
                    this.errorMessage = "Failed to register or update user.";
                    return false;
                }
            }
        } catch (SQLException e) {
            rollbackTransaction(connection);
            this.errorMessage = "Database error during registration for email: " + email + " - " + e.getMessage();
            handleSQLException("Registration failed for email: " + email, e);
            return false;
        } finally {
            closeConnection(connection);
        }
    }

    // Existing helper methods
    private void handleSQLException(String message, SQLException e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
        this.errorMessage = "Database error: " + e.getMessage();
    }

    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
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

    private void rollbackTransaction(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                System.err.println("Error rolling back transaction: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

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

    public String getErrorMessage() {
        return errorMessage;
    }
}