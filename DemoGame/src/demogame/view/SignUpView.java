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
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JLayeredPane layeredPane = new JLayeredPane();
        getContentPane().add(layeredPane);

        // Background Image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/resources/Background.jpg"));
        Image scaled = bgIcon.getImage().getScaledInstance(1200, 800, Image.SCALE_SMOOTH);
        JLabel background = new JLabel(new ImageIcon(scaled));
        background.setBounds(0, 0, 1200, 800);
        layeredPane.add(background, Integer.valueOf(0));

        // Right Panel (moved lower)
        RoundedPanel signUpPanel = new RoundedPanel(30);
        signUpPanel.setBackground(Color.WHITE);
        signUpPanel.setLayout(null);
        signUpPanel.setBounds(720, 130, 400, 600);  // Changed y from 80 to 130
        layeredPane.add(signUpPanel, Integer.valueOf(1));

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        title.setForeground(Color.RED);
        title.setBounds(80, 30, 300, 40);
        signUpPanel.add(title);

        addLabeledField(signUpPanel, "Username:", usernameField, 90);
        addLabeledField(signUpPanel, "Email:", emailField, 180);
        addLabeledField(signUpPanel, "Password:", passwordField, 270);

        showPasswordCheck.setBounds(40, 350, 200, 30);
        showPasswordCheck.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        signUpPanel.add(showPasswordCheck);

        showPasswordCheck.addActionListener(e -> {
            if (showPasswordCheck.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });

        signUpButton.setBounds(40, 400, 320, 50);
        signUpButton.setBackground(Color.RED);
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        signUpButton.setOpaque(true);
        signUpButton.setBorderPainted(false);
        signUpPanel.add(signUpButton);

        loginLink.setForeground(Color.BLUE);
        loginLink.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        loginLink.setBounds(40, 470, 300, 30);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signUpPanel.add(loginLink);
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

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}