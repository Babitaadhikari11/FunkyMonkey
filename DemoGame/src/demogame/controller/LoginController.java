package demogame.controller;

import demogame.model.LoginModel;
import demogame.view.LoginView;

import java.awt.event.*;

public class LoginController {
    private LoginModel model;
    private LoginView view;

    public LoginController(LoginModel model, LoginView view) {
        this.model = model;
        this.view = view;

        // Add action listener for login button
        view.loginButton.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = view.usernameField.getText();
        String password = new String(view.passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            view.showError("Username and password cannot be empty.");
            return;
        }

        if (model.validateLogin(username, password)) {
            view.showSuccess("Login successful!");
            // Optionally, redirect to another view (e.g., game dashboard)
            // For now, just close the login window
            view.dispose();
        } else {
            view.showError("Invalid username or password.");
        }
    }
}