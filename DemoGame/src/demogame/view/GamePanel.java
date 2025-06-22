package demogame.view;

import demogame.controller.BananaController;
import demogame.controller.MonkeyController;
import demogame.controller.ObstacleController;
import demogame.controller.ScoreController;
import demogame.model.Monkey;
import demogame.model.Obstacle;
import demogame.model.TutorialOverlay;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener, Obstacle.CollisionListener {
    // Constants
    private static final int PANEL_WIDTH = 1200;
    private static final int PANEL_HEIGHT = 800;
    private static final int GROUND_LEVEL = 750;
    private static final int MONKEY_WIDTH = 120;
    private static final int MONKEY_HEIGHT = 120;
    private static final int MONKEY_MIN_X = 150;
    private static final int MONKEY_MAX_X = 350;
    private static final int BG_SPEED = 3;
    private static final int FRAME_RATE = 60;
    private static final int FRAME_DELAY = 800 / FRAME_RATE;
    private static final boolean SHOW_COLLISION_BOXES = false; // Hide all debug info
    private static final int PLAY_LEVEL = GROUND_LEVEL - 30;
    private static final float PARALLAX_FACTOR = 0.5f;
    private static final int MONKEY_OFFSET = 60;

    // Game state
    private boolean isPaused;
    private boolean isGameOver;
    private float gameSpeed = 1.0f;
    private boolean showControls = true;
    private float controlsAlpha = 1.0f;
    private boolean tutorialActive = true;

    // UI Components
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
    private static final Logger LOGGER = Logger.getLogger(GamePanel.class.getName());

    // Background
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
        requestFocusInWindow();
        LOGGER.info("GamePanel initialized, tutorialActive=" + tutorialActive);
    }

    public BananaController getBananaController() {
        return bananaController;
    }

    private void initializePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);
        setLayout(null); // Use null layout for absolute positioning of quitButton

        try {
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
            quitButton.setBounds(PANEL_WIDTH - 140, 20, 120, 40); // Position at top-right
            quitButton.addActionListener(e -> {
                if (gameView != null) {
                    gameView.dispose();
                } else {
                    System.exit(0);
                }
            });
            add(quitButton); // Add button to panel
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing panel", e);
        }
    }

    private void loadResources() {
        try {
            jungle1 = new ImageIcon(getClass().getResource("/resources/Bg1.jpg")).getImage();
            jungle2 = new ImageIcon(getClass().getResource("/resources/Bg2.jpg")).getImage();
            obstacleImage = new ImageIcon(getClass().getResource("/resources/obstacle.png")).getImage();

            bgX1 = 0;
            bgX2 = jungle1.getWidth(null);

            spriteManager = new SpriteManager();

            LOGGER.info("Resources loaded successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading resources", e);
        }
    }

    private void setupGame() {
        try {
            int monkeyStartX = 50;
            int monkeyStartY = GROUND_LEVEL - MONKEY_HEIGHT - 50;
            monkey = new Monkey(monkeyStartX, monkeyStartY);

            monkeyController = new MonkeyController(monkey);
            monkeyController.setGamePanel(this);
            obstacleController = new ObstacleController(PANEL_WIDTH, PLAY_LEVEL, this);

            for (Obstacle obstacle : obstacleController.getObstacles()) {
                obstacle.setCollisionListener(this);
            }

            addKeyListener(monkeyController);
            addKeyListener(new GameKeyListener());
            LOGGER.info("Game setup completed");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error setting up game", e);
        }
    }

    private void startGameLoop() {
        try {
            gameLoop = new Timer(FRAME_DELAY, this);
            gameLoop.start();
            LOGGER.info("Game loop started, running=" + gameLoop.isRunning());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error starting game loop", e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isPaused && !isGameOver && !tutorialActive) {
            updateGame();
        }
        repaint();
    }

    private void updateGame() {
        if (monkey == null || obstacleController == null || bananaController == null) {
            LOGGER.severe("Game objects not initialized properly");
            return;
        }
        updateBackground();
        updateMonkey();
        updateObstacles();
        updateBananas();
        checkCollisions();
    }

    private void updateBackground() {
        float speed = BG_SPEED * gameSpeed;
        bgX1 -= speed;
        bgX2 -= speed;

        if (bgX1 + PANEL_WIDTH <= 0) {
            bgX1 = bgX2 + PANEL_WIDTH;
        }
        if (bgX2 + PANEL_WIDTH <= 0) {
            bgX2 = bgX1 + PANEL_WIDTH;
        }
    }

    private void updateMonkey() {
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
        float tolerance = 1.0f;
        if (monkey.getY() >= groundY - tolerance) {
            monkey.setY(groundY);
            monkey.setOnGround(true);
            monkey.setVelocityY(0);
        } else {
            monkey.setOnGround(false);
        }
    }

    private void updateObstacles() {
        obstacleController.update();
        for (Obstacle obstacle : obstacleController.getObstacles()) {
            if (obstacle.getCollisionListener() == null) {
                obstacle.setCollisionListener(this);
            }
        }
    }

    private void updateBananas() {
        bananaController.update(obstacleController.getObstacles());
        if (gameView != null) {
            gameView.updateScore(bananaController.getScore());
        }
    }

    private void checkCollisions() {
        if (!isGameOver && obstacleController != null && monkey != null) {
            if (obstacleController.checkCollisions(monkey)) {
                LOGGER.info("Obstacle collision detected by ObstacleController");
            }
        }

        if (bananaController != null && monkey != null) {
            bananaController.checkCollisions(monkey);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        setupGraphics(g2d);

        drawBackground(g2d);
        drawGround(g2d);
        drawGameObjects(g2d);
        drawUI(g2d);

        if (tutorial.isVisible()) {
            drawTutorialOverlay(g2d);
        }
    }

    private void setupGraphics(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    private void drawBackground(Graphics2D g2d) {
        if (jungle1 != null && jungle2 != null) {
            g2d.drawImage(jungle1, bgX1, 0, PANEL_WIDTH, PANEL_HEIGHT, null);
            g2d.drawImage(jungle2, bgX2, 0, PANEL_WIDTH, PANEL_HEIGHT, null);
        }
    }

    private void drawGround(Graphics2D g2d) {
        g2d.setColor(Color.GREEN);
        // g2d.drawLine(0, GROUND_LEVEL, PANEL_WIDTH, GROUND_LEVEL);
    }

    private void drawGameObjects(Graphics2D g2d) {
        drawMonkey(g2d);
        drawObstacles(g2d);
        if (bananaView != null && bananaController != null) {
            bananaView.drawBananas(g2d, bananaController.getBananas());
        }
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
                    g2d.drawImage(sprite, drawX + MONKEY_WIDTH, drawY, -MONKEY_WIDTH, MONKEY_HEIGHT, null);
                }
            }
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

    private void drawObstacles(Graphics2D g2d) {
        if (obstacleImage != null && obstacleController != null) {
            for (Obstacle obstacle : obstacleController.getObstacles()) {
                if (obstacle.isActive()) {
                    g2d.drawImage(obstacleImage, obstacle.getX(), obstacle.getY(),
                            obstacle.getWidth(), obstacle.getHeight(), null);
                }
            }
        }
    }

    private void drawUI(Graphics2D g2d) {
        if (gameView == null && bananaView != null && bananaController != null) {
            bananaView.drawScore(g2d, bananaController.getScore(), bananaController.getBananasCollected());
        }

        if (isPaused) {
            drawPauseOverlay(g2d);
        }
        if (isGameOver) {
            drawGameOverOverlay(g2d);
        }
    }

    private void drawPauseOverlay(Graphics2D g2d) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 36));
        String pauseText = "Paused - Press P to Resume";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(pauseText);
        g2d.drawString(pauseText, (PANEL_WIDTH - textWidth) / 2, PANEL_HEIGHT / 2);
    }

    private void drawGameOverOverlay(Graphics2D g2d) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 36));
        String gameOverText = "Game Over! Score: " + (bananaController != null ? bananaController.getScore() : 0);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(gameOverText);
        g2d.drawString(gameOverText, (PANEL_WIDTH - textWidth) / 2, PANEL_HEIGHT / 2 - 20);

        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        String restartText = "Press R to Restart or ESC to Quit";
        textWidth = fm.stringWidth(restartText);
        g2d.drawString(restartText, (PANEL_WIDTH - textWidth) / 2, PANEL_HEIGHT / 2 + 20);
    }

    @Override
    public void onObstacleCollision(Obstacle obstacle) {
        if (!isGameOver) {
            LOGGER.info("Obstacle collision at x=" + obstacle.getX() + ", y=" + obstacle.getY());
            handleGameOver();
        }
    }

    private void handleGameOver() {
        isGameOver = true;
        final int finalScore = bananaController != null ? bananaController.getScore() : 0;

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
        int monkeyStartY = GROUND_LEVEL - MONKEY_HEIGHT - 50;
        monkey = new Monkey(monkeyStartX, monkeyStartY);
        monkeyController = new MonkeyController(monkey);
        monkeyController.setGamePanel(this);
        removeKeyListener(monkeyController);
        addKeyListener(monkeyController);

        if (obstacleController != null) {
            obstacleController.restart();
        }
        if (bananaController != null) {
            bananaController.restart();
        }

        requestFocusInWindow();
        LOGGER.info("Game restarted, tutorialActive=" + tutorialActive);
    }

    public void togglePause() {
        if (!isGameOver && !tutorialActive) {
            isPaused = !isPaused;
            if (isPaused && gameView != null) {
                gameView.showPauseMenu();
            } else {
                requestFocusInWindow();
            }
            LOGGER.info("Pause toggled: isPaused=" + isPaused);
        }
    }

    private class GameKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (tutorial.isVisible()) {
                tutorial.setVisible(false);
                tutorialActive = false;
                requestFocusInWindow();
                LOGGER.info("Tutorial dismissed, tutorialActive=" + tutorialActive);
                return;
            }

            if (e.getKeyCode() == KeyEvent.VK_P) {
                togglePause();
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE && gameView != null) {
                togglePause();
            } else if (isGameOver && e.getKeyCode() == KeyEvent.VK_R) {
                restartGame();
            } else if (isGameOver && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                if (gameView != null) {
                    gameView.dispose();
                } else {
                    System.exit(0);
                }
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
        return bananaController != null ? bananaController.getScore() : 0;
    }

    public int getBananasCollected() {
        return bananaController != null ? bananaController.getBananasCollected() : 0;
    }

    public void cleanup() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        if (scoreController != null) {
            scoreController.dispose();
        }
        LOGGER.info("GamePanel cleaned up");
    }
}