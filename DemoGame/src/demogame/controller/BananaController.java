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
    
}
