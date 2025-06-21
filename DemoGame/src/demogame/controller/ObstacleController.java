package demogame.controller;

import demogame.model.Monkey;
import demogame.model.Obstacle;
import demogame.view.GamePanel;
import java.util.ArrayList;
import java.util.Random;

public class ObstacleController {
    // constants for obstacle spawning and spacing
    private static final int MIN_OBSTACLE_SPACING = 500;
    private static final int SPAWN_INTERVAL = 180;
    private static final int MAX_OBSTACLES = 3;
    private static final int SPAWN_OFFSET = 300;
    
    // game properties
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

    // updates obstacles ->removes off-screen , moves existing, give new
    public void update() {
  
        obstacles.removeIf(obstacle -> 
            obstacle.getX() + obstacle.getWidth() < 0);
        

        for (Obstacle obstacle : obstacles) {
            obstacle.update();
        }
        
  
        spawnTimer++;
        if (spawnTimer >= SPAWN_INTERVAL) {
            if (canSpawnObstacle()) {
                spawnObstacle();
                spawnTimer = 0;
            }
        }
    }

    // checks if a new obstacle can be generated based on count and spacing
    private boolean canSpawnObstacle() {
        
        if (obstacles.size() >= MAX_OBSTACLES) {
            return false;
        }

  
        if (!obstacles.isEmpty()) {
            Obstacle lastObstacle = obstacles.get(obstacles.size() - 1);
            return lastObstacle.getX() < PANEL_WIDTH - MIN_OBSTACLE_SPACING;
        }

        return true;
    }

    // creates and adds a new obstacle at screen right side
    private void spawnObstacle() {
        int obstacleX = PANEL_WIDTH + SPAWN_OFFSET;
        int obstacleHeight =90;
        int obstacleY = GROUND_LEVEL - obstacleHeight+20; // slightly raised
        

        Obstacle obstacle = new Obstacle(obstacleX, obstacleY, 70, 90);
        obstacles.add(obstacle);
    }

    // checks if monkey collides with any obstacle
    public boolean checkCollisions(Monkey monkey) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.checkCollision(monkey)) {
                return true; // collision detected
            }
        }
        return false;
    }

    // clears obstacles and resets timer for game restart
    public void restart() {
        obstacles.clear();
        spawnTimer = 0;
    }

    // returns list of current obstacles
    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }
}