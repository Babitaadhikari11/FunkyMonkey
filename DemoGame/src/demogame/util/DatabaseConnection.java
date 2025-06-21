
package demogame.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    
    private static final String URL = "jdbc:mysql://localhost:3306/funkymonkey?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "funkymonkey123";

    public static Connection getConnection() throws SQLException {
        LOGGER.info("Attempting to create database connection to: " + URL);
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            LOGGER.info("Database connection established successfully");
            return conn;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to database: SQLState=" + e.getSQLState() + ", ErrorCode=" + e.getErrorCode(), e);
            throw e;
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Database connection closed successfully");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing connection: " + e.getMessage(), e);
            }
        }
    }

    public static boolean testConnection() {
        LOGGER.info("Testing database connection");
        try (Connection conn = getConnection()) {
            boolean success = conn != null && !conn.isClosed();
            LOGGER.info("Connection test " + (success ? "successful" : "failed"));
            return success;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Connection test failed: " + e.getMessage(), e);
            return false;
        }
    }
}