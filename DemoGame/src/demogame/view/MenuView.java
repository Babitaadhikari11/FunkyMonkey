package demogame.view;

import demogame.controller.GameController;
import demogame.controller.LoadingController;
import demogame.controller.UserUpdateController;
import demogame.util.BackgroundMusicPlayer;
import demogame.dao.UserDao;
import demogame.view.LoadingView;
import demogame.view.UserUpdateView;
import demogame.model.UserData;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class MenuView extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(MenuView.class.getName());
    private final GameController gameController;
    private JLabel welcomeLabel;
    private JButton startButton;
    private JButton updateButton;
    private JButton quitButton;
    private JToggleButton soundButton;
    private Timer animationTimer;
    private int welcomeX = 20;
    private boolean movingRight = true;

    public MenuView(GameController gameController) {
        this.gameController = gameController;
        String username = fetchUsername(gameController.getUserId());
        setTitle("DemoGame - Main Menu");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JLayeredPane layeredPane = new JLayeredPane();
        getContentPane().add(layeredPane);

        try {
            ImageIcon bgIcon = new ImageIcon(getClass().getResource("/resources/menu.jpeg"));
            Image scaled = bgIcon.getImage().getScaledInstance(1200, 800, Image.SCALE_SMOOTH);
            JLabel background = new JLabel(new ImageIcon(scaled));
            background.setBounds(0, 0, 1200, 800);
            layeredPane.add(background, Integer.valueOf(0));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Background image not found: /resources/menu.jpeg", e);
            setBackground(Color.DARK_GRAY);
        }

        welcomeLabel = new JLabel("Welcome, " + username + "!");
        try {
            welcomeLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        } catch (Exception e) {
            LOGGER.warning("Failed to load font Comic Sans MS, using default");
            welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        }
        welcomeLabel.setForeground(Color.RED);
        welcomeLabel.setBounds(20, 20, 300, 40);
        layeredPane.add(welcomeLabel, Integer.valueOf(1));

        animationTimer = new Timer(50, e -> animateWelcomeMessage());
        animationTimer.start();

        setupButtons(layeredPane);
        LOGGER.info("MenuView initialized for userId: " + gameController.getUserId());
    }

    private String fetchUsername(int userId) {
        try {
            UserDao userDao = new UserDao();
            String username = userDao.getUsernameById(userId);
            if (username != null && !username.isEmpty()) {
                LOGGER.info("Fetched username: " + username + " for userId: " + userId);
                return username;
            } else {
                LOGGER.warning("No username found for userId: " + userId + ", using default");
                return "Player";
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error fetching username for userId: " + userId, e);
            SwingUtilities.invokeLater(() -> 
                JOptionPane.showMessageDialog(null,
                    "Error fetching username: Database issue. Using default name.",
                    "Database Error",
                    JOptionPane.WARNING_MESSAGE)
            );
            return "Player";
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error fetching username for userId: " + userId, e);
            return "Player";
        }
    }

    private void setupButtons(JLayeredPane layeredPane) {
        startButton = createImageButton("/resources/buttons.png", "Start Game", 280);
        updateButton = createImageButton("/resources/buttons.png", "Update Profile", 400);
        quitButton = createImageButton("/resources/buttons.png", "Quit Game", 520);

        int buttonX = (1200 - 300) / 2;
        startButton.setBounds(buttonX, 280, 300, 80);
        updateButton.setBounds(buttonX, 400, 300, 80);
        quitButton.setBounds(buttonX, 520, 300, 80);

        startButton.addActionListener(e -> {
            LOGGER.info("Start button clicked for userId: " + gameController.getUserId());
            setVisible(false); // Hide MenuView
            try {
                LoadingView loadingView = new LoadingView();
                LoadingController loadingController = new LoadingController(loadingView, gameController.getUserId());
                LOGGER.info("LoadingView and LoadingController initialized for userId: " + gameController.getUserId());
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error initializing loading screen for userId: " + gameController.getUserId(), ex);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null,
                        "Error starting game: " + ex.getMessage(),
                        "Start Game Error",
                        JOptionPane.ERROR_MESSAGE);
                    setVisible(true); // Show MenuView again
                });
            }
        });

        updateButton.addActionListener(e -> {
            LOGGER.info("Update Profile button clicked for userId: " + gameController.getUserId());
            UserData user = gameController.getUserData();
            if (user != null) {
                setVisible(false);
                try {
                    UserUpdateView updateView = new UserUpdateView(user.getUsername(), user.getEmail());
                    new UserUpdateController(updateView, user);
                    updateView.setVisible(true);
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error opening UserUpdateView for userId: " + gameController.getUserId(), ex);
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null,
                            "Error opening profile update: " + ex.getMessage(),
                            "Update Error",
                            JOptionPane.ERROR_MESSAGE);
                        setVisible(true);
                    });
                }
            } else {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null,
                        "Error: Unable to load user data.",
                        "Update Error",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        });

        quitButton.addActionListener(e -> {
            LOGGER.info("Quit button clicked for userId: " + gameController.getUserId());
            dispose();
            System.exit(0);
        });

        layeredPane.add(startButton, Integer.valueOf(1));
        layeredPane.add(updateButton, Integer.valueOf(1));
        layeredPane.add(quitButton, Integer.valueOf(1));

        soundButton = new JToggleButton("Sound: ON");
        boolean musicOn = BackgroundMusicPlayer.isPlaying();
        soundButton.setSelected(!musicOn);
        soundButton.setText(musicOn ? "Sound: ON" : "Sound: OFF");

        soundButton.setBounds(1050, 700, 120, 40);
        try {
            soundButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        } catch (Exception e) {
            LOGGER.warning("Failed to load font Comic Sans MS, using default");
            soundButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        }
        soundButton.setFocusPainted(false);

        soundButton.addActionListener(e -> {
            if (soundButton.isSelected()) {
                BackgroundMusicPlayer.stop();
                soundButton.setText("Sound: OFF");
                LOGGER.info("Sound turned OFF for userId: " + gameController.getUserId());
            } else {
                BackgroundMusicPlayer.playLoop("C:\\Users\\acer\\Desktop\\FunkyMonkey\\DemoGame\\src\\resources\\music1.wav");
                soundButton.setText("Sound: ON");
                LOGGER.info("Sound turned ON for userId: " + gameController.getUserId());
            }
        });

        layeredPane.add(soundButton, JLayeredPane.POPUP_LAYER);
    }

    private JButton createImageButton(String imagePath, String text, int yPosition) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                Font buttonFont;
                try {
                    buttonFont = new Font("Comic Sans MS", Font.BOLD, 24);
                } catch (Exception e) {
                    LOGGER.warning("Failed to load font Comic Sans MS, using default");
                    buttonFont = new Font("SansSerif", Font.BOLD, 24);
                }
                g2d.setFont(buttonFont);
                FontMetrics fm = g2d.getFontMetrics(buttonFont);
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                int textX = (getWidth() - textWidth) / 2;
                int textY = (getHeight() - textHeight) / 2 + fm.getAscent();
                if (text.equals("Quit Game")) {
                    g2d.setColor(Color.RED);
                } else {
                    g2d.setColor(Color.BLACK);
                }
                g2d.drawString(text, textX, textY);
            }
        };

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
            Image img = icon.getImage().getScaledInstance(300, 80, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Button image not found: " + imagePath, e);
        }

        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void animateWelcomeMessage() {
        if (movingRight) {
            welcomeX += 2;
            if (welcomeX >= 280) movingRight = false;
        } else {
            welcomeX -= 2;
            if (welcomeX <= 20) movingRight = true;
        }
        welcomeLabel.setBounds(welcomeX, 20, 300, 40);
    }

    public JButton getStartButton() { return startButton; }
    public JButton getUpdateButton() { return updateButton; }
    public JButton getQuitButton() { return quitButton; }
    public JToggleButton getSoundButton() { return soundButton; }

    @Override
    public void dispose() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        super.dispose();
        LOGGER.info("MenuView disposed for userId: " + gameController.getUserId());
    }
}