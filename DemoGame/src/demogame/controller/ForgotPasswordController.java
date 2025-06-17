package demogame.controller;

import demogame.dao.UserDao;
import demogame.view.ForgotPasswordView;
import demogame.view.LoginView;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ForgotPasswordController {
    private ForgotPasswordView forgotView;
    private UserDao userDao;

    public ForgotPasswordController(ForgotPasswordView forgotView) {
        this.forgotView = forgotView;
        this.userDao = new UserDao();

        forgotView.getResetPasswordButton().addActionListener(e -> resetPassword());
        forgotView.getBackToLoginLabel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openLoginView();
            }
        });
    }

    private void resetPassword() {
        String username = forgotView.getUsername().trim();
        String email = forgotView.getEmail().trim();
        String newPassword = forgotView.getNewPassword().trim();

        if (username.isEmpty() || email.isEmpty() || newPassword.isEmpty()) {
            forgotView.showError("Please fill all fields.");
            return;
        }

        if (!userDao.checkUserByUsernameAndEmail(username, email)) {
            forgotView.showError(userDao.getErrorMessage());
            return;
        }

        boolean success = userDao.resetPassword(username, newPassword);
        if (success) {
            forgotView.showSuccess("Password has been reset successfully.");
            openLoginView();
        } else {
            forgotView.showError(userDao.getErrorMessage());
        }
    }

    private void openLoginView() {
        forgotView.dispose();
        LoginView loginView = new LoginView();
        new LoginController(loginView);
        loginView.setVisible(true);
    }
}
