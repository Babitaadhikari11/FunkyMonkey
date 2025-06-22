package demogame.view;

import demogame.controller.BananaController;
import demogame.controller.MonkeyController;
import demogame.controller.ObstacleController;
import demogame.controller.ScoreController;
import demogame.model.Monkey;
import demogame.model.Obstacle;
import demogame.model.TutorialOverlay;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {
    // Constants
    private static final int PANEL_WIDTH = 1200;
    private static final int PANEL_HEIGHT = 800;
    private static final int GROUND_LEVEL = 750;
    private static final int MONKEY_WIDTH = 120;
    private static final int MONKEY_HEIGHT = 120;
    private static final int MONKEY_MIN_X = 150;
    private static final int MONKEY_MAX_X = 350;
    private static final int BG_SPEED = 2;
    private static final int FRAME_RATE = 60;
    private static final int FRAME_DELAY = 1000 / FRAME_RATE;
    private static final boolean SHOW_COLLISION_BOXES = true;
    private static final int PLAY_LEVEL = GROUND_LEVEL - 30;
    private static final float PARALLAX_FACTOR = 0.5f;
    private static final int MONKEY_OFFSET = 80;

    // Game state
    private boolean isPaused;
    private boolean isGameOver;
    private float gameSpeed = 1.0f;
    private boolean showControls = true;
    private float controlsAlpha = 1.0f;
    private boolean tutorialActive = true;

    // UI Components
    private Timer controlsFadeTimer;
    private JButton quitButton;
    private TutorialOverlay tutorial;
    private GameView gameView;

    // Game objects
    private Timer gameLoop;
    private Monkey monkey;
    private MonkeyController monkeyController;
    private ObstacleController obstacleController;
    private BananaController bananaController;
    private BananaView bananaView;
    private SpriteManager spriteManager;
    private ScoreController scoreController;
    private Timer notificationTimer;
    private String notificationMessage;
    private boolean showNotification;
    private Clip notificationSound;

    private static final Logger LOGGER = Logger.getLogger(GamePanel.class.getName());

    // Background images
    private Image jungle1;
    private Image jungle2;
    private Image obstacleImage;
    private int bgX1;
    private int bgX2;

    public GamePanel(GameView gameView) {
        this.gameView = gameView;
        initializePanel();
        tutorial = new TutorialOverlay();
        loadResources();
        setupGame();
        startGameLoop();
    }

    public BananaController getBananaController() {
        return bananaController;
    }

    private void initializePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);
        
        scoreController = new ScoreController(gameView.getGameController().getUserId(), gameView.getScoreLabel());
        bananaController = new BananaController(PANEL_WIDTH, GROUND_LEVEL, scoreController);
        bananaController.addScoreListener(newScore -> {
            if (gameView != null) {
                LOGGER.info("Score update from banana: " + newScore);
                gameView.updateScore(newScore);
            }
        });
        
        bananaView = new BananaView();
        
        quitButton = new JButton("Quit Game");
        quitButton.setFont(new Font("Arial", Font.BOLD, 14));
        quitButton.addActionListener(e -> {
            if (gameView != null) {
                gameView.dispose();
            } else {
                System.exit(0);
            }
        });
    }

    private void loadResources() {
        try {
            java.net.URL bg1Url = getClass().getResource("/resources/Bg1.jpg");
            jungle1 = bg1Url != null ? new ImageIcon(bg1Url).getImage() : null;
            if (jungle1 == null) LOGGER.log(Level.WARNING, "Background image Bg1.jpg not found");
            java.net.URL bg2Url = getClass().getResource("/resources/Bg2.jpg");
            jungle2 = bg2Url != null ? new ImageIcon(bg2Url).getImage() : null;
            if (jungle2 == null) LOGGER.log(Level.WARNING, "Background image Bg2.jpg not found");
            java.net.URL obstacleUrl = getClass().getResource("/resources/obstacle.png");
            obstacleImage = obstacleUrl != null ? new ImageIcon(obstacleUrl).getImage() : null;
            if (obstacleImage == null) LOGGER.log(Level.WARNING, "Obstacle image obstacle.png not found");
            
            bgX1 = 0;
            bgX2 = jungle1 != null ? jungle1.getWidth(null) : PANEL_WIDTH;

            spriteManager = new SpriteManager();
            
            java.net.URL soundURL = getClass().getResource("/resources/notification.wav");
            if (soundURL != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
                notificationSound = AudioSystem.getClip();
                notificationSound.open(audioIn);
            } else {
                LOGGER.log(Level.WARNING, "Notification sound file not found: /resources/notification.wav");
            }
            
            LOGGER.info("Resources loaded successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading resources", e);
        }
    }

    private void setupGame() {
        int monkeyStartX = 50;
        int monkeyStartY = GROUND_LEVEL - MONKEY_HEIGHT;
        monkey = new Monkey(monkeyStartX, monkeyStartY);

        monkeyController = new MonkeyController(monkey);
        monkeyController.setGamePanel(this);
        obstacleController = new ObstacleController(PANEL_WIDTH, PLAY_LEVEL, this);

        addKeyListener(monkeyController);
        addKeyListener(new GameKeyListener());
        startNotificationTimer();
    }
