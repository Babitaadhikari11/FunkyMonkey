package demogame.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ScoreDao {
    private Connection conn;

    public ScoreDao(Connection conn) {
        this.conn = conn;
    }

    public boolean insertScore(int userId, int score) {
        String sql = "INSERT INTO scores (user_id, score) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, score);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
