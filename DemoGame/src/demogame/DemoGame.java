
package demogame;

import demogame.controller.LoginController;
import demogame.util.DatabaseConnection;
import demogame.view.LoginView;
import javax.swing.SwingUtilities;
import java.sql.Connection;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DemoGame {
    private static final Logger LOGGER = Logger.getLogger(DemoGame.class.getName());

    public static void main(String[] args) {
        // Test database connection at startup
        // testDatabaseConnection();

        SwingUtilities.invokeLater(() -> {
            LoginView loginPanel = new LoginView();
            new LoginController(loginPanel);
            loginPanel.setVisible(true);
        });

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Application shutting down, cleaning up resources...");
            cleanup();
        }));
    }

  
    

    private static void cleanup() {
        try {
            // Since we're not keeping static connections anymore,
            // we just log the cleanup
            LOGGER.info("Application cleanup completed");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during cleanup", e);
        }
    }
}