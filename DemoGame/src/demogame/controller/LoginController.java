package demogame.controller;

import demogame.dao.UserDao;
import demogame.model.UserData;
import demogame.view.GameView;
import demogame.view.LoginView;
import demogame.view.MenuView;
import demogame.view.SignUpView;
import demogame.view.ForgotPasswordView;

import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginController {
    private UserDao userDAO;
    private LoginView view;

    private int loggedInUserId = -1;

    public LoginController(LoginView view) {
        this.view = view;
        this.userDAO = new UserDao();

        view.getLoginButton().addActionListener(e -> handleLogin());

        view.getCreateAccountLink().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                view.setVisible(false);
                SignUpView signUpPanel = new SignUpView();
                new SignupController(signUpPanel);
                signUpPanel.setVisible(true);
            }
        });

        // Handle forgot password link click here:
        view.getForgotPasswordLink().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                view.setVisible(false);
                ForgotPasswordView forgotView = new ForgotPasswordView();
                new ForgotPasswordController(forgotView);
                forgotView.setVisible(true);
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
            this.loggedInUserId = user.getId();

            view.setVisible(false);
            MenuView menuView = new MenuView(username);
            new MenuController(menuView, user);
            menuView.setVisible(true);
        } else {
            view.showError(userDAO.getErrorMessage());
        }
    }

    public int getLoggedInUserId() {
        return loggedInUserId;
    }
}
