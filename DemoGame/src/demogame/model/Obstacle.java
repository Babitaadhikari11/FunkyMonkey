// package demogame.model;

// import java.awt.Rectangle;

// public class Obstacle {
//     private int x;
//     private int y;
//     private final int WIDTH = 80;
//     private final int HEIGHT = 100; // Match with monkey height
//     private Rectangle bounds;
//     private boolean isActive = true;
//     private final int SPEED = 3; // Increased speed for visibility
//     private final int PLAYABLE_AREA;
//     public Obstacle(int x, int y) {
//         this.x = x;
//         this.y = y-40;
//         this.bounds = new Rectangle(x, y, WIDTH, HEIGHT);
//         this.PLAYABLE_AREA = (int)(1200 * 0.65); // Assuming panel width is 1200
//     }
//     public void update() {
//         // Only move if within or approaching playable area
//         if (x > 0) {
//             x -= SPEED;
//             bounds.setLocation(x, y);
//         }
//     }

   
//     public boolean checkCollision(Monkey monkey) {
//         // Create smaller collision bounds for more precise detection
//         Rectangle monkeyBounds = new Rectangle(
//             monkey.getX() + 30,           // Add offset to narrow collision area
//             monkey.getY() + 30,           // Add offset to narrow collision area
//             monkey.getWidth() - 60,       // Reduce width for more precise collision
//             monkey.getHeight() - 60       // Reduce height for more precise collision
//         );
        
//         Rectangle obstacleBounds = new Rectangle(
//             x + 15,                       // Add offset to narrow collision area
//             y + 15,                       // Add offset to narrow collision area
//             WIDTH - 30,                   // Reduce width for more precise collision
//             HEIGHT - 30                   // Reduce height for more precise collision
//         );
//         // debugginh
//         System.out.println("monkey bounds: "+monkeyBounds);
//         System.out.println("obstacle bounds: "+obstacleBounds);
//          // Only detect collision if monkey is not jumping high enough
//         if (monkey.isJumping() && monkey.getY() + monkey.getHeight() < y) {
//             return false;  // Successfully jumped over
//         }


//         return obstacleBounds.intersects(monkeyBounds);
//     }

//     public int getX() { return x; }
//     public int getY() { return y; }
//     public int getWidth() { return WIDTH; }
//     public int getHeight() { return HEIGHT; }
//     public boolean isActive() { return isActive; }
//     public void setActive(boolean active) { this.isActive = active; }
// }
package demogame.model;

import java.awt.Rectangle;

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

    private void updateBounds() {
        bounds.setLocation(x, y);
        collisionBounds.setLocation(
            x + COLLISION_INSET,
            y + COLLISION_INSET
        );
    }
    public boolean checkCollision(Monkey monkey) {
    if (!isActive || wasJumpedOver) return false;

    // Create tighter collision bounds
    Rectangle monkeyBounds = new Rectangle(
        monkey.getX() + 20,        // Smaller inset
        monkey.getY() + 20,        // Smaller inset
        MONKEY_DEFAULT_SIZE - 40,   // Tighter collision box
        MONKEY_DEFAULT_SIZE - 40
    );

    // Create obstacle collision bounds
    Rectangle obstacleHitbox = new Rectangle(
        x + 10,                    // Smaller inset
        y + 10,                    // Smaller inset
        width - 20,                // Tighter collision box
        height - 20
    );

    // Debug output
    System.out.println("Monkey Y: " + monkey.getY() + ", Obstacle Y: " + y);
    System.out.println("Jumping: " + monkey.isJumping());

    // Check for successful jump
    if (monkey.isJumping()) {
        // More forgiving jump detection
        boolean isAboveObstacle = monkey.getY() + MONKEY_DEFAULT_SIZE - 40 < y;
        boolean hasForwardMomentum = monkey.getVelocityX() > 0;
        
        if (isAboveObstacle && hasForwardMomentum) {
            wasJumpedOver = true;
            return false;
        }
    }

    return obstacleHitbox.intersects(monkeyBounds);
}


    // public boolean checkCollision(Monkey monkey) {
    //     if (!isActive || wasJumpedOver) return false;

    //     Rectangle monkeyBounds = createMonkeyCollisionBounds(monkey);

    //     if (monkey.isJumping()) {
    //         boolean isAboveObstacle = monkey.getY() + MONKEY_DEFAULT_SIZE - JUMP_CLEARANCE < y;
    //         boolean hasForwardMomentum = monkey.getVelocityX() > 0;
    //         boolean isApproaching = isApproachingObstacle(monkey);
    //         boolean isClearing = monkey.getY() + MONKEY_DEFAULT_SIZE < y + height/2;

    //         if ((isAboveObstacle && hasForwardMomentum) || 
    //             (isApproaching && isClearing && hasForwardMomentum)) {
    //             wasJumpedOver = true;
    //             return false;
    //         }

    //         if (monkey.getVelocityX() > 8.0f && 
    //             monkey.getY() + MONKEY_DEFAULT_SIZE < y + height) {
    //             wasJumpedOver = true;
    //             return false;
    //         }
    //     }

    //     if (!isInCollisionRange(monkey)) {
    //         return false;
    //     }

    //     return collisionBounds.intersects(monkeyBounds);
    // }

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

    // Setters
    public void setActive(boolean active) { this.isActive = active; }
    public void setSpeed(float speed) { this.speed = speed; }
    public void reset() {
        wasJumpedOver = false;
        isActive = true;
        speed = SPEED;
    }
}