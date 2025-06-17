package demogame.dao;

import demogame.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ScoreDao {

    /**
     * The only constructor needed. It's empty.
     */
    public ScoreDao() {
    }

    /**
     * This method is now self-contained and safe. It gets its own connection
     * and closes it automatically.
     * @param userId The ID of the user.
     * @param score The score to save.
     * @return true if successful, false otherwise.
     */
    public boolean insertScore(int userId, int score) {
        String sql = "INSERT INTO scores (user_id, score) VALUES (?, ?)";

        // This method now gets its own connection, making it much safer.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, score);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the number of unique users who have played at least once.
     * This is needed for the "Active Users" panel on your dashboard.
     * @return The count of unique users with scores.
     */
    public int getActiveUserCount() {
        String sql = "SELECT COUNT(DISTINCT user_id) FROM scores";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Failed to get active user count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0; // Return 0 if there's an error
    }
}