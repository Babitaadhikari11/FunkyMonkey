package demogame.view;

import demogame.controller.SignupController;
import demogame.model.SignUpModel;
import demogame.util.RoundedPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginView extends JFrame {
    public JTextField usernameField = new JTextField();
    public JPasswordField passwordField = new JPasswordField();
    public JCheckBox showPasswordCheck = new JCheckBox("Show Password");
    public JButton loginButton = new JButton("Login");
    public JLabel createAccountLink = new JLabel("<html><u>Create Account</u></html>");

    public LoginView() {
        setTitle("DemoGame - Login");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JLayeredPane layeredPane = new JLayeredPane();
        getContentPane().add(layeredPane);

        // Background Image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/resources/Background.jpg"));
        Image scaled = bgIcon.getImage().getScaledInstance(800, 600, Image.SCALE_SMOOTH);
        JLabel background = new JLabel(new ImageIcon(scaled));
        background.setBounds(0, 0, 800, 600);
        layeredPane.add(background, Integer.valueOf(0));

        // Right Panel
        RoundedPanel loginPanel = new RoundedPanel(30);
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setLayout(null);
        loginPanel.setBounds(420, 80, 360, 400);
        layeredPane.add(loginPanel, Integer.valueOf(1));

        JLabel title = new JLabel("Welcome Back !");
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 22));
        title.setForeground(Color.RED);
        title.setBounds(70, 20, 250, 30);
        loginPanel.add(title);

        addLabeledField(loginPanel, "Username:", usernameField, 70);
        addLabeledField(loginPanel, "Password:", passwordField, 140);

        showPasswordCheck.setBounds(30, 200, 200, 25);
        loginPanel.add(showPasswordCheck);

        // Show/hide password logic
        showPasswordCheck.addActionListener(e -> {
            if (showPasswordCheck.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });

        loginButton.setBounds(30, 240, 300, 40);
        loginButton.setBackground(Color.RED);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginPanel.add(loginButton);

        JLabel question = new JLabel("Don't have an account?");
        question.setBounds(30, 300, 160, 25);
        question.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
        loginPanel.add(question);

        createAccountLink.setForeground(Color.BLUE);
        createAccountLink.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
        createAccountLink.setBounds(190, 300, 140, 25);
        createAccountLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginPanel.add(createAccountLink);

        // Redirect to SignUpView
        createAccountLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                SignUpView signUpView = new SignUpView();
                SignUpModel signUpModel = new SignUpModel();
                new SignupController(signUpModel, signUpView);
                signUpView.setVisible(true);
            }
        });
    }

    private void addLabeledField(JPanel panel, String label, JComponent field, int y) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        jLabel.setBounds(30, y, 200, 25);
        panel.add(jLabel);

        field.setBounds(30, y + 25, 300, 30);
        panel.add(field);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}