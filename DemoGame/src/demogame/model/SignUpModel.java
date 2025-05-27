package demogame.model;

import demogame.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUpModel {
    private String username;
    private String password;
    private String email;

    public boolean validateAndSave(String username, String password, String email) {
        // Basic input validation
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        if (password == null || password.length() < 6) {
            return false;
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return false;
        }

        // Save to database (plain text password)
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO users (username, email, password) VALUES (?, ?, ?)")) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password); // Store plain text password
            stmt.executeUpdate();
            this.username = username;
            this.password = password;
            this.email = email;
            return true;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) { // Duplicate entry error
                return false;
            }
            e.printStackTrace();
            return false;
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}