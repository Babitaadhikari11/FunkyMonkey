package demogame;

import demogame.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MinimalSignUpTest {
    public static void main(String[] args) {
        String username = "testuser5";
        String email = "testuser5@example.com";
        String password = "password123";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO users (username, email, password) VALUES (?, ?, ?)")) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("User inserted successfully: " + username);
            } else {
                System.out.println("No rows affected. Insertion failed.");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
        }
    }
}