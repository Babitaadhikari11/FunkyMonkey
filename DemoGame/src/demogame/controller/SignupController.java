package demogame.controller;

import demogame.dao.UserDao;
import demogame.model.UserData;
import demogame.view.LoginView;
import demogame.view.SignUpView;
import java.awt.event.*;
import javax.swing.SwingUtilities;

public class SignupController {
    private UserDao userDAO;
    private SignUpView view;

    
    public SignupController(SignUpView view) {
        this.view = view;
        this.userDAO = new UserDao();
        initializeListeners();
    }

    // listeners for signup button, login link window close
    private void initializeListeners() {
        view.getSignUpButton().addActionListener(e -> handleSignUp());
        view.getLoginLink().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleLoginLink();
            }
        });

        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanup();
            }
        });
    }

    // handles signup process validates input and registers user
    private void handleSignUp() {
        view.getSignUpButton().setEnabled(false);
        
        try {
            String username = view.getUsername().trim();
            String email = view.getEmail().trim();
            String password = view.getPassword();
            String confirmPassword = view.getConfirmPassword();

            // validate inputs
            if (!validateInputs(username, email, password, confirmPassword)) {
                view.getSignUpButton().setEnabled(true);
                return;
            }

            UserData user = new UserData(username, email, password);
            
         
            new Thread(() -> {
                try {
                    final boolean success = userDAO.register(user);
                    SwingUtilities.invokeLater(() -> {
                        if (success) {
                            handleSuccessfulRegistration();
                        } else {
                            handleFailedRegistration();
                        }
                        view.getSignUpButton().setEnabled(true);
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        e.printStackTrace();
                        view.showError("An unexpected error occurred during registration.");
                        view.getSignUpButton().setEnabled(true);
                    });
                }
            }).start();
            
        } catch (Exception e) {
            e.printStackTrace();
            view.showError("An unexpected error occurred. Please try again.");
            view.getSignUpButton().setEnabled(true);
        }
    }

    // validates username, email, and password requirements
    private boolean validateInputs(String username, String email, String password, String confirmPassword) {
        // check for empty fields
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            view.showError("All fields are required.");
            return false;
        }

        if (username.length() < 3 || username.length() > 50) {
            view.showError("Username must be between 3 and 50 characters.");
            return false;
        }

        // restrict username to alphanumeric and underscores
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            view.showError("Username can only contain letters, numbers, and underscores.");
            return false;
        }

        if (!isValidEmail(email)) {
            view.showError("Please enter a valid email address.");
            return false;
        }

        if (email.length() > 100) {
            view.showError("Email address is too long.");
            return false;
        }

        if (password.length() < 6) {
            view.showError("Password must be at least 6 characters long.");
            return false;
        }

        if (password.length() > 50) {
            view.showError("Password is too long.");
            return false;
        }

        //  password complexity
        if (!password.matches(".*[A-Z].*")) {
            view.showError("Password must contain at least one uppercase letter.");
            return false;
        }

        if (!password.matches(".*[a-z].*")) {
            view.showError("Password must contain at least one lowercase letter.");
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            view.showError("Password must contain at least one number.");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            view.showError("Passwords do not match.");
            return false;
        }

        return true;
    }

    // checks email format using regex
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    // navigates to login
    private void handleSuccessfulRegistration() {
        try {
            view.showSuccess("Registration successful! Please log in.");
            Thread.sleep(1500); // brief delay for success message
            navigateToLoginPanel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
// error show
    private void handleFailedRegistration() {
        String errorMessage = userDAO.getErrorMessage();
        if (errorMessage == null) {
            errorMessage = "Registration failed. Please try again.";
        }
        view.showError(errorMessage);
    }

    // navigates to login screen 
    private void handleLoginLink() {
        cleanup();
        navigateToLoginPanel();
    }

 
    private void navigateToLoginPanel() {
        SwingUtilities.invokeLater(() -> {
            view.setVisible(false);
            LoginView loginPanel = new LoginView();
            new LoginController(loginPanel);
            loginPanel.setVisible(true);
            view.dispose();
        });
    }

    
    private void cleanup() {
        if (view != null) {
            view.dispose();
        }
    }
}