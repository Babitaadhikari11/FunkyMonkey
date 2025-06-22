package demogame.controller;

import demogame.model.Banana;
import demogame.model.Monkey;
import demogame.model.Obstacle;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BananaController {
    private static final Logger LOGGER = Logger.getLogger(BananaController.class.getName());
    
    private static final int MAX_BANANAS = 5;
    private static final int BANANA_WIDTH = 30;
    private static final int BANANA_HEIGHT = 30;
    private static final int SPAWN_INTERVAL = 100;
    private static final int SPAWN_Y_MIN = 450; // New: Minimum spawn y (higher above ground)
    private static final int SPAWN_Y_MAX = 650; // New: Maximum spawn y
    private static final int COLLISION_INSET = 30;
    private static final double MONKEY_LEVEL_CHANCE = 0.3;
    private static final int BANANA_SPAWN_X_OFFSET = 50;

    private final ArrayList<Banana> bananas;
    private final ArrayList<ScoreListener> scoreListeners;
    private final int panelWidth;
    private final int groundLevel;
    private final Random random;
    private int score;
    private int bananasCollected;
    private int spawnTimer;
    private final ScoreController scoreController;

    public BananaController(int panelWidth, int groundLevel, ScoreController scoreController) {
        this.panelWidth = panelWidth;
        this.groundLevel = groundLevel;
        this.scoreController = scoreController;
        this.bananas = new ArrayList<>();
        this.scoreListeners = new ArrayList<>();
        this.random = new Random();
        this.score = 0;
        this.bananasCollected = 0;
        this.spawnTimer = 0;
    }

    public void update(ArrayList<Obstacle> obstacles) {
        spawnTimer++;
        if (spawnTimer >= SPAWN_INTERVAL && bananas.size() < MAX_BANANAS) {
            spawnBanana(obstacles);
            spawnTimer = 0;
        }
        updateBananas();
    }

    private void spawnBanana(ArrayList<Obstacle> obstacles) {
        int x = panelWidth + BANANA_SPAWN_X_OFFSET;
        // New: Spawn y between SPAWN_Y_MIN (400) and SPAWN_Y_MAX (550)
        int y = SPAWN_Y_MIN + random.nextInt(SPAWN_Y_MAX - SPAWN_Y_MIN + 1);
        
        if (isValidSpawnLocation(x, y, obstacles)) {
            Banana banana = new Banana(x, y);
            bananas.add(banana);
            LOGGER.info("Spawned banana at (" + x + ", " + y + ")");
        } else {
            LOGGER.fine("Banana spawn at (" + x + ", " + y + ") blocked by obstacle");
        }
    }

    private boolean isValidSpawnLocation(int x, int y, ArrayList<Obstacle> obstacles) {
        Rectangle bananaArea = new Rectangle(x, y, BANANA_WIDTH, BANANA_HEIGHT);
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

    private void updateBananas() {
        for (int i = bananas.size() - 1; i >= 0; i--) {
            Banana banana = bananas.get(i);
            banana.update();
            if (banana.getX() + BANANA_WIDTH < 0 || banana.isCollected()) {
                bananas.remove(i);
                LOGGER.fine("Removed banana at x=" + banana.getX());
            }
        }
    }

    public void checkCollisions(Monkey monkey) {
        if (monkey == null) {
            LOGGER.severe("Monkey is null in checkCollisions");
            return;
        }

        // Validate monkey dimensions
        int monkeyWidth = monkey.getWidth();
        int monkeyHeight = monkey.getHeight();
        if (monkeyWidth <= COLLISION_INSET * 2 || monkeyHeight <= COLLISION_INSET * 2) {
            LOGGER.warning("Invalid monkey dimensions: width=" + monkeyWidth + ", height=" + monkeyHeight);
            return;
        }

        Rectangle monkeyBounds = new Rectangle(
            monkey.getX() + COLLISION_INSET,
            monkey.getY() + COLLISION_INSET,
            monkeyWidth - (COLLISION_INSET * 2),
            monkeyHeight - (COLLISION_INSET * 2)
        );

        for (Banana banana : new ArrayList<>(bananas)) {
            if (banana == null || banana.isCollected()) {
                LOGGER.warning("Skipping null or collected banana");
                continue;
            }

            Rectangle bananaBounds = banana.getBounds();
            if (bananaBounds == null) {
                LOGGER.warning("Banana bounds are null at position (" + banana.getX() + ", " + banana.getY() + ")");
                continue;
            }

            if (monkeyBounds.intersects(bananaBounds)) {
                banana.setCollected(true);
                handleBananaCollection();
                LOGGER.info("Collision detected: Monkey at " + monkeyBounds + ", Banana at " + bananaBounds);
            }
        }
    }

    private void handleBananaCollection() {
        score++;
        bananasCollected++;
        scoreController.updateScore(score);
        notifyScoreListeners();
        LOGGER.info("Banana collected. Score: " + score + ", Bananas: " + bananasCollected);
    }

    private void notifyScoreListeners() {
        LOGGER.info("Notifying " + scoreListeners.size() + " score listeners");
        for (ScoreListener listener : scoreListeners) {
            try {
                listener.onScoreChanged(score);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error notifying score listener", e);
            }
        }
    }

    public void addScoreListener(ScoreListener listener) {
        if (listener != null) {
            scoreListeners.add(listener);
        }
    }

    public void restart() {
        bananas.clear();
        score = 0;
        bananasCollected = 0;
        spawnTimer = 0;
        scoreController.updateScore(0);
    }

    // Getters
    public ArrayList<Banana> getBananas() { return bananas; }
    public int getScore() { return score; }
    public int getBananasCollected() { return bananasCollected; }

    public interface ScoreListener {
        void onScoreChanged(int score);
    }
}