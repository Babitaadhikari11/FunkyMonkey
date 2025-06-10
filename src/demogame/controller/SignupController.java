package demogame.controller;

import demogame.dao.UserDao;
import demogame.model.UserData;
import demogame.view.LoginView;
import demogame.view.SignUpView;
import javax.swing.JOptionPane;
import java.awt.event.*;

public class SignupController {
    private UserDao userDAO;
    private SignUpView view;

    public SignupController(SignUpView view) {
        this.view = view;
        this.userDAO = new UserDao();

        view.getSignUpButton().addActionListener(e -> handleSignUp());

        view.getLoginLink().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleLoginLink();
            }
        });
    }

    private void handleSignUp() {
        String username = view.getUsername();
        String email = view.getEmail();
        String password = view.getPassword();
       

        // Validate inputs
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            view.showError("All fields are required.");
            return;
        }

        

        UserData user = new UserData(username, email, password);
        if (userDAO.register(user)) {
            view.showSuccess("Registration successful! Please log in.");
            view.setVisible(false);
            navigateToLoginPanel();
        } else {
            String errorMessage = userDAO.getErrorMessage() != null ? userDAO.getErrorMessage() : "Registration failed.";
            view.showError(errorMessage);
        }
    }

    private void handleLoginLink() {
        view.setVisible(false);
        navigateToLoginPanel();
    }

    private void navigateToLoginPanel() {
        LoginView loginPanel = new LoginView();
        new LoginController(loginPanel);
        loginPanel.setVisible(true);
    }
}