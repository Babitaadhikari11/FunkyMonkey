package demogame.controller;

import demogame.dao.UserDao;
import demogame.model.UserData;
import demogame.view.LoginView;
import demogame.view.MenuView;
import demogame.view.SignUpView;

import javax.swing.JOptionPane; // Added for pop-up messages

import demogame.view.LoadingView;

import javax.swing.SwingUtilities;

public class LoginController {
    private UserDao userDAO;
    private LoginView view;

    private int loggedInUserId = -1;
    private String loggedInUsername;

    public LoginController(LoginView view) {
        this.view = view;
        this.userDAO = new UserDao();
        initializeListeners();
    }

    private void initializeListeners() {
        view.getLoginButton().addActionListener(e -> handleLogin());

        view.getCreateAccountLink().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showSignUpView();
            }
        });
    }

    private void handleLogin() {
        String username = view.getUsername();
        String password = view.getPassword();

        if (validateInput(username, password)) {
            authenticateUser(username, password);
        }
    }

    private boolean validateInput(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            view.showError("Username and password cannot be empty.");
            return false;
        }
        return true;
    }


    private void authenticateUser(String username, String password) {
        try {
            UserData user = userDAO.authenticate(username, password);
            if (user != null) {
                handleSuccessfulLogin(user);
            } else {
                view.showError(userDAO.getErrorMessage());
            }
        } catch (Exception e) {
            view.showError("Authentication error: " + e.getMessage());
        }
    }

    private void handleSuccessfulLogin(UserData user) {
        this.loggedInUserId = user.getId();
        this.loggedInUsername = user.getUsername();
        view.showSuccess("Login successful! Welcome, " + loggedInUsername + "!");
        
        // Start transition to menu
        SwingUtilities.invokeLater(() -> {
            view.setVisible(false);
            showMenuView(user);
        });
    }

    private void showSignUpView() {
        SwingUtilities.invokeLater(() -> {
            view.setVisible(false);
            SignUpView signUpPanel = new SignUpView();
            new SignupController(signUpPanel);
            signUpPanel.setVisible(true);
        });
    }

    private void showMenuView(UserData user) {
        SwingUtilities.invokeLater(() -> {
            MenuView menuView = new MenuView(user.getUsername());
            new MenuController(menuView, user);
            menuView.setVisible(true);
            view.dispose(); // Clean up login view
        });

    }

    public void startGame() {
        SwingUtilities.invokeLater(() -> {
            // Show loading screen first
            LoadingView loadingView = new LoadingView();
            new LoadingController(loadingView, loggedInUserId);
            loadingView.setVisible(true);
            
            // Clean up login view if it's still showing
            if (view != null) {
                view.dispose();
            }
        });
    }
    // Getters and utility methods
    public int getLoggedInUserId() {
        return loggedInUserId;
    }

    public String getLoggedInUsername() {
        return loggedInUsername;
    }

    public boolean isUserLoggedIn() {
        return loggedInUserId != -1;
    }

    // Clean up method
    public void cleanup() {
        if (view != null) {
            view.dispose();
        }
        // Add any additional cleanup needed
    }

    // Error handling method
    private void handleError(String message, Exception e) {
        e.printStackTrace();
        SwingUtilities.invokeLater(() -> {
            view.showError(message + "\nError: " + e.getMessage());
        });
    }

}