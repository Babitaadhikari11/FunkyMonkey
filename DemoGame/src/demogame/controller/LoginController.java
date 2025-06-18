package demogame.controller;

import demogame.dao.UserDao;
import demogame.model.UserData;
import demogame.view.GameView;
import demogame.view.LoginView;
import demogame.view.MenuView;
import demogame.view.SignUpView;
import javax.swing.JOptionPane; // Added for pop-up messages
import javax.swing.SwingUtilities;

public class LoginController {
    private UserDao userDAO;
    private LoginView view;

    private int loggedInUserId = -1;

    public LoginController(LoginView view) {
        this.view = view;
        this.userDAO = new UserDao();

        view.getLoginButton().addActionListener(e -> handleLogin());

        view.getCreateAccountLink().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                view.setVisible(false);
                SignUpView signUpPanel = new SignUpView();
                new SignupController(signUpPanel);
                signUpPanel.setVisible(true);
            }
        });
    }

    private void handleLogin() {
        String username = view.getUsername();
        String password = view.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            view.showError("Username and password cannot be empty.");
            return;
        }

        UserData user = userDAO.authenticate(username, password);

        // --- MODIFIED SECTION: Logic for role-based redirection ---
        if (user != null) {
            // Login successful, now check the user's role
            
            if ("admin".equalsIgnoreCase(user.getRole())) {
                // User is an ADMIN, open the Admin Dashboard
                JOptionPane.showMessageDialog(view, "Welcome Admin, " + user.getUsername() + "!");
                new AdminController(); // Launch the admin controller
                view.dispose(); // Close the login window
                
            } else {
                // User is a PLAYER, open the main game menu
                view.showSuccess("Login successful! Welcome, " + username + "!");
                this.loggedInUserId = user.getId();
                view.setVisible(false);
                MenuView menuView = new MenuView(username);
                new MenuController(menuView, user);
                menuView.setVisible(true);
            }
            
        } else {
            // Login failed
            view.showError(userDAO.getErrorMessage());
        }
    }

    private void launchGame() {
        SwingUtilities.invokeLater(() -> {
            view.dispose();
            GameView gameView = new GameView();
            new GameController(gameView);
            gameView.setVisible(true);
        });
    }

    public int getLoggedInUserId() {
        return loggedInUserId;
    }
}