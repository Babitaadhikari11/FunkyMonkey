//package demogame.controller;
//
//import demogame.model.SignUpModel;
//import demogame.view.LoginView;
//import demogame.view.SignupView;
//
//import java.awt.event.*;
//
//public class SignupController {
//    private SignUpModel model;
//    private SignupView view;
//
//    public SignupController(SignUpModel model, SignupView view) {
//        this.model = model;
//        this.view = view;
//
//        // Add action listeners
//        view.signUpButton.addActionListener(e -> handleSignUp());
//        view.loginLink.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                view.dispose();
//                new LoginView().setVisible(true);
//            }
//        });
//    }
//
//    private void handleSignUp() {
//        String username = view.usernameField.getText();
//        String email = view.emailField.getText();
//        String password = new String(view.passwordField.getPassword());
//
//        if (model.validateSignUp(username, password, email)) {
//            view.showSuccess("Account created successfully! Please login.");
//            view.dispose();
//            new LoginView().setVisible(true);
//        } else {
//            view.showError("Invalid input. Ensure username is not empty, password is at least 6 characters, and email is valid.");
//        }
//    }
//}
package demogame.controller;

import demogame.model.LoginModel;
import demogame.model.SignUpModel;
import demogame.view.LoginView;
import demogame.view.SignUpView;

import java.awt.event.*;
import java.sql.SQLException;

public class SignupController {
    private SignUpModel model;
    private SignUpView view;

    public SignupController(SignUpModel model, SignUpView view) {
        this.model = model;
        this.view = view;

        view.signUpButton.addActionListener(e -> handleSignUp());
        view.loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                view.dispose();
                LoginView loginView = new LoginView();
                LoginModel loginModel = new LoginModel();
                new LoginController(loginModel, loginView);
                loginView.setVisible(true);
            }
        });
    }

    private void handleSignUp() {
        String username = view.usernameField.getText();
        String email = view.emailField.getText();
        String password = new String(view.passwordField.getPassword());

        try {
            if (model.validateAndSave(username, password, email)) {
                view.showSuccess("Account created successfully! Please login.");
                view.dispose();
                LoginView loginView = new LoginView();
                LoginModel loginModel = new LoginModel();
                new LoginController(loginModel, loginView);
                loginView.setVisible(true);
            } else {
                view.showError("Invalid input. Ensure username is not empty, password is at least 6 characters, and email is valid.");
            }
        } catch (Exception e) {
            if (e.getCause() instanceof SQLException && ((SQLException) e.getCause()).getSQLState().equals("23000")) {
                view.showError("Username or email already exists.");
            } else {
                view.showError("An error occurred during signup.");
                e.printStackTrace();
            }
        }
    }
}