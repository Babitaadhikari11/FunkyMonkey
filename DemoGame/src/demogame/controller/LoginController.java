package demogame.controller;

import demogame.dao.UserDao;
import demogame.model.UserData;
import demogame.view.LoginView;
import demogame.view.MenuView;
import demogame.view.SignUpView;
import java.awt.event.*;

public class LoginController {
    private UserDao userDAO;
    private LoginView view;

    public LoginController(LoginView view) {
        this.view = view;
        this.userDAO = new UserDao();

        // Add action listener for login button
        view.getLoginButton().addActionListener(e -> handleLogin());
        
        view.getCreateAccountLink().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e){
                view.setVisible(false);
                SignUpView signUpView =  new SignUpView();
                signUpView.setVisible(true);
            }
        });

        // // Add mouse listener for create account link
        // view.getCreateAccountLink().addMouseListener(new MouseAdapter() {
        //     @Override
        //     public void mouseClicked(MouseEvent e) {
        //         view.setVisible(false);
        //         SignUpView signUpPanel = new SignUpView();
        //         new SignupController(signUpPanel);
        //         signUpPanel.setVisible(true);
        //     }
        // });
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
            //  navigation to MenuPanel; 
            view.setVisible(false);
            MenuView menuView =  new MenuView(username);
            new MenuController(menuView, user); // passing the user object
            menuView.setVisible(true);

        } else {
            view.showError(userDAO.getErrorMessage());
        }
    }
}