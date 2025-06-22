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
    private ScoreController scoreController; // Added ScoreController field
    private static final Logger LOGGER = Logger.getLogger(GamePanel.class.getName()); // Added Logger

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
    }

    public BananaController getBananaController() {
        return bananaController; // Return the BananaController instance
    }

    private void initializePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);
        
        // Initialize ScoreController with userId from GameController and scoreLabel from GameView
        scoreController = new ScoreController(gameView.getGameController().getUserId(), gameView.getScoreLabel());
        // Pass ScoreController to BananaController
        bananaController = new BananaController(PANEL_WIDTH, GROUND_LEVEL, scoreController);
        // Add score listener to connect BananaController to GameView
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
        int monkeyStartX = 50;
        int monkeyStartY = GROUND_LEVEL - MONKEY_HEIGHT ;
        monkey = new Monkey(monkeyStartX, monkeyStartY);

        monkeyController = new MonkeyController(monkey);
        monkeyController.setGamePanel(this);
        obstacleController = new ObstacleController(PANEL_WIDTH, PLAY_LEVEL, this);

        addKeyListener(monkeyController);
        addKeyListener(new GameKeyListener());
    }

    private void startGameLoop() {
        gameLoop = new Timer(FRAME_DELAY, this);
        gameLoop.start();
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        setupGraphics(g2d);
        
        drawBackground(g2d);
        drawGround(g2d);
        drawGameObjects(g2d);
        drawUI(g2d);
        
        // if (SHOW_COLLISION_BOXES) {
        //     drawDebugInfo(g2d);
        // }
        
        if (tutorial.isVisible()) {
            drawTutorialOverlay(g2d);
        }
    }

    private void setupGraphics(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
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
        if (obstacleImage != null) {
            for (Obstacle obstacle : obstacleController.getObstacles()) {
                if (obstacle.isActive()) {
                    g2d.drawImage(obstacleImage, 
                                obstacle.getX(), 
                                obstacle.getY(), 
                                obstacle.getWidth(), 
                                obstacle.getHeight(), 
                                null);
                    
                    // if (SHOW_COLLISION_BOXES) {
                    //     g2d.setColor(Color.RED);
                    //     g2d.drawRect(obstacle.getX() + 10, 
                    //                obstacle.getY() + 10, 
                    //                obstacle.getWidth() - 20, 
                    //                obstacle.getHeight() - 20);
                    // }
                }
            }
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

    // private void drawDebugInfo(Graphics2D g2d) {
    //     g2d.setColor(Color.WHITE);
    //     g2d.setFont(new Font("Arial", Font.PLAIN, 12));
    //     if (monkey != null) {
    //         g2d.drawString("Monkey Position: " + monkey.getX() + ", " + monkey.getY(), 10, 20);
    //         g2d.drawString("Frame: " + monkey.getCurrentFrameNumber(), 10, 40);
    //         g2d.drawString("Ground Level: " + GROUND_LEVEL, 10, 60);
    //     }
    // }

    private void handleGameOver() {
        isGameOver = true;
        final int finalScore = bananaController.getScore();
        
        if (gameView != null) {
            gameView.updateScore(finalScore);
            // Show game over dialog through GameView
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
            // Fallback if GameView is not available
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

        requestFocusInWindow();
    }

    public void togglePause() {
        isPaused = !isPaused;
        if (gameView != null) {
            gameView.showPauseMenu();
        }
    }

    private class GameKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (tutorial.isVisible()) {
                tutorial.setVisible(false);
                tutorialActive = false;
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
            gameLoop.stop();
        }
        if (scoreController != null) {
            scoreController.dispose(); // Cleanup ScoreController
        }
    }
}