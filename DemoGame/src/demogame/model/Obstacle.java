package demogame.model;
import java.awt.Rectangle;
import java.lang.reflect.Constructor;
public class Obstacle {
    // Constants for obstacle properties
    private static final int SPEED = 3;
    private static final int COLLISION_INSET = 15;
    private static final int VERTICAL_OFFSET = 40;
    private static final int MONKEY_DEFAULT_SIZE = 120;
    private static final int JUMP_CLEARANCE = 40;
     // Size presets - correctly defined
    private static final class SizePreset {
        final int width;
        final int height;
        
        SizePreset(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    private static final SizePreset[] SIZE_PRESETS = {
        new SizePreset(60, 80),   // Small obstacle
        new SizePreset(80, 100),  // Medium obstacle
        new SizePreset(100, 120)  // Large obstacle
    };

    // Position and dimensions
    private int x;
    private int y;
    private final int width;
    private final int height;
    private float speed;

    // State
    private boolean isActive;
    private boolean wasJumpedOver;
    private Rectangle bounds;
    private Rectangle collisionBounds;
     // Constructor for specific size
    public Obstacle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y ;
        this.width = width;
        this.height = height;
        this.speed = SPEED;
        this.isActive = true;
        this.wasJumpedOver = false;
        
        initializeBounds();
    }
    // Constructor using size preset
    public static Obstacle createFromPreset(int x, int y, int presetIndex) {
        if (presetIndex < 0 || presetIndex >= SIZE_PRESETS.length) {
            presetIndex = 1; // Default to medium size
        }
        SizePreset preset = SIZE_PRESETS[presetIndex];
        return new Obstacle(x, y, preset.width, preset.height);
    }
      private void initializeBounds() {
        this.bounds = new Rectangle(x, y, width, height);
        this.collisionBounds = new Rectangle(
            x + COLLISION_INSET,
            y + COLLISION_INSET,
            width - (COLLISION_INSET * 2),
            height - (COLLISION_INSET * 2)
        );
    }

    public void update() {
        if (isActive && x > -width) {
            x -= speed;
            updateBounds();
        } else {
            isActive = false;
        }
    }
     // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isActive() { return isActive; }
    public Rectangle getBounds() { return new Rectangle(bounds); }
    public Rectangle getCollisionBounds() { return new Rectangle(collisionBounds); }
    public float getSpeed() { return speed; }

    // Setters
    public void setActive(boolean active) { this.isActive = active; }
    public void setSpeed(float speed) { this.speed = speed; }
    public void reset() {
        wasJumpedOver = false;
        isActive = true;
        speed = SPEED;
    }


    
}
