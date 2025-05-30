package demogame;

import demogame.model.LoginModel;
import demogame.view.LoginView;
import demogame.controller.LoginController;

import javax.swing.*;

public class DemoGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            LoginModel loginModel = new LoginModel();
            new LoginController(loginModel, loginView);
            loginView.setVisible(true);
        });
    }
}