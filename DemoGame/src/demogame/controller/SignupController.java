package demogame.controller;

import demogame.model.SignUpModel;
import demogame.view.SignUpView;
import demogame.view.LoginView;
import demogame.model.LoginModel;
import javax.swing.JOptionPane;
import java.awt.event.*;

public class SignupController {
    private SignUpModel model;
    private SignUpView view;

    public SignupController(SignUpModel model, SignUpView view) {
        this.model = model;
        this.view = view;

        view.signUpButton.addActionListener(e -> handleSignUp());

        // Use MouseListener for the JLabel loginLink
        view.loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Login link clicked in SignUpView");
                handleLoginLink();
            }
        });
    }

    private void handleSignUp() {
        String username = view.usernameField.getText();
        String email = view.emailField.getText();
        String password = new String(view.passwordField.getPassword());

        if (model.register(username, email, password)) {
            view.showSuccess("Registration successful! Please log in.");
            view.dispose();
            navigateToLoginView();
        } else {
            String errorMessage = model.getErrorMessage() != null ? model.getErrorMessage() : "Registration failed.";
            view.showError(errorMessage);
        }
    }

    private void handleLoginLink() {
        System.out.println("Handling login link navigation");
        view.dispose();
        navigateToLoginView();
    }

    private void navigateToLoginView() {
        try {
            System.out.println("Creating new LoginView");
            LoginView loginView = new LoginView();
            System.out.println("Creating new LoginModel");
            LoginModel loginModel = new LoginModel();
            System.out.println("Creating new LoginController");
            new LoginController(loginModel, loginView);
            System.out.println("Setting LoginView visible");
            loginView.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error navigating to LoginView: " + e.getMessage(), "Navigation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}