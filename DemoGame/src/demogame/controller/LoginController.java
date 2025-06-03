package demogame.controller;

import demogame.dao.UserDao;
import demogame.model.UserData;
import demogame.view.LoginView;
<<<<<<< HEAD
import demogame.view.MenuView;
=======
>>>>>>> 4bca1e4ac703fa2092ef69540c903863cfc1aee1
import demogame.view.SignUpView;
import demogame.view.MenuView;
import java.awt.event.*;

public class LoginController {
    private UserDao userDAO;
    private LoginView view;

    public LoginController(LoginView view) {
        this.view = view;
        this.userDAO = new UserDao();

        // Add action listener for login button
        view.getLoginButton().addActionListener(e -> handleLogin());
<<<<<<< HEAD
        
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
=======

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
>>>>>>> 4bca1e4ac703fa2092ef69540c903863cfc1aee1
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
<<<<<<< HEAD
            view.setVisible(false);
            //  navigation to MenuPanel; 
            MenuView menuView = new MenuView(username);
            new MenuController(menuView, user); //PASSING USER OBJECT
            menuView.setVisible(true);
=======
<<<<<<< HEAD
            //  navigation to MenuPanel; 
            view.setVisible(false);
            MenuView menuView =  new MenuView(username);
            new MenuController(menuView, user); // passing the user object
            menuView.setVisible(true);

=======
            // No navigation to MenuPanel; stay on LoginPanel
>>>>>>> 4bca1e4ac703fa2092ef69540c903863cfc1aee1
>>>>>>> cbe49f0c9b96a62bd2c68d6b96c2c63be4620f52
        } else {
            view.showError(userDAO.getErrorMessage());
        }
    }
}