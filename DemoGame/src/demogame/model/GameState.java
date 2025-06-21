package demogame.model;

public class GameState {
    private int score;
    private int level;
    private int distanceTraveled;
    private int bananasCollected;
    private float gameSpeed;
    private static final float SPEED_INCREMENT = 0.1f;
    private static final int LEVEL_UP_SCORE = 10;

    public GameState() {
        score = 0;
        level = 1;
        distanceTraveled = 0;
        gameSpeed = 1.0f;
    }

    public void update() {
        distanceTraveled++;
        // level up every LEVEL_UP_SCORE points
        level = 1 + (score / LEVEL_UP_SCORE);
        // increase game speed with level
        gameSpeed = 1.0f + (level - 1) * SPEED_INCREMENT;
    }

    // Getters and setters
    public int getScore() { 
        return score; 
    }
    public int getLevel() { 
        return level; 
    }
    public float getGameSpeed() {
         return gameSpeed;
         }
    public int getDistanceTraveled() { 
        return distanceTraveled; 
    }
    
    public void addScore(int points) {
        score += points;
    }

    public void reset() {
        score = 0;
        level = 1;
        distanceTraveled = 0;
        gameSpeed = 1.0f;
    }
}