package demogame.view;

import javax.swing.*;
import java.awt.*;

public class ForgotPasswordView extends JFrame {
    private JTextField emailField;
    private JTextField otpField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton sendOtpButton;
    private JButton resetPasswordButton;
    private JLabel backToLoginLink;

    public ForgotPasswordView() {
        setTitle("DemoGame - Forgot Password");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 450);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Reset Password");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLUE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(emailLabel, gbc);

        emailField = new JTextField(15);
        emailField.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(emailField, gbc);

        JLabel otpLabel = new JLabel("OTP:");
        otpLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(otpLabel, gbc);

        otpField = new JTextField(15);
        otpField.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        otpField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(otpField, gbc);

        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(newPasswordLabel, gbc);

        newPasswordField = new JPasswordField(15);
        newPasswordField.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        newPasswordField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(newPasswordField, gbc);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField(15);
        confirmPasswordField.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        confirmPasswordField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(confirmPasswordField, gbc);

        sendOtpButton = new JButton("Send OTP");
        sendOtpButton.setEnabled(true); // Ensure button is enabled
        sendOtpButton.setFocusable(true); // Ensure it can receive focus
        sendOtpButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        sendOtpButton.setBackground(Color.RED);
        sendOtpButton.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(sendOtpButton, gbc);
        System.out.println("Send OTP button added at " + new java.util.Date() + ", enabled: " + sendOtpButton.isEnabled() + ", visible: " + sendOtpButton.isVisible());

        resetPasswordButton = new JButton("Reset Password");
        resetPasswordButton.setEnabled(true);
        resetPasswordButton.setFocusable(true);
        resetPasswordButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        resetPasswordButton.setBackground(Color.RED);
        resetPasswordButton.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        add(resetPasswordButton, gbc);
        System.out.println("Reset Password button added at " + new java.util.Date() + ", enabled: " + resetPasswordButton.isEnabled() + ", visible: " + resetPasswordButton.isVisible());

        backToLoginLink = new JLabel("Back to Login");
        backToLoginLink.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        backToLoginLink.setForeground(Color.BLUE);
        backToLoginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        add(backToLoginLink, gbc);
    }
    

    // Getters
    public String getEmail() { return emailField.getText().trim(); }
    public String getOtp() { return otpField.getText().trim(); }
    public String getNewPassword() { return new String(newPasswordField.getPassword()).trim(); }
    public String getConfirmPassword() { return new String(confirmPasswordField.getPassword()).trim(); }
    public JButton getSendOtpButton() { return sendOtpButton; }
    public JButton getResetPasswordButton() { return resetPasswordButton; }
    public JLabel getBackToLoginLink() { return backToLoginLink; }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}