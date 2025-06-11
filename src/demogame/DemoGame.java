package demogame;

import demogame.util.DatabaseConnection;
import demogame.view.LoginView;
import demogame.controller.LoginController;
import javax.swing.SwingUtilities;

public class DemoGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginView loginPanel = new LoginView();
            new LoginController(loginPanel);
            loginPanel.setVisible(true);
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConnection.closeConnection();
        }));
    }
}