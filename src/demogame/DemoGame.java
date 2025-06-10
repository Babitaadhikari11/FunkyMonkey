package demogame;

<<<<<<< HEAD
import demogame.model.LoginModel;
import demogame.view.LoginView;
import demogame.controller.LoginController;

import javax.swing.*;
=======
import demogame.controller.LoginController;
import demogame.util.DatabaseConnection;
import demogame.view.LoginView;
import javax.swing.SwingUtilities;
>>>>>>> nirajan

public class DemoGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
<<<<<<< HEAD
            LoginView loginView = new LoginView();
            LoginModel loginModel = new LoginModel();
            new LoginController(loginModel, loginView);
            loginView.setVisible(true);
        });
=======
            LoginView loginPanel = new LoginView();
            new LoginController(loginPanel);
            loginPanel.setVisible(true);
        });
        

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConnection.closeConnection();
        }));
        
>>>>>>> nirajan
    }
}