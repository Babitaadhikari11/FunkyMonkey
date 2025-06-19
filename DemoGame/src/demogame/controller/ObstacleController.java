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
    
}
