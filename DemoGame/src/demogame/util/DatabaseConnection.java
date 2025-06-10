package demogame.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            // Added allowPublicKeyRetrieval=true to fix "Public Key Retrieval is not allowed" error
            String url = "jdbc:mysql://localhost:3306/funkymonkey?useSSL=false&allowPublicKeyRetrieval=true";
            String user = "root";

            String password = "funkymonkey123"; // Your MySQL root password

            try {
                // No need for Class.forName("com.mysql.cj.jdbc.Driver");
                // The MySQL Connector/J driver auto-registers via JDBC 4.0 SPI
                connection = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                /*System.err.println("SQL Connection Error: " + e.getMessage());
                System.err.println("SQL State: " + e.getSQLState());
                System.err.println("Error Code: " + e.getErrorCode());*/
                throw e;
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
//                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}