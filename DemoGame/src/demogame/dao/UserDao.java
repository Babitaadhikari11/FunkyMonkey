package demogame.dao;

import demogame.model.UserData;
import demogame.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    private Connection connection;
    private String errorMessage;

    public UserDao() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Failed to initialize database connection: " + e.getMessage());
            throw new RuntimeException("Database connection error", e);
        }
    }

    // Authenticate a user (used for login)
    public UserData authenticate(String username, String password) {
        this.errorMessage = null;

        if (username == null || username.trim().isEmpty()) {
            this.errorMessage = "Username cannot be empty.";
            return null;
        }
        if (password == null || password.trim().isEmpty()) {
            this.errorMessage = "Password cannot be empty.";
            return null;
        }

        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if (rs.getObject("id") == null) {
                    this.errorMessage = "User ID is null or inaccessible.";
                    return null;
                }
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
        } catch (SQLException e) {
            System.err.println("Authentication failed: " + e.getMessage());
            e.printStackTrace();
            this.errorMessage = "Database error: " + e.getMessage();
            return null;
        }
    }

    // Register a new user (used for signup)
    public boolean register(UserData user) {
        this.errorMessage = null;

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

        String query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Registration failed: " + e.getMessage());
            e.printStackTrace();
            if (e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("username")) {
                    this.errorMessage = "Username already exists.";
                } else if (e.getMessage().contains("email")) {
                    this.errorMessage = "Email already exists.";
                } else {
                    this.errorMessage = "Duplicate entry error: " + e.getMessage();
                }
            } else {
                this.errorMessage = "Database error: " + e.getMessage();
            }
            return false;
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}