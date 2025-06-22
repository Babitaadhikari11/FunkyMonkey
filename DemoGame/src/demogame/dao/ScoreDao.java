package demogame.dao;

import demogame.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScoreDao {
    private static final Logger LOGGER = Logger.getLogger(ScoreDao.class.getName());
    
    // SQL Query Constants
    private static final String INSERT_SCORE_QUERY = 
        "INSERT INTO game_scores (user_id, score, date_created) VALUES (?, ?, CURRENT_TIMESTAMP)";
    private static final String CHECK_TODAY_SCORE_QUERY = 
        "SELECT id, score FROM game_scores WHERE user_id = ? AND DATE(date_created) = CURRENT_DATE()";
    private static final String UPDATE_SCORE_QUERY = 
        "UPDATE game_scores SET score = ? WHERE id = ?";
    private static final String GET_BEST_SCORE_QUERY = 
        "SELECT MAX(score) as best_score FROM game_scores WHERE user_id = ?";
    private static final String GET_USER_SCORES_QUERY = 
        "SELECT score, date_created FROM game_scores WHERE user_id = ? ORDER BY date_created DESC";
    private static final String GET_ACTIVE_USERS_QUERY = 
        "SELECT u.username, " +
        "MAX(gs.score) as high_score, " +
        "COUNT(gs.id) as games_played, " +
        "COUNT(DISTINCT DATE(gs.date_created)) as days_played, " +
        "MAX(gs.date_created) as last_played " +
        "FROM users u " +
        "INNER JOIN game_scores gs ON u.id = gs.user_id " +
        "WHERE u.role = 'player' " +
        "AND gs.score > 0 " +
        "AND gs.date_created >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 30 DAY) " +
        "GROUP BY u.id, u.username " +
        "ORDER BY high_score DESC";

    private List<ScoreUpdateListener> updateListeners;
    private String errorMessage;

    public ScoreDao() {
        updateListeners = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                LOGGER.info("ScoreDao database connection established successfully");
                conn.close();
            } else {
                LOGGER.severe("ScoreDao failed to establish database connection");
                errorMessage = "Failed to establish database connection"; // Added for error tracking
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing ScoreDao database connection", e);
            e.printStackTrace();
            errorMessage = "Database connection error: " + e.getMessage(); // Added for error tracking
        }
        LOGGER.info("ScoreDao constructor completed, updateListeners initialized: " + (updateListeners != null));
    }

    public boolean updateOrInsertScore(int userId, int score) {
        System.out.println("=== SCORE DAO: SAVE ATTEMPT ===");
        System.out.println("User ID: " + userId);
        System.out.println("Score: " + score);
        
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            System.out.println("Database connection established");

            String checkQuery = "SELECT id, score FROM game_scores WHERE user_id = ? AND DATE(date_created) = CURRENT_DATE()";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, userId);
                System.out.println("Executing check query: " + checkQuery);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    int existingScore = rs.getInt("score");
                    System.out.println("Found existing score: " + existingScore);
                    
                    if (score > existingScore) {
                        String updateQuery = "UPDATE game_scores SET score = ? WHERE id = ?";
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                            updateStmt.setInt(1, score);
                            updateStmt.setInt(2, rs.getInt("id"));
                            int rows = updateStmt.executeUpdate();
                            System.out.println("Updated score. Rows affected: " + rows);
                            notifyListeners();
                            LOGGER.info("Notified listeners after score update for userId: " + userId);
                            return rows > 0;
                        }
                    } else {
                        System.out.println("New score not higher than existing score");
                        return true;
                    }
                } else {
                    System.out.println("No existing score found for today, inserting new score");
                    String insertQuery = "INSERT INTO game_scores (user_id, score, date_created) VALUES (?, ?, NOW())";
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                        insertStmt.setInt(1, userId);
                        insertStmt.setInt(2, score);
                        int rows = insertStmt.executeUpdate();
                        System.out.println("Inserted new score. Rows affected: " + rows);
                        notifyListeners();
                        LOGGER.info("Notified listeners after score insert for userId: " + userId);
                        return rows > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("=== DATABASE ERROR ===");
            System.err.println("Error message: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            errorMessage = "Database error: " + e.getMessage(); // Added for error tracking
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("Database connection closed");
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                    errorMessage = "Error closing connection: " + e.getMessage(); // Added for error tracking
                }
            }
        }
    }

    public int getActiveUserCount() {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = 
                "SELECT COUNT(DISTINCT user_id) as active_count " +
                "FROM game_scores " +
                "WHERE date_created >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 30 DAY) " +
                "AND score > 0";
            
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                if (rs.next()) {
                    int count = rs.getInt("active_count");
                    LOGGER.info("Active user count: " + count);
                    return count;
                }
            }
        } catch (SQLException e) {
            errorMessage = "Error getting active user count: " + e.getMessage();
            LOGGER.log(Level.SEVERE, errorMessage, e);
        } finally {
            closeConnection(connection);
        }
        return 0;
    }

    public int getUserBestScore(int userId) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(GET_BEST_SCORE_QUERY)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    int bestScore = rs.getInt("best_score");
                    LOGGER.info("Best score for user " + userId + ": " + bestScore);
                    return bestScore;
                }
            }
        } catch (SQLException e) {
            errorMessage = "Error getting best score: " + e.getMessage();
            LOGGER.log(Level.SEVERE, errorMessage, e);
        } finally {
            closeConnection(connection);
        }
        return 0;
    }

    public List<ActiveUserScore> getRecentActiveUsers() {
        List<ActiveUserScore> activeUsers = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            if (connection == null) {
                System.err.println("Database connection is null");
                errorMessage = "Database connection is null"; // Added for error tracking
                return activeUsers;
            }

            try (Statement stmt = connection.createStatement()) {
                System.out.println("Executing query: " + GET_ACTIVE_USERS_QUERY);
                ResultSet rs = stmt.executeQuery(GET_ACTIVE_USERS_QUERY);
                
                while (rs.next()) {
                    String username = rs.getString("username");
                    int highScore = rs.getInt("high_score");
                    int gamesPlayed = rs.getInt("games_played");
                    int daysPlayed = rs.getInt("days_played");
                    Timestamp lastPlayed = rs.getTimestamp("last_played");
                    
                    System.out.println("Found user: " + username + 
                        ", Score: " + highScore + 
                        ", Games: " + gamesPlayed);
                    
                    activeUsers.add(new ActiveUserScore(
                        username, highScore, gamesPlayed, daysPlayed, lastPlayed));
                }
            }
            System.out.println("Total active users found: " + activeUsers.size());
            
        } catch (SQLException e) {
            System.err.println("Error in getRecentActiveUsers: " + e.getMessage());
            e.printStackTrace();
            errorMessage = "Error in getRecentActiveUsers: " + e.getMessage(); // Added for error tracking
        } finally {
            closeConnection(connection);
        }
        return activeUsers;
    }

    public List<UserScore> getUserScores(int userId) {
        List<UserScore> scores = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(GET_USER_SCORES_QUERY)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    scores.add(new UserScore(
                        rs.getInt("score"),
                        rs.getTimestamp("date_created")
                    ));
                }
                LOGGER.info("Retrieved " + scores.size() + " scores for user " + userId);
            }
        } catch (SQLException e) {
            errorMessage = "Error getting user scores: " + e.getMessage();
            LOGGER.log(Level.SEVERE, errorMessage, e);
        } finally {
            closeConnection(connection);
        }
        return scores;
    }

    private void notifyListeners() {
        LOGGER.info("Notifying " + updateListeners.size() + " score update listeners");
        for (ScoreUpdateListener listener : updateListeners) {
            try {
                listener.onScoreUpdated();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error notifying listener", e);
                errorMessage = "Error notifying listener: " + e.getMessage(); // Added for error tracking
            }
        }
    }

    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing connection", e);
                errorMessage = "Error closing connection: " + e.getMessage(); // Added for error tracking
            }
        }
    }

    public void addUpdateListener(ScoreUpdateListener listener) {
        try {
            LOGGER.info("Adding ScoreUpdateListener: " + listener + ", type: " + (listener != null ? listener.getClass().getName() : "null"));
            if (listener != null) {
                if (!(listener instanceof ScoreUpdateListener)) {
                    LOGGER.severe("Listener is not an instance of ScoreDao.ScoreUpdateListener: " + listener.getClass().getName());
                    errorMessage = "Invalid listener type: " + listener.getClass().getName(); // Added for error tracking
                    return;
                }
                if (updateListeners == null) {
                    LOGGER.severe("updateListeners is null, initializing new list");
                    updateListeners = new ArrayList<>();
                }
                if (!updateListeners.contains(listener)) { // Added to prevent duplicate listeners
                    updateListeners.add(listener);
                    LOGGER.info("ScoreUpdateListener added successfully, total listeners: " + updateListeners.size());
                } else {
                    LOGGER.info("ScoreUpdateListener already registered, total listeners: " + updateListeners.size());
                }
            } else {
                LOGGER.warning("Attempted to add null ScoreUpdateListener");
                errorMessage = "Attempted to add null ScoreUpdateListener"; // Added for error tracking
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error in addUpdateListener", e);
            e.printStackTrace();
            errorMessage = "Unexpected error in addUpdateListener: " + e.getMessage(); // Added for error tracking
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // Helper Classes
    public static class ActiveUserScore {
        private final String username;
        private final int highScore;
        private final int gamesPlayed;
        private final int daysPlayed;
        private final Timestamp lastPlayed;

        public ActiveUserScore(String username, int highScore, int gamesPlayed, 
                             int daysPlayed, Timestamp lastPlayed) {
            this.username = username;
            this.highScore = highScore;
            this.gamesPlayed = gamesPlayed;
            this.daysPlayed = daysPlayed;
            this.lastPlayed = lastPlayed;
        }

        public String getUsername() { return username; }
        public int getHighScore() { return highScore; }
        public int getGamesPlayed() { return gamesPlayed; }
        public int getDaysPlayed() { return daysPlayed; }
        public Timestamp getLastPlayed() { return lastPlayed; }
    }

    public static class UserScore {
        private final int score;
        private final Timestamp dateCreated;

        public UserScore(int score, Timestamp dateCreated) {
            this.score = score;
            this.dateCreated = dateCreated;
        }

        public int getScore() { return score; }
        public Timestamp getDateCreated() { return dateCreated; }
    }

    public interface ScoreUpdateListener {
        void onScoreUpdated();
    }
}