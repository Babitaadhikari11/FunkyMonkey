package demogame.controller;

import demogame.dao.UserDao;
import demogame.model.UserData;
import demogame.view.LoginView;
import demogame.view.MenuView;
import demogame.view.SignUpView;
import demogame.view.ForgotPasswordView;
import demogame.view.LoadingView;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.logging.Logger;
import java.util.logging.Level;

public class LoginController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());
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
                System.out.println("Create Account clicked!"); // Debug
                showSignUpView();
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                view.getCreateAccountLink().setText("<html><u>Create Account</u></html>");
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                view.getCreateAccountLink().setText("<html><u>Create Account</u></html>"); // Match LoginView's underlined style
            }
        });
        view.getForgotPasswordLink().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                System.out.println("Forgot Password clicked!"); // Debug
                showForgotPasswordView();
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                view.getForgotPasswordLink().setText("<html><u>Forgot Password?</u></html>");
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                view.getForgotPasswordLink().setText("<html><u>Forgot Password?</u></html>"); // Match LoginView's underlined style
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
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            view.showError("Username and password cannot be empty.");
            return false;
        }
        return true;
    }

    private void authenticateUser(String username, String password) {
        try {
            UserData user = userDAO.authenticate(username, password);
            if (user != null) {
                if ("admin".equals(user.getRole())) {
                    handleAdminLogin(user);
                } else {
                    handleSuccessfulLogin(user);
                }
            } else {
                view.showError(userDAO.getErrorMessage());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Authentication error for username: " + username, e);
            view.showError("Authentication error: " + e.getMessage());
        }
    }

    private void handleAdminLogin(UserData user) {
        this.loggedInUserId = user.getId();
        this.loggedInUsername = user.getUsername();
        view.showSuccess("Admin login successful! Welcome, " + loggedInUsername + "!");
        
        SwingUtilities.invokeLater(() -> {
            view.setVisible(false);
            new AdminController();
            view.dispose();
        });
    }

    private void handleSuccessfulLogin(UserData user) {
        this.loggedInUserId = user.getId();
        this.loggedInUsername = user.getUsername();
        view.showSuccess("Login successful! Welcome, " + loggedInUsername + "!");
        
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

    private void showForgotPasswordView() {
        SwingUtilities.invokeLater(() -> {
            view.setVisible(false);
            ForgotPasswordView forgotPasswordView = new ForgotPasswordView();
            new ForgotPasswordController(forgotPasswordView);
            forgotPasswordView.setVisible(true);
        });
    }

    private void showMenuView(UserData user) {
        SwingUtilities.invokeLater(() -> {
            try {
                LOGGER.info("Showing MenuView for userId: " + user.getId());
                GameController gameController = new GameController(user.getId());
                MenuView menuView = new MenuView(gameController);
                menuView.setVisible(true);
                view.dispose();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error showing MenuView for userId: " + user.getId(), e);
                JOptionPane.showMessageDialog(null,
                    "Error displaying menu: " + e.getMessage(),
                    "Menu Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void startGame() {
        SwingUtilities.invokeLater(() -> {
            LOGGER.info("Starting game for userId: " + loggedInUserId);
            LoadingView loadingView = new LoadingView();
            new LoadingController(loadingView, loggedInUserId);
            loadingView.setVisible(true);
            
            if (view != null) {
                view.dispose();
            }
        });
    }

    public int getLoggedInUserId() {
        return loggedInUserId;
    }

    public String getLoggedInUsername() {
        return loggedInUsername;
    }

    public boolean isUserLoggedIn() {
        return loggedInUserId != -1;
    }

    public void cleanup() {
        if (view != null) {
            view.dispose();
        }
    }

    private void handleError(String message, Exception e) {
        LOGGER.log(Level.SEVERE, message, e);
        SwingUtilities.invokeLater(() -> {
            view.showError(message + "\nError: " + e.getMessage());
        });
    }

    // Added getter for UserDao
    public UserDao getUserDao() {
        return userDAO;
    }
}