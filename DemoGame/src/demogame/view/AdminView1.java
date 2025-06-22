package demogame.view;

import demogame.controller.AdminController;
import demogame.dao.ScoreDao;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminView1 extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(AdminView1.class.getName());

    private AdminController adminController;
    private JLabel totalUsersLabel, activeUsersLabel, newestUserLabel;
    private JTable activeUsersTable;
    private DefaultTableModel tableModel;
    private JScrollPane tableScrollPane;
    public JButton jButton1, jButton2, jButton3; // Made public for navigation
    private JButton refreshButton;

    public AdminView1() {
        setMinimumSize(new java.awt.Dimension(1200, 600)); // Standardized
        setSize(1200, 600); // Standardized
        setLocationRelativeTo(null); // Center the window
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(203, 235, 220)); // Consistent background
        mainPanel.setBorder(BorderFactory.createEtchedBorder());

        // Left Navigation Panel
        JPanel navPanel = new JPanel();
        navPanel.setBackground(new Color(197, 225, 195));
        navPanel.setBorder(BorderFactory.createEtchedBorder());
        navPanel.setLayout(new BorderLayout());
        navPanel.setPreferredSize(new Dimension(150, 600)); // Match the height of the frame

        JPanel navHeader = new JPanel();
        navHeader.setBackground(new Color(242, 244, 230));
        navHeader.setBorder(BorderFactory.createEtchedBorder());
        JLabel navLabel = new JLabel("Game Dashboard");
        navLabel.setFont(new Font("Arial", Font.BOLD, 14));
        navHeader.add(navLabel);

        // Vertical Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(197, 225, 195));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // Vertical layout
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        jButton1 = new JButton("Dashboard");
        jButton2 = new JButton("Users");
        jButton3 = new JButton("Notifications");
        int buttonWidth = 120; // Consistent width
        int buttonHeight = 38; // Consistent height
        Dimension buttonSize = new Dimension(buttonWidth, buttonHeight);
        jButton1.setPreferredSize(buttonSize);
        jButton2.setPreferredSize(buttonSize);
        jButton3.setPreferredSize(buttonSize);
        jButton1.setFont(new Font("Arial", Font.BOLD, 14));
        jButton2.setFont(new Font("Arial", Font.BOLD, 14));
        jButton3.setFont(new Font("Arial", Font.BOLD, 14));
        jButton1.setBorder(BorderFactory.createSoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton2.setBorder(BorderFactory.createSoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton3.setBorder(BorderFactory.createSoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        // Add buttons with spacing to match AdminView3 layout
        buttonPanel.add(jButton1);
        buttonPanel.add(Box.createVerticalStrut(69)); // Spacing to match AdminView3
        buttonPanel.add(jButton2);
        buttonPanel.add(Box.createVerticalStrut(56)); // Spacing to match AdminView3
        buttonPanel.add(jButton3);

        navPanel.add(navHeader, BorderLayout.NORTH);
        navPanel.add(buttonPanel, BorderLayout.CENTER);

        // Top Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(207, 222, 212));
        headerPanel.setBorder(BorderFactory.createEtchedBorder());
        JLabel headerLabel = new JLabel("Welcome to Admin Panel");
        headerLabel.setFont(new Font("Geeza Pro", Font.PLAIN, 24));
        headerPanel.add(headerLabel);

        // Main Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(203, 235, 220));

        // Stats Panel
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBackground(new Color(207, 222, 212));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Dashboard Stats"));

        // Make stats labels more noticeable
        totalUsersLabel = new JLabel("Total Users: -");
        activeUsersLabel = new JLabel("Active Users: -");
        newestUserLabel = new JLabel("Newest User: -");
        totalUsersLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Larger, bold font
        activeUsersLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Larger, bold font
        newestUserLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Larger, bold font
        totalUsersLabel.setForeground(Color.BLUE); // Distinct color
        activeUsersLabel.setForeground(Color.GREEN); // Distinct color
        newestUserLabel.setForeground(Color.RED); // Distinct color

        JPanel labelsPanel = new JPanel();
        labelsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 0)); // Horizontal layout with spacing
        labelsPanel.add(totalUsersLabel);
        labelsPanel.add(activeUsersLabel);
        labelsPanel.add(newestUserLabel);

        statsPanel.add(labelsPanel, BorderLayout.CENTER);

        // Active Users Table
        setupActiveUsersTable();

        // Refresh Button
        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setBorder(BorderFactory.createSoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(tableScrollPane, BorderLayout.CENTER);
        contentPanel.add(refreshButton, BorderLayout.SOUTH);

        mainPanel.add(navPanel, BorderLayout.WEST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        LOGGER.info("AdminView1 UI initialized successfully");
    }

    private void setupActiveUsersTable() {
        String[] columnNames = {"Username", "High Score", "Games Played", "Days Played", "Last Played"};
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        activeUsersTable = new JTable(tableModel);
        tableScrollPane = new JScrollPane(activeUsersTable);
        activeUsersTable.setFont(new Font("Arial", Font.PLAIN, 14));
        activeUsersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        activeUsersTable.setRowHeight(25);
        activeUsersTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        activeUsersTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        activeUsersTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        activeUsersTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        activeUsersTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < activeUsersTable.getColumnCount(); i++) {
            activeUsersTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        LOGGER.info("Active users table setup completed");
    }

    public void setAdminController(AdminController controller) {
        this.adminController = controller;
        addRefreshButtonListener(e -> {
            if (adminController != null) {
                adminController.refreshDashboard();
            } else {
                LOGGER.severe("adminController is null in setAdminController");
            }
        });
    }

    public void addRefreshButtonListener(ActionListener listener) {
        if (refreshButton != null) {
            refreshButton.addActionListener(listener);
            LOGGER.info("Refresh button listener added");
        } else {
            LOGGER.severe("refreshButton is null in addRefreshButtonListener");
        }
    }

    public void updateDashboardStats(int totalUsers, int activeUsers, String newestUser) {
        SwingUtilities.invokeLater(() -> {
            try {
                totalUsersLabel.setText("Total Users: " + (totalUsers >= 0 ? totalUsers : "-"));
                activeUsersLabel.setText("Active Users: " + (activeUsers >= 0 ? activeUsers : "-"));
                newestUserLabel.setText("Newest User: " + (newestUser != null ? newestUser : "-"));
                LOGGER.info("Dashboard stats updated: Total=" + totalUsers + ", Active=" + activeUsers + ", Newest=" + newestUser);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error updating dashboard stats", e);
            }
        });
    }

    public void updateActiveUsersList(List<ScoreDao.ActiveUserScore> activeUsers) {
        SwingUtilities.invokeLater(() -> {
            try {
                LOGGER.info("Updating active users list with " + (activeUsers != null ? activeUsers.size() : 0) + " users");
                if (tableModel == null) {
                    setupActiveUsersTable();
                }
                tableModel.setRowCount(0);

                if (activeUsers != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    for (ScoreDao.ActiveUserScore user : activeUsers) {
                        Object[] row = {
                            user.getUsername(),
                            user.getHighScore(),
                            user.getGamesPlayed(),
                            user.getDaysPlayed(),
                            dateFormat.format(user.getLastPlayed())
                        };
                        tableModel.addRow(row);
                    }
                }

                activeUsersTable.revalidate();
                activeUsersTable.repaint();
                revalidate();
                repaint();
                LOGGER.info("Active users table updated with " + tableModel.getRowCount() + " rows");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error updating active users table", e);
                JOptionPane.showMessageDialog(this, "Error updating table: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}