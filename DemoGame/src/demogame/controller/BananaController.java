package demogame.controller;
import demogame.model.Banana;
import demogame.model.Monkey;
import demogame.model.Obstacle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.awt.Rectangle;
public class BananaController {
      // Constants
    private static final int MAX_BANANAS = 5;
    private static final int BANANA_WIDTH = 30;
    private static final int BANANA_HEIGHT = 30;
    private static final int SPAWN_OFFSET = 200;
    private static final int VERTICAL_RANGE = 200;
    private static final int COLLISION_INSET = 30;
    private static final int MONKEY_DEFAULT_SIZE = 120; // Default monkey size
    // Add these constants
    private static final int MONKEY_LEVEL_CHANCE = 30; // 30% chance for banana at monkey level
    private static final int MONKEY_HEIGHT = 120;      // Match monkey's height


    private ArrayList<Banana> bananas;
    private Random random;
    private final int PANEL_WIDTH;
    private final int GROUND_LEVEL;
    private int score;
    private int bananasCollected;
     public BananaController(int panelWidth, int groundLevel) {
        this.PANEL_WIDTH = panelWidth;
        this.GROUND_LEVEL = groundLevel;
        this.bananas = new ArrayList<>();
        this.random = new Random();
        this.score = 0;
        this.bananasCollected = 0;
    }
     public void update(ArrayList<Obstacle> obstacles) {
        if (bananas.size() < MAX_BANANAS) {
            spawnBanana(obstacles);
        }

        updateBananas();
    }
     private void updateBananas() {
        Iterator<Banana> it = bananas.iterator();
        while (it.hasNext()) {
            Banana banana = it.next();
            banana.update();
            if (isOffscreen(banana)) {
                it.remove();
            }
        }
    }
     private boolean isOffscreen(Banana banana) {
        return banana.getX() + BANANA_WIDTH < 0;
    }
        private void spawnBanana(ArrayList<Obstacle> obstacles) {
        int bananaX = PANEL_WIDTH + random.nextInt(200);
        int bananaY;

        // Randomly decide if banana should be at monkey's level
        if (random.nextInt(100) < MONKEY_LEVEL_CHANCE) {
            // Place banana at monkey's running level
            bananaY = GROUND_LEVEL - MONKEY_HEIGHT - 20; // Match monkey's height
        } else {
            // Random height for other bananas
            int minHeight = GROUND_LEVEL - 300;  // Highest point
            int maxHeight = GROUND_LEVEL - 100;  // Lowest point
            bananaY = minHeight + random.nextInt(maxHeight - minHeight);
        }

        if (isValidSpawnLocation(bananaX, bananaY, obstacles)) {
            bananas.add(new Banana(bananaX, bananaY));
        }
    }
       private boolean isValidSpawnLocation(int x, int y, ArrayList<Obstacle> obstacles) {
        Rectangle bananaArea = new Rectangle(x, y, 30, 30);
        
        // Check collision with obstacles
        for (Obstacle obstacle : obstacles) {
            Rectangle obstacleArea = new Rectangle(
                obstacle.getX(), obstacle.getY(),
                obstacle.getWidth(), obstacle.getHeight()
            );
            if (bananaArea.intersects(obstacleArea)) {
                return false;
            }
        }
        return true;
    }
     public void checkCollisions(Monkey monkey) {
        Iterator<Banana> it = bananas.iterator();
        while (it.hasNext()) {
            Banana banana = it.next();
            if (!banana.isCollected() && checkCollision(banana, monkey)) {
                handleBananaCollection();
                it.remove();
            }
        }
    }
       private boolean checkCollision(Banana banana, Monkey monkey) {
        // Create collision bounds with insets for more precise collision
        Rectangle monkeyBounds = createMonkeyCollisionBounds(monkey);
        Rectangle bananaBounds = banana.getBounds();
        
        return bananaBounds.intersects(monkeyBounds);
    }

    private Rectangle createMonkeyCollisionBounds(Monkey monkey) {
        // Use a smaller collision box for more precise collision detection
        return new Rectangle(
            monkey.getX() + COLLISION_INSET,
            monkey.getY() + COLLISION_INSET,
            MONKEY_DEFAULT_SIZE - (COLLISION_INSET * 2),
            MONKEY_DEFAULT_SIZE - (COLLISION_INSET * 2)
        );
    }

    private void handleBananaCollection() {
        score++;
        bananasCollected++;
        // Could add sound effects or particle effects here
    }
    public void restart() {
        bananas.clear();
        score = 0;
        bananasCollected = 0;
    }
    
    // Getters
    public int getScore() { return score; }
    public int getBananasCollected() { return bananasCollected; }
    public ArrayList<Banana> getBananas() { return bananas; }
    
}
