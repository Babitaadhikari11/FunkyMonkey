package demogame.controller;

import demogame.dao.UserDao;
import demogame.model.UserData;
import demogame.view.GameView;
import demogame.view.LoginView;
import demogame.view.MenuView;
import demogame.view.SignUpView;
import javax.swing.SwingUtilities;

public class LoginController {
    private UserDao userDAO;
    private LoginView view;

    // ✅ New: Field to store logged-in user's ID
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
        if (user != null) {
            view.showSuccess("Login successful! Welcome, " + username + "!");

            // ✅ New: store logged-in user's ID
            this.loggedInUserId = user.getId();  // Assumes UserData has getId()

            view.setVisible(false);
            MenuView menuView = new MenuView(username);
            new MenuController(menuView, user); // Passing user object
            menuView.setVisible(true);
        } else {
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

    // ✅ New: Getter for logged-in user ID
    public int getLoggedInUserId() {
        return loggedInUserId;
    }
}
