package demogame.view;

import demogame.util.RoundedPanel;

import javax.swing.*;
import java.awt.*;

public class SignUpView extends JFrame {
    public JTextField usernameField = new JTextField();
    public JTextField emailField = new JTextField();
    public JPasswordField passwordField = new JPasswordField();
    public JCheckBox showPasswordCheck = new JCheckBox("Show Password");
    public JButton signUpButton = new JButton("Sign Up");
    public JLabel loginLink = new JLabel("<html><u>Already have an account? Login</u></html>");

    public SignUpView() {
        setTitle("DemoGame - Sign Up");
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
        RoundedPanel signUpPanel = new RoundedPanel(30);
        signUpPanel.setBackground(Color.WHITE);
        signUpPanel.setLayout(null);
        signUpPanel.setBounds(420, 60, 360, 450);
        layeredPane.add(signUpPanel, Integer.valueOf(1));

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 22));
        title.setForeground(Color.RED);
        title.setBounds(70, 20, 250, 30);
        signUpPanel.add(title);

        addLabeledField(signUpPanel, "Username:", usernameField, 70);
        addLabeledField(signUpPanel, "Email:", emailField, 140);
        addLabeledField(signUpPanel, "Password:", passwordField, 210);

        showPasswordCheck.setBounds(30, 270, 200, 25);
        signUpPanel.add(showPasswordCheck);

        // Show/hide password logic
        showPasswordCheck.addActionListener(e -> {
            if (showPasswordCheck.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });

        signUpButton.setBounds(30, 310, 300, 40);
        signUpButton.setBackground(Color.RED);
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        signUpButton.setOpaque(true);
        signUpButton.setBorderPainted(false);
        signUpPanel.add(signUpButton);

        loginLink.setForeground(Color.BLUE);
        loginLink.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
        loginLink.setBounds(30, 360, 300, 25);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signUpPanel.add(loginLink);
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