private void startNotificationTimer() {
    if (notificationTimer != null) {
        try {
            notificationTimer.cancel();
            LOGGER.info("Existing notification timer canceled");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error canceling notification timer", e);
        }
        notificationTimer = null;
    }
    try {
        notificationTimer = new Timer();
        notificationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isPaused && !isGameOver && !tutorialActive) {
                    notificationMessage = gameView.getGameController().getRandomNotification();
                    if (notificationMessage != null) {
                        showNotification = true;
                        if (notificationSound != null) {
                            notificationSound.setFramePosition(0);
                            notificationSound.start();
                        }
                        SwingUtilities.invokeLater(() -> repaint());
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                showNotification = false;
                                SwingUtilities.invokeLater(() -> repaint());
                            }
                        }, 5000);
                        LOGGER.info("Notification displayed: " + notificationMessage);
                    } else {
                        LOGGER.warning("No notification message available");
                    }
                } else {
                    LOGGER.info("Notification skipped: Paused=" + isPaused + ", GameOver=" + isGameOver + ", TutorialActive=" + tutorialActive);
                }
            }
        }, 10_000, 120_000); // Changed initial delay from 30_000 to 10_000
        LOGGER.info("Notification timer started with initial delay: 10 seconds");
    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error starting notification timer", e);
    }
}
     

private void startGameLoop() {
    if (gameLoop != null) {
        try {
            gameLoop.cancel();
            gameLoop.purge();
            LOGGER.info("Existing game loop timer canceled and purged");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error canceling existing game loop timer", e);
        }
        gameLoop = null;
    }
    try {
        gameLoop = new Timer();
        gameLoop.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isPaused && !isGameOver) {
                    updateGame();
                }
                repaint();
            }
        }, 0, FRAME_DELAY);
        LOGGER.info("Game loop timer started with frame delay: " + FRAME_DELAY + "ms");
    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error starting game loop timer", e);
    }
}

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isPaused && !isGameOver) {
            updateGame();
        }
        repaint();
    }

    private void updateGame() {
        if (!tutorialActive) {
            updateBackground();
            updateMonkey();
            updateObstacles();
            updateBananas();
            checkCollisions();
        }
    }
    

    private void updateBackground() {
        if (jungle1 == null || jungle2 == null) {
            LOGGER.log(Level.WARNING, "Cannot update background: images not loaded");
            return;
        }
        float speed = BG_SPEED * gameSpeed;
        bgX1 -= speed;
        bgX2 -= speed;

        if (jungle1 != null && bgX1 + jungle1.getWidth(null) <= 0) {
            bgX1 = bgX2 + PANEL_WIDTH;
        }
        if (jungle2 != null && bgX2 + jungle2.getWidth(null) <= 0) {
            bgX2 = bgX1 + PANEL_WIDTH;
        }
    }

    private void drawBackground(Graphics2D g2d) {
        if (jungle1 != null && jungle2 != null) {
            g2d.drawImage(jungle1, bgX1, 0, PANEL_WIDTH, PANEL_HEIGHT, null);
            g2d.drawImage(jungle2, bgX2, 0, PANEL_WIDTH, PANEL_HEIGHT, null);
        } else {
            LOGGER.log(Level.WARNING, "Cannot draw background: images not loaded");
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        }
    }

    private void drawGround(Graphics2D g2d) {
        g2d.setColor(Color.GREEN);
        // g2d.drawLine(0, GROUND_LEVEL, PANEL_WIDTH, GROUND_LEVEL);
    }

    private void drawGameObjects(Graphics2D g2d) {
        drawMonkey(g2d);
        drawObstacles(g2d);
        bananaView.drawBananas(g2d, bananaController.getBananas());
    }

    private void drawMonkey(Graphics2D g2d) {
        if (monkey != null && spriteManager != null) {
            BufferedImage sprite = spriteManager.getSprite(monkey.getCurrentFrameNumber());
            if (sprite != null) {
                int drawX = monkey.getX();
                int drawY = monkey.getY();

                if (monkey.isFacingRight()) {
                    g2d.drawImage(sprite, drawX, drawY, MONKEY_WIDTH, MONKEY_HEIGHT, null);
                } else {
                    g2d.drawImage(sprite, drawX + MONKEY_WIDTH, drawY, 
                                -MONKEY_WIDTH, MONKEY_HEIGHT, null);
                }
            }
        }
    }

    private void drawObstacles(Graphics2D g2d) {
        if (obstacleImage != null) {
            if (obstacleController == null || obstacleController.getObstacles() == null) {
                LOGGER.log(Level.WARNING, "Cannot draw obstacles: obstacleController or obstacles list is null");
                return;
            }
            for (Obstacle obstacle : obstacleController.getObstacles()) {
                if (obstacle.isActive()) {
                    g2d.drawImage(obstacleImage, 
                                obstacle.getX(), 
                                obstacle.getY(), 
                                obstacle.getWidth(), 
                                obstacle.getHeight(), 
                                null);
                }
            }
        } else {
            LOGGER.log(Level.WARNING, "Cannot draw obstacles: obstacleImage not loaded");
        }
    }

    private void drawNotificationOverlay(Graphics2D g2d) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
        g2d.setColor(new Color(0, 0, 0, 200));
        int popupWidth = 400;
        int popupHeight = 100;
        int x = (PANEL_WIDTH - popupWidth) / 2;
        int y = 100;
        g2d.fillRoundRect(x, y, popupWidth, popupHeight, 20, 20);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(notificationMessage);
        int textX = x + (popupWidth - textWidth) / 2;
        int textY = y + popupHeight / 2 + fm.getAscent() / 2;
        g2d.drawString(notificationMessage, textX, textY);
    }

    private void setupGraphics(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        setupGraphics(g2d);
        
        drawBackground(g2d);
        drawGround(g2d);
        drawGameObjects(g2d);
        drawUI(g2d);
        
        if (showNotification && notificationMessage != null) {
            drawNotificationOverlay(g2d);
        }
        
        if (tutorial.isVisible()) {
            drawTutorialOverlay(g2d);
        }
    }

    private void drawTutorialOverlay(Graphics2D g2d) {
        if (tutorial.isVisible()) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));

            String[] instructions = tutorial.getInstructions();
            int startY = PANEL_HEIGHT / 2 - (instructions.length * 30);
            
            for (int i = 0; i < instructions.length; i++) {
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(instructions[i]);
                int x = (PANEL_WIDTH - textWidth) / 2;
                g2d.drawString(instructions[i], x, startY + (i * 40));
            }

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }

    private void drawUI(Graphics2D g2d) {
        if (gameView == null) {
            bananaView.drawScore(g2d, 
                               bananaController.getScore(),
                               bananaController.getBananasCollected());
        }
        
        if (isPaused) {
            drawPauseOverlay(g2d);
        }
        if (isGameOver) {
            drawGameOverOverlay(g2d);
        }
    }

    private void drawPauseOverlay(Graphics2D g2d) {
        // Implement pause overlay if needed
    }

    private void drawGameOverOverlay(Graphics2D g2d) {
        // Implement game over overlay if needed
    }
    
    private void updateMonkey() {
        if (monkey != null) {
            monkey.update();
            
            if (monkey.getX() < MONKEY_MIN_X) {
                monkey.setX(MONKEY_MIN_X);
                monkey.setVelocityX(0);
            }
            if (monkey.getX() > MONKEY_MAX_X) {
                monkey.setX(MONKEY_MAX_X);
                monkey.setVelocityX(0);
            }
            
            int groundY = GROUND_LEVEL - MONKEY_HEIGHT - 50;
            if (monkey.getY() > groundY) {
                monkey.setY(groundY);
                monkey.setOnGround(true);
                monkey.setVelocityY(0);
            } else {
                monkey.setOnGround(false);
            }
        }
    }

    private void updateObstacles() {
        if (obstacleController != null) {
            obstacleController.update();
        }
    }

    private void updateBananas() {
        if (bananaController != null) {
            bananaController.update(obstacleController.getObstacles());
            if (gameView != null) {
                gameView.updateScore(bananaController.getScore());
            }
        }
    }

    private void checkCollisions() {
        if (obstacleController != null && obstacleController.checkCollisions(monkey)) {
            handleGameOver();
        }

        if (bananaController != null) {
            bananaController.checkCollisions(monkey);
        }
    }

    private void handleGameOver() {
        isGameOver = true;
        final int finalScore = bananaController.getScore();
        
        if (gameView != null) {
            gameView.updateScore(finalScore);
            SwingUtilities.invokeLater(() -> {
                int choice = JOptionPane.showConfirmDialog(
                    gameView,
                    "Game Over! Score: " + finalScore + "\nTry again?",
                    "Game Over",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (choice == JOptionPane.YES_OPTION) {
                    restartGame();
                } else {
                    gameView.dispose();
                }
            });
        } else {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Game Over! Score: " + finalScore + "\nTry again?",
                "Game Over",
                JOptionPane.YES_NO_OPTION
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                restartGame();
            } else {
                System.exit(0);
            }
        }
    }

    public void restartGame() {
        isGameOver = false;
        isPaused = false;
        gameSpeed = 1.0f;
        tutorialActive = true;
        tutorial = new TutorialOverlay();

        int monkeyStartX = 50;
        int monkeyStartY = PLAY_LEVEL - MONKEY_HEIGHT - 50;
        monkey = new Monkey(monkeyStartX, monkeyStartY);
        monkeyController = new MonkeyController(monkey);
        addKeyListener(monkeyController);

        obstacleController.restart();
        bananaController.restart();

        startNotificationTimer();

        requestFocusInWindow();
    }

    public void togglePause() {
        isPaused = !isPaused;
        if (gameView != null) {
            gameView.showPauseMenu();
        }
    }
    private void drawNotificationOverlay(Graphics g) {
    if (showNotification && notificationMessage != null) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(255, 255, 0, 200));
        int width = 300;
        int height = 100;
        int x = (getWidth() - width) / 2;
        int y = getHeight() / 4;
        g2d.fillRoundRect(x, y, width, height, 20, 20);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(notificationMessage);
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(notificationMessage, textX, textY);
    }
}

 private class GameKeyListener extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent e) {
        if (tutorial.isVisible()) {
            tutorial.setVisible(false);
            tutorialActive = false;
            LOGGER.info("Tutorial dismissed");
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_P) {
            togglePause();
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE && gameView != null) {
            gameView.showPauseMenu();
        }
    }
}

    public boolean isTutorialActive() {
        return tutorialActive;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public int getCurrentScore() {
        return bananaController.getScore();
    }

    public int getBananasCollected() {
        return bananaController.getBananasCollected();
    }

    public void cleanup() {
    if (gameLoop != null) {
        try {
            gameLoop.cancel();
            gameLoop.purge();
            LOGGER.info("Game loop timer canceled and purged");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error canceling game loop timer", e);
        }
        gameLoop = null;
    }
    if (notificationTimer != null) {
        try {
            notificationTimer.cancel();
            LOGGER.info("Notification timer canceled");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error canceling notification timer", e);
        }
        notificationTimer = null;
    }
    if (scoreController != null) {
        try {
            scoreController.dispose();
            LOGGER.info("ScoreController disposed");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error disposing ScoreController", e);
        }
    }
    if (notificationSound != null) {
        try {
            notificationSound.close();
            LOGGER.info("Notification sound closed");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error closing notification sound", e);
        }
        notificationSound = null;
    }
    LOGGER.info("GamePanel cleanup completed");
}
}