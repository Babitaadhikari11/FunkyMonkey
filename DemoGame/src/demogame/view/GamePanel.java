// package demogame.view;

// import java.awt.*;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
// import javax.swing.*;


// public class GamePanel extends JPanel implements ActionListener {

//     private Image jungle1;
//     private Image jungle2;
//     private int x1 = 0;
//     private int x2;
//     private Timer timer;
//     private final int SPEED = 2;

//     public GamePanel() {
//         setPreferredSize(new Dimension(1200, 800)); // Match your JFrame
//         setDoubleBuffered(true);
//         loadImages();
//         x2 = jungle1.getWidth(null); // Start second image right after first

//         timer = new Timer(25, this); // 60 FPS
//         timer.start();
//     }

//     private void loadImages() {
//         jungle1 = new ImageIcon(getClass().getResource("/resources/Bg1.jpg")).getImage();
//         jungle2 = new ImageIcon(getClass().getResource("/resources/Bg2.jpg")).getImage();

//         if (jungle1 == null || jungle2 == null) {
//         System.err.println("Images not loaded! Check the file paths and resource locations.");
//     }
//     }

//     @Override
//     protected void paintComponent(Graphics g) {
//         super.paintComponent(g);
//         g.drawImage(jungle1, x1, 0, this);
//         g.drawImage(jungle2, x2, 0, this);
//     }

//     @Override
//     public void actionPerformed(ActionEvent e) {
//         x1 -= SPEED;
//         x2 -= SPEED;

//         int width = jungle1.getWidth(null);

//         if (x1 + width < 0) x1 = x2 + width;
//         if (x2 + width < 0) x2 = x1 + width;

//         repaint();
//     }
//     public static void main(String[] args) {
//     JFrame testFrame = new JFrame("Test Jungle Background");
//     testFrame.setSize(1200, 800);
//     testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//     testFrame.setLocationRelativeTo(null);
//     testFrame.add(new GamePanel());
//     testFrame.setVisible(true);
//     }
    
    

// }
package demogame.view;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import demogame.model.Monkey;
import demogame.model.Obstacle;
import demogame.controller.BananaController;
import demogame.controller.MonkeyController;
import demogame.controller.ObstacleController;

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
     private static final int PLAY_LEVEL = GROUND_LEVEL - 30;  // Raised level for both monkey and obstacles
    
    private static final float PARALLAX_FACTOR = 0.5f;
     private static final int MONKEY_OFFSET = 80; // New constant for monkey's height above ground
     //for button press
     // Add after your existing constants
    private static final String CONTROLS_TEXT = """
    Game Controls:
    ← → Arrow Keys: Move
    SPACE: Jump
    P: Pause
    ESC: Menu
    H: Show/Hide Controls
    """;

