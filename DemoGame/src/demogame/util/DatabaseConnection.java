package demogame.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/demogame?useSSL=false";
private static final String USER = "root"; // Your MySQL username
private static final String PASSWORD = "Babita@000"; // Your MySQL password= "Babita@000"; // Replace with your MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}