package demogame.view;

import demogame.controller.AdminController;
import demogame.model.Notification;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

// Admin panel for managing notifications
public class AdminView3 extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(AdminView3.class.getName());
    private AdminController adminController;

    // Constructor accepting AdminController
    public AdminView3(AdminController controller) {
        this.adminController = controller;
        initComponents();
        setMinimumSize(new Dimension(1200, 600));
        setSize(1200, 600);
        setLocationRelativeTo(null);
        loadNotifications(); // Load notifications on initialization
        setupPushNotification();
    }

    // Load notifications into JList
    public void loadNotifications() {
        try {
            List<Notification> notifications = adminController.getNotifications();
            DefaultListModel<String> model = new DefaultListModel<>();
            for (Notification notification : notifications) {
                model.addElement(notification.getMessage());
            }
            jList1.setModel(model);
            LOGGER.info("Notifications loaded into JList: " + notifications.size());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading notifications", e);
            JOptionPane.showMessageDialog(this, "Error loading notifications: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Setup action listener for the Push Notification button
    private void setupPushNotification() {
        jButton4.addActionListener(e -> {
            String notification = jTextField1.getText().trim();
            if (!notification.isEmpty()) {
                adminController.addNotification(notification);
                JOptionPane.showMessageDialog(this, "Notification added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                jTextField1.setText("");
                loadNotifications(); // Refresh the list
            } else {
                JOptionPane.showMessageDialog(this, "Notification cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold desc="Generated Code">
    private void initComponents() {
        jScrollPane1 = new JScrollPane();
        jPanel1 = new JPanel();
        jPanel3 = new JPanel();
        jPanel4 = new JPanel();
        jLabel2 = new JLabel();
        jButton1 = new JButton();
        jButton2 = new JButton();
        jButton3 = new JButton();
        jPanel5 = new JPanel();
        jLabel3 = new JLabel();
        jButton4 = new JButton();
        jPanel2 = new JPanel();
        jLabel1 = new JLabel();
        jScrollPane2 = new JScrollPane();
        jList1 = new JList<>();
        jPanel6 = new JPanel();
        jLabel4 = new JLabel();
        jTextField1 = new JTextField();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new Color(203, 235, 220));
        jPanel1.setBorder(BorderFactory.createEtchedBorder());

        jPanel3.setBackground(new Color(197, 225, 195));
        jPanel3.setBorder(BorderFactory.createEtchedBorder());

        jPanel4.setBackground(new Color(242, 244, 230));
        jPanel4.setBorder(BorderFactory.createEtchedBorder());

        jLabel2.setFont(new Font("Arial", Font.BOLD, 14));
        jLabel2.setText("Game Dashboard");

        GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(20, 20, 20)
                    .addComponent(jLabel2)
                    .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(15, 15, 15)
                    .addComponent(jLabel2)
                    .addContainerGap(20, Short.MAX_VALUE))
        );

        jButton1.setFont(new Font("Arial", Font.BOLD, 14));
        jButton1.setText("Dashboard");
        jButton1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jButton2.setFont(new Font("Arial", Font.BOLD, 14));
        jButton2.setText("User");
        jButton2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jButton3.setFont(new Font("Arial", Font.BOLD, 14));
        jButton3.setText("Notification");
        jButton3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jPanel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(jButton3, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))
                    .addGap(16, 16, 16))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addComponent(jPanel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(51, 51, 51)
                    .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
                    .addGap(69, 69, 69)
                    .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                    .addGap(56, 56, 56)
                    .addComponent(jButton3, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 203, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new Color(207, 222, 212));
        jPanel5.setBorder(BorderFactory.createEtchedBorder());

        jLabel3.setFont(new Font("Arial", Font.PLAIN, 24));
        jLabel3.setText("Welcome to Admin Panel");

        jButton4.setFont(new Font("Arial", Font.BOLD, 14));
        jButton4.setText("Add Notification");
        jButton4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        GroupLayout jPanel5Layout = new GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(386, 386, 386)
                    .addComponent(jLabel3)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4)
                    .addGap(58, 58, 58))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jButton4))
                    .addContainerGap(14, Short.MAX_VALUE))
        );

        jLabel1.setFont(new Font("Arial", Font.BOLD, 14));
        jLabel1.setText("Notifications");

        jList1.setFont(new Font("Arial", Font.PLAIN, 14));
        jList1.setModel(new DefaultListModel<>());
        jScrollPane2.setViewportView(jList1);

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(132, 132, 132)
                            .addComponent(jLabel1))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(41, 41, 41)
                            .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 268, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(51, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(26, 26, 26)
                    .addComponent(jLabel1)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 312, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(37, Short.MAX_VALUE))
        );

        jLabel4.setFont(new Font("Arial", Font.BOLD, 14));
        jLabel4.setText("Write notification to the user");

        jTextField1.setFont(new Font("Arial", Font.PLAIN, 14));

        GroupLayout jPanel6Layout = new GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addGap(202, 202, 202)
                    .addComponent(jLabel4)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                    .addContainerGap(68, Short.MAX_VALUE)
                    .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, 411, GroupLayout.PREFERRED_SIZE)
                    .addGap(59, 59, 59))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addGap(27, 27, 27)
                    .addComponent(jLabel4)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, 299, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(43, 43, 43)
                            .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 96, Short.MAX_VALUE)
                            .addComponent(jPanel6, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(74, 74, 74))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(jPanel5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(44, 44, 44)
                    .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGap(0, 0, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    // Variables declaration
    public JButton jButton1;
    public JButton jButton2;
    public JButton jButton3;
    private JButton jButton4;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JList<String> jList1;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JPanel jPanel4;
    private JPanel jPanel5;
    private JPanel jPanel6;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JTextField jTextField1;
    // End of variables declaration
}