package demogame.controller;
import demogame.model.Obstacle;
import demogame.model.Monkey;
import demogame.view.GamePanel;
import java.util.ArrayList;
import java.util.Random;

public class ObstacleController {
     // Constants
    private static final int MIN_OBSTACLE_SPACING = 500;
    private static final int SPAWN_INTERVAL = 180;
    private static final int MAX_OBSTACLES = 3;
    private static final int SPAWN_OFFSET = 300;
    
    // Game properties
    private final int PANEL_WIDTH;
    private final int GROUND_LEVEL;
    private final GamePanel gamePanel;
    private final ArrayList<Obstacle> obstacles;
    private final Random random;
    private int spawnTimer;
    public ObstacleController(int panelWidth, int groundLevel, GamePanel gamePanel) {
        this.PANEL_WIDTH = panelWidth;
        this.GROUND_LEVEL = groundLevel;
        this.gamePanel = gamePanel;
        this.obstacles = new ArrayList<>();
        this.random = new Random();
        this.spawnTimer = 0;
    }
    public void update() {
        // Remove off-screen obstacles
        obstacles.removeIf(obstacle -> 
            obstacle.getX() + obstacle.getWidth() < 0);
        
        // Update existing obstacles
        for (Obstacle obstacle : obstacles) {
            obstacle.update();
        }
        
        // Spawn new obstacles
        spawnTimer++;
        if (spawnTimer >= SPAWN_INTERVAL) {
            if (canSpawnObstacle()) {
                spawnObstacle();
                spawnTimer = 0;
            }
        }
    }
    private boolean canSpawnObstacle() {
        // Don't spawn if too many obstacles
        if (obstacles.size() >= MAX_OBSTACLES) {
            return false;
        }

        // Check spacing from last obstacle
        if (!obstacles.isEmpty()) {
            Obstacle lastObstacle = obstacles.get(obstacles.size() - 1);
            return lastObstacle.getX() < PANEL_WIDTH - MIN_OBSTACLE_SPACING;
        }

        return true;
    }
     private void spawnObstacle() {
    int obstacleX = PANEL_WIDTH + SPAWN_OFFSET;
    // Adjust Y position to match monkey's level
    int obstacleY = GROUND_LEVEL - 110; // Move obstacles up slightly
    
    // Create new obstacle with slightly smaller size
    Obstacle obstacle = new Obstacle(obstacleX, obstacleY, 70, 90);
    obstacles.add(obstacle);
}
public boolean checkCollisions(Monkey monkey) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.checkCollision(monkey)) {
                return true;
            }
        }
        return false;
    }

    public void restart() {
        obstacles.clear();
        spawnTimer = 0;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    
}
