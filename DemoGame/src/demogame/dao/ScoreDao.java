package demogame.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ScoreDao {
    private static final Logger LOGGER = Logger.getLogger(ScoreDao.class.getName());
    
    private final Connection connection;
    
    // SQL queries
    private static final String INSERT_SCORE = 
        "INSERT INTO game_scores (user_id, score, date_created) VALUES (?, ?, CURRENT_TIMESTAMP)";
    private static final String GET_HIGH_SCORES = 
        "SELECT * FROM game_scores WHERE user_id = ? ORDER BY score DESC LIMIT ?";
    private static final String GET_BEST_SCORE = 
        "SELECT MAX(score) as best_score FROM game_scores WHERE user_id = ?";
    
    public ScoreDao(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Database connection cannot be null");
        }
        this.connection = connection;
    }

    public boolean insertScore(int userId, int score) throws SQLException {
        if (userId <= 0) {
            LOGGER.warning("Invalid user ID provided: " + userId);
            return false;
        }

        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SCORE)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, score);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting score for user " + userId, e);
            throw e;
        }
    }

    public List<Integer> getHighScores(int userId, int limit) throws SQLException {
        List<Integer> scores = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(GET_HIGH_SCORES)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    scores.add(rs.getInt("score"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving high scores for user " + userId, e);
            throw e;
        }
        
        return scores;
    }

    public int getBestScore(int userId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(GET_BEST_SCORE)) {
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("best_score");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving best score for user " + userId, e);
            throw e;
        }
        
        return 0;
    }

    // Method to check if the database connection is valid
    public boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking database connection", e);
            return false;
        }
    }
}