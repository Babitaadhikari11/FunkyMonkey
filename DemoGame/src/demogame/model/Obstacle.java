package demogame.model;

import java.awt.Rectangle;
import java.util.logging.Logger;

public class Obstacle {
    private static final Logger LOGGER = Logger.getLogger(Obstacle.class.getName());
    private static final float SPEED = 3.4f;
    private static final int COLLISION_INSET = 15;
    private static final int VERTICAL_OFFSET = 40;
    private static final int MONKEY_DEFAULT_SIZE = 120;
    private static final int JUMP_CLEARANCE = 40;

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

    private int x;
    private int y;
    private final int width;
    private final int height;
    private float speed;
    private boolean isActive;
    private boolean wasJumpedOver;
    private Rectangle bounds;
    private Rectangle collisionBounds;
    private CollisionListener collisionListener;

    public Obstacle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = SPEED;
        this.isActive = true;
        this.wasJumpedOver = false;
        initializeBounds();
    }

    public static Obstacle createFromPreset(int x, int y, int presetIndex) {
        if (presetIndex < 0 || presetIndex >= SIZE_PRESETS.length) {
            presetIndex = 1;
        }
        SizePreset preset = SIZE_PRESETS[presetIndex];
        return new Obstacle(x, y, preset.width, preset.height);
    }

    public void setCollisionListener(CollisionListener listener) {
        this.collisionListener = listener;
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

    private void updateBounds() {
        bounds.setLocation(x, y);
        collisionBounds.setLocation(
            x + COLLISION_INSET,
            y + COLLISION_INSET
        );
    }

    public boolean checkCollision(Monkey monkey) {
        if (!isActive || wasJumpedOver) return false;

        Rectangle monkeyBounds = new Rectangle(
            monkey.getX() + 20,
            monkey.getY() + 20,
            MONKEY_DEFAULT_SIZE - 40,
            MONKEY_DEFAULT_SIZE - 40
        );

        Rectangle obstacleHitbox = new Rectangle(
            x + 10,
            y + 10,
            width - 20,
            height - 20
        );

        // Log positions and states
        LOGGER.fine("Collision check: Monkey y=" + monkey.getY() + ", obstacle y=" + y +
                    ", isJumping=" + monkey.isJumping() + ", velocityX=" + monkey.getVelocityX());

        // Check for successful jump
        if (monkey.isJumping() && monkey.getVelocityX() > 0) {
            // Require monkey to be above and past obstacle
            boolean isAboveObstacle = monkey.getY() + MONKEY_DEFAULT_SIZE - 40 < y;
            boolean isPastObstacle = monkey.getX() > x + width;
            if (isAboveObstacle && isPastObstacle) {
                wasJumpedOver = true;
                LOGGER.info("Obstacle jumped over at x=" + x + ", y=" + y);
                return false;
            }
        }

        // Check collision
        if (obstacleHitbox.intersects(monkeyBounds)) {
            LOGGER.info("Collision detected: Monkey at " + monkeyBounds + ", Obstacle at " + obstacleHitbox);
            if (collisionListener != null) {
                collisionListener.onObstacleCollision(this);
            }
            return true;
        }
        return false;
    }

    private Rectangle createMonkeyCollisionBounds(Monkey monkey) {
        int inset = 30;
        return new Rectangle(
            monkey.getX() + inset,
            monkey.getY() + inset,
            MONKEY_DEFAULT_SIZE - (inset * 2),
            MONKEY_DEFAULT_SIZE - (inset * 2)
        );
    }

    private boolean isApproachingObstacle(Monkey monkey) {
        int distanceToObstacle = x - (monkey.getX() + MONKEY_DEFAULT_SIZE);
        return distanceToObstacle > 0 && distanceToObstacle < 200;
    }

    private boolean isInCollisionRange(Monkey monkey) {
        int monkeyRight = monkey.getX() + MONKEY_DEFAULT_SIZE;
        int obstacleLeft = x;
        int obstacleRight = x + width;
        return monkeyRight >= obstacleLeft && monkey.getX() <= obstacleRight;
    }

    public boolean shouldJump(Monkey monkey) {
        if (!isActive || wasJumpedOver) return false;
        int distanceToObstacle = x - (monkey.getX() + MONKEY_DEFAULT_SIZE);
        int optimalJumpDistance = width + 100;
        return distanceToObstacle > 0 && distanceToObstacle < optimalJumpDistance;
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
    public CollisionListener getCollisionListener() { return collisionListener; } // Added for GamePanel

    // Setters
    public void setActive(boolean active) { this.isActive = active; }
    public void setSpeed(float speed) { this.speed = speed; }
    public void reset() {
        wasJumpedOver = false;
        isActive = true;
        speed = SPEED;
    }

    public interface CollisionListener {
        void onObstacleCollision(Obstacle obstacle);
    }
}