private boolean showControls = true;
private float controlsAlpha = 1.0f;
private Timer controlsFadeTimer;
// for quit button
 private JButton quitButton;

    // Game state
    private boolean isPaused;
    private boolean isGameOver;
    private float gameSpeed = 1.0f;

    // Game objects
    private Timer gameLoop;
    private Monkey monkey;
    private MonkeyController monkeyController;
    private ObstacleController obstacleController;
    private BananaController bananaController;
    private BananaView bananaView;
    private SpriteManager spriteManager;

    // Background
    private Image jungle1;
    private Image jungle2;
    private Image obstacleImage;
    private int bgX1;
    private int bgX2;

    public GamePanel() {
        initializePanel();
        loadResources();
        setupGame();
        startGameLoop();
    }

    private void initializePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);
        
        // Initialize controllers
        bananaController = new BananaController(PANEL_WIDTH, GROUND_LEVEL);
        bananaView = new BananaView();
        //setup of quit button
        quitButton = new JButton("Quit Game");
        quitButton.setFont(new Font("Arial", Font.BOLD, 14));
        
    }

    private void loadResources() {
        try {
            // Load background images
            jungle1 = new ImageIcon(getClass().getResource("/resources/Bg1.jpg")).getImage();
            jungle2 = new ImageIcon(getClass().getResource("/resources/Bg2.jpg")).getImage();
            obstacleImage = new ImageIcon(getClass().getResource("/resources/obstacle.png")).getImage();
            
            // Set initial background positions
            bgX1 = 0;
            bgX2 = jungle1.getWidth(null);

            // Initialize sprite manager
            spriteManager = new SpriteManager();
            
            System.out.println("Resources loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading resources: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupGame() {
        // Create monkey
        int monkeyStartX =50;
        int monkeyStartY = GROUND_LEVEL - MONKEY_HEIGHT -50;
        monkey = new Monkey(monkeyStartX, monkeyStartY);

        // Setup controllers
        monkeyController = new MonkeyController(monkey);
        obstacleController = new ObstacleController(PANEL_WIDTH, PLAY_LEVEL, this);

        // Add input listeners
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
    if (monkey != null) {
        monkey.update();
        
        // Horizontal boundaries
        if (monkey.getX() < MONKEY_MIN_X) {
            monkey.setX(MONKEY_MIN_X);
            monkey.setVelocityX(0);
        }
        if (monkey.getX() > MONKEY_MAX_X) {
            monkey.setX(MONKEY_MAX_X);
            monkey.setVelocityX(0);
        }
        
        // Ground collision with adjusted height
        int groundY = GROUND_LEVEL - MONKEY_HEIGHT - 50; // Raised slightly
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
        }
    }

    private void checkCollisions() {
        // Check obstacle collisions
        if (obstacleController != null && obstacleController.checkCollisions(monkey)) {
            handleGameOver();
        }

        // Check banana collisions
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
        
        if (SHOW_COLLISION_BOXES) {
            drawDebugInfo(g2d);
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
        g2d.drawLine(0, GROUND_LEVEL, PANEL_WIDTH, GROUND_LEVEL);
    }

    private void drawGameObjects(Graphics2D g2d) {
        drawMonkey(g2d);
        drawObstacles(g2d);
        bananaView.drawBananas(g2d, bananaController.getBananas());
    }
    //pause overlay
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
            for (Obstacle obstacle : obstacleController.getObstacles()) {
                if (obstacle.isActive()) {
                    // Draw obstacle sitting on ground
                    g2d.drawImage(obstacleImage, 
                                obstacle.getX(), 
                                obstacle.getY(), 
                                obstacle.getWidth(), 
                                obstacle.getHeight(), 
                                null);
                    
                    if (SHOW_COLLISION_BOXES) {
                        g2d.setColor(Color.RED);
                        // Adjust collision box to match actual bounds
                        g2d.drawRect(obstacle.getX() + 10, 
                                   obstacle.getY() + 10, 
                                   obstacle.getWidth() - 20, 
                                   obstacle.getHeight() - 20);
                    }
                }
            }
        }
        }
    private void drawUI(Graphics2D g2d) {
        bananaView.drawScore(g2d, 
                           bananaController.getScore(),
                           bananaController.getBananasCollected());
        
        if (isPaused) {
            drawPauseOverlay(g2d);
        }
        if (isGameOver) {
            drawGameOverOverlay(g2d);
        }
    }

    private void drawPauseOverlay(Graphics2D g2d) {
        // Implement pause overlay
    }

    private void drawGameOverOverlay(Graphics2D g2d) {
        // Implement game over overlay
    }

    private void drawDebugInfo(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        if (monkey != null) {
            g2d.drawString("Monkey Position: " + monkey.getX() + ", " + monkey.getY(), 10, 20);
            g2d.drawString("Frame: " + monkey.getCurrentFrameNumber(), 10, 40);
            g2d.drawString("Ground Level: " + GROUND_LEVEL, 10, 60);
        }
    }

    private void handleGameOver() {
        isGameOver = true;
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Game Over! Score: " + bananaController.getScore() + "\nTry again?",
            "Game Over",
            JOptionPane.YES_NO_OPTION
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            System.exit(0);
        }
    }  public void restartGame() {
        // Reset game state
        isGameOver = false;
        isPaused = false;
        gameSpeed = 1.0f;

        // Reset monkey at new play level
        int monkeyStartX=50;
        int monkeyStartY = PLAY_LEVEL - MONKEY_HEIGHT -50;
        monkey = new Monkey(monkeyStartX, monkeyStartY);
        monkeyController = new MonkeyController(monkey);
        addKeyListener(monkeyController);

        // Reset controllers
        obstacleController.restart();
        bananaController.restart();

        requestFocusInWindow();
    }


    public void togglePause() {
        isPaused = !isPaused;
    }

    private class GameKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_P) {
                togglePause();
            }
        }
    }
}