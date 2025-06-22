package demogame.controller;

import demogame.dao.UserDao;
import demogame.service.OtpService;
import demogame.view.ForgotPasswordView;
import demogame.view.LoginView;

import javax.swing.*;
import java.awt.event.*;

public class ForgotPasswordController {
    private ForgotPasswordView view;
    private UserDao userDao;
    private LoginController loginController; // To navigate back

    public ForgotPasswordController(ForgotPasswordView view) {
        this.view = view;
        this.userDao = new UserDao(); // Default instantiation; replace with dependency injection if needed
        initializeListeners();
    }

    public ForgotPasswordController(ForgotPasswordView view, UserDao userDao, LoginController loginController) {
        this.view = view;
        this.userDao = userDao;
        this.loginController = loginController;
        initializeListeners();
    }
private void initializeListeners() {
    JButton sendOtpButton = view.getSendOtpButton();
    if (sendOtpButton == null) {
        System.out.println("Error: Send OTP button is null!");
    } else {
        System.out.println("Send OTP button found, adding listener...");
        sendOtpButton.addActionListener(e -> {
            System.out.println("Send OTP button clicked!");
            handleSendOtp();
        });
    }
    view.getResetPasswordButton().addActionListener(e -> handleResetPassword());
    view.getBackToLoginLink().addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            view.setVisible(false);
            if (loginController != null) {
                loginController.cleanup();
            }
            new LoginView().setVisible(true);
        }
    });
}

    private void handleSendOtp() {
        String email = view.getEmail().trim();
        if (email.isEmpty()) {
            view.showError("Please enter an email address.");
            return;
        }
        if (!isValidEmail(email)) {
            view.showError("Invalid email format.");
            return;
        }
        OtpService.sendOtp(email);
        view.showSuccess("OTP sent to " + email + ". Check your inbox!");
        System.out.println("OTP sent to " + email + " at " + new java.util.Date()); // Debug
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
    

    private void handleResetPassword() {
        String email = view.getEmail().trim();
        String otp = view.getOtp().trim();
        String newPassword = view.getNewPassword().trim();
        String confirmPassword = view.getConfirmPassword().trim();

        if (email.isEmpty() || otp.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            view.showError("All fields are required.");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            view.showError("Passwords do not match.");
            return;
        }
        if (!OtpService.verifyOtp(email, otp)) {
            view.showError("Invalid or expired OTP. Please try again.");
            return;
        }

        boolean resetSuccess = userDao.updatePasswordByEmail(email, newPassword);
        if (!resetSuccess && userDao.getErrorMessage() != null) {
            view.showError(userDao.getErrorMessage());
            return;
        }

        if (!resetSuccess) {
            // If update fails, try registering a new user
            resetSuccess = userDao.register(email, newPassword, "player");
            if (!resetSuccess) {
                view.showError(userDao.getErrorMessage() != null ? userDao.getErrorMessage() : "Failed to create account.");
                return;
            }
            view.showSuccess("Account created with email " + email + ". You can now log in.");
        } else {
            view.showSuccess("Password reset successfully for " + email + ".");
        }

        OtpService.deleteOtp(email); // Clean up
        view.setVisible(false);
        if (loginController != null) {
            loginController.cleanup();
        }
        new LoginView().setVisible(true);
    }

    public void cleanup() {
        if (view != null) {
            view.dispose();
        }
    }
}