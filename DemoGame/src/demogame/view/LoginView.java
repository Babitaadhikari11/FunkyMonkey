package demogame.view;

import demogame.util.RoundedPanel;
import java.awt.*;
import javax.swing.*;

public class LoginView extends JFrame {
    public JTextField usernameField = new JTextField();
    public JPasswordField passwordField = new JPasswordField();
    public JCheckBox showPasswordCheck = new JCheckBox("Show Password");
    public JButton loginButton = new JButton("Login");
    public JLabel createAccountLink = new JLabel("<html><u>Create Account</u></html>");
    public JLabel forgotPasswordLink = new JLabel("<html><u>Forgot Password?</u></html>");

    public LoginView() {
        setTitle("DemoGame - Login");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JLayeredPane layeredPane = new JLayeredPane();
        getContentPane().add(layeredPane);

        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/resources/Background.jpg"));
        Image scaled = bgIcon.getImage().getScaledInstance(1200, 800, Image.SCALE_SMOOTH);
        JLabel background = new JLabel(new ImageIcon(scaled));
        background.setBounds(0, 0, 1200, 800);
        layeredPane.add(background, Integer.valueOf(0));

        RoundedPanel loginPanel = new RoundedPanel(30);
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setLayout(null);
        loginPanel.setBounds(720, 150, 400, 500);
        layeredPane.add(loginPanel, Integer.valueOf(1));

        JLabel title = new JLabel("Welcome Back !");
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        title.setForeground(Color.RED);
        title.setBounds(80, 30, 300, 40);
        loginPanel.add(title);

        addLabeledField(loginPanel, "Username:", usernameField, 90);
        addLabeledField(loginPanel, "Password:", passwordField, 180);

        showPasswordCheck.setBounds(40, 260, 200, 30);
        showPasswordCheck.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        loginPanel.add(showPasswordCheck);

        showPasswordCheck.addActionListener(e -> {
            if (showPasswordCheck.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });

        forgotPasswordLink.setForeground(Color.BLUE);
        forgotPasswordLink.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        forgotPasswordLink.setBounds(240, 260, 140, 30);
        forgotPasswordLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginPanel.add(forgotPasswordLink);

        loginButton.setBounds(40, 310, 320, 50);
        loginButton.setBackground(Color.RED);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginPanel.add(loginButton);

        JLabel question = new JLabel("Don't have an account?");
        question.setBounds(40, 380, 180, 30);
        question.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        loginPanel.add(question);

        createAccountLink.setForeground(Color.BLUE);
        createAccountLink.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        createAccountLink.setBounds(220, 380, 140, 30);
        createAccountLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginPanel.add(createAccountLink);
    }

    private void addLabeledField(JPanel panel, String label, JComponent field, int y) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        jLabel.setBounds(40, y, 200, 30);
        panel.add(jLabel);

        field.setBounds(40, y + 30, 320, 40);
        field.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        panel.add(field);
    }

    // getters
    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public JLabel getCreateAccountLink() {
        return createAccountLink;
    }

    public JLabel getForgotPasswordLink() {
        return forgotPasswordLink;
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}