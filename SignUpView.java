import java.awt.*;
import javax.swing.*;

public class SignUpView extends JFrame {

    public SignUpView() {
        setTitle("FunkyMonkey - Sign Up");  
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Load background image
        ImageIcon bgIcon = new ImageIcon("background.png");
        Image bgImage = bgIcon.getImage();

        // Background panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

       
       
    

        // Form Panel
        JPanel formPanel = new RoundedPanel(30);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBounds(600, 100, 350, 450);
        formPanel.setLayout(null);
        backgroundPanel.add(formPanel);

        JLabel formTitle = new JLabel("Create your free account");
        formTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        formTitle.setBounds(40, 20, 300, 30);
        formPanel.add(formTitle);

        // Name Label and Field
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setBounds(40, 60, 270, 20);
        formPanel.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(40, 80, 270, 35);
        formPanel.add(nameField);

        // Email Label and Field
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setBounds(40, 125, 270, 20);
        formPanel.add(emailLabel);

        JTextField emailField = new JTextField();
        emailField.setBounds(40, 145, 270, 35);
        formPanel.add(emailField);

        // Password Label and Field
        JLabel passLabel = new JLabel("Password");
        passLabel.setBounds(40, 190, 270, 20);
        formPanel.add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(40, 210, 270, 35);
        formPanel.add(passField);

        // Confirm Password Label and Field
        JLabel confirmLabel = new JLabel("Confirm Password");
        confirmLabel.setBounds(40, 255, 270, 20);
        formPanel.add(confirmLabel);

        JPasswordField confirmField = new JPasswordField();
        confirmField.setBounds(40, 275, 270, 35);
        formPanel.add(confirmField);

        // Show Password Checkbox
        JCheckBox showPass = new JCheckBox("Show Password");
        showPass.setFont(new Font("SansSerif", Font.PLAIN, 13));
        showPass.setBounds(40, 320, 150, 25);
        showPass.setOpaque(false);
        formPanel.add(showPass);

        // Sign Up Button
        JButton signUpBtn = new JButton("Sign Up");
        signUpBtn.setBounds(40, 360, 270, 45);
        signUpBtn.setBackground(new Color(220, 53, 69));
        signUpBtn.setForeground(Color.WHITE);
        signUpBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        formPanel.add(signUpBtn);

        // Show/Hide Password Logic
        showPass.addActionListener(e -> {
            boolean show = showPass.isSelected();
            passField.setEchoChar(show ? (char) 0 : '*');
            confirmField.setEchoChar(show ? (char) 0 : '*');
        });

        setVisible(true);
    }

    // Rounded panel class
    class RoundedPanel extends JPanel {
        private int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SignUpView::new);
    }
}
