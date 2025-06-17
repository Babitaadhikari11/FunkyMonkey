package demogame.view;

import demogame.util.RoundedPanel;
import javax.swing.*;
import java.awt.*;

public class ForgotPasswordView extends JFrame {
    private JTextField emailField = new JTextField();
    private JButton resetButton = new JButton("Reset Password");
    private JButton backButton = new JButton("Back to Login");

    public ForgotPasswordView() {
        setTitle("DemoGame - Forgot Password");
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

        RoundedPanel panel = new RoundedPanel(30);
        panel.setBackground(Color.WHITE);
        panel.setLayout(null);
        panel.setBounds(720, 150, 400, 300);
        layeredPane.add(panel, Integer.valueOf(1));

        JLabel title = new JLabel("Forgot Password");
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        title.setForeground(Color.RED);
        title.setBounds(100, 30, 300, 40);
        panel.add(title);

        JLabel emailLabel = new JLabel("Enter your registered email:");
        emailLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        emailLabel.setBounds(40, 90, 300, 30);
        panel.add(emailLabel);

        emailField.setBounds(40, 130, 320, 40);
        emailField.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        panel.add(emailField);

        resetButton.setBounds(40, 190, 320, 50);
        resetButton.setBackground(Color.RED);
        resetButton.setForeground(Color.WHITE);
        resetButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        resetButton.setOpaque(true);
        resetButton.setBorderPainted(false);
        panel.add(resetButton);

        backButton.setBounds(40, 250, 320, 40);
        backButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.add(backButton);
    }

    public String getEmail() {
        return emailField.getText();
    }

    public JButton getResetButton() {
        return resetButton;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
