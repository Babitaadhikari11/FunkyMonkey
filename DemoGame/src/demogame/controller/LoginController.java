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

    public LoginController(LoginView view) {
        this.view = view;
        this.userDAO = new UserDao();

        // Add action listener for login button
        view.getLoginButton().addActionListener(e -> handleLogin());

        // Add mouse listener for create account link
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
// validation
        if (username.isEmpty() || password.isEmpty()) {
            view.showError("Username and password cannot be empty.");
            return;
        }

        UserData user = userDAO.authenticate(username, password);
        if (user != null) {
            view.showSuccess("Login successful! Welcome, " + username + "!");
            view.setVisible(false);
            //  navigation to MenuPanel; 
            MenuView menuView = new MenuView(username);
            new MenuController(menuView, user); //PASSING USER OBJECT
            menuView.setVisible(true);
        } else {
            view.showError(userDAO.getErrorMessage());
        }
    }
    private void launchGame() {
    SwingUtilities.invokeLater(() -> {
        view.dispose();
        GameView gameView = new GameView(); // ⬅️ This should create the frame with GamePanel
        new GameController(gameView);       // ⬅️ Optional logic
        gameView.setVisible(true);
    });
}

}
