package demogame.model;
import java.awt.Rectangle;
public class Monkey {
    // Physics constants
    private static final float JUMP_FORCE = -18.0f;
    private static final float GRAVITY = 0.6f;
    // private static final float MAX_FALL_SPEED = 12.0f;
    private static final float MOVE_SPEED = 0.5f;
    private static final float AIR_CONTROL = 0.7f;
    private static final float JUMP_MOVE_SPEED = 0.8f; // Faster forward movement while jumping
    private static final float MAX_FALL_SPEED = 8.0f;
    private static final float FORWARD_JUMP_FORCE = -22.0f; // Extra boost when jumping forward
    private static final float HORIZONTAL_DAMPING = 0.99f; // Reduced horizontal slowdown
     // Position and velocity
    private float x;
    private float y;
    private float velocityX;
    private float velocityY;
    // State
    private boolean isJumping;
    private boolean isOnGround;
    private boolean facingRight;

    // Animation
    private int currentFrame;
    private int frameDelay;
    private static final int FRAME_DELAY_LIMIT = 5;

    // Collision
    private Rectangle bounds;
      public Monkey(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.velocityX = 0;
        this.velocityY = 0;
        this.isJumping = false;
        this.isOnGround = true;
        this.facingRight = true;
        this.currentFrame = 1;
        this.bounds = new Rectangle(startX, startY, 120, 120);
    }
    // Getters
    public int getX() { return Math.round(x); }
    public int getY() { return Math.round(y); }
    public float getVelocityX() { return velocityX; }
    public float getVelocityY() { return velocityY; }
    public boolean isJumping() { return isJumping; }
    public boolean isOnGround() { return isOnGround; }
    public boolean isFacingRight() { return facingRight; }
    public Rectangle getBounds() { return bounds; }
    public int getCurrentFrameNumber() { return currentFrame; }

    // Setters
    public void setX(int x) { 
        this.x = x;
        bounds.x = x;
    }
    
    public void setY(int y) { 
        this.y = y;
        bounds.y = y;
    }
    
    public void setVelocityX(float vx) { 
        this.velocityX = vx;
    }
    
    public void setVelocityY(float vy) { 
        this.velocityY = vy;
    }
    
    public void setOnGround(boolean onGround) {
        this.isOnGround = onGround;
        if (onGround) {
            isJumping = false;
            velocityY = 0;
        }
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }
}

    

