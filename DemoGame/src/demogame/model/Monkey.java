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

    public void update() {
        // Apply gravity if not on ground
        if (!isOnGround) {
            velocityY += GRAVITY;
            if (velocityY > MAX_FALL_SPEED) {
                velocityY = MAX_FALL_SPEED;
            }
             // Maintain forward momentum during jump
            if (isJumping && velocityX > 0) {
                velocityX *= 0.99f; // Very slight decay of forward momentum
            }
        }

        // Update position based on velocity
        x += velocityX;
        y += velocityY;

        // Update collision bounds
        // bounds.setLocation(Math.round(x), Math.round(y));

        // Update animation
        updateAnimation();
    }

    private void updateAnimation() {
        frameDelay++;
        if (frameDelay >= FRAME_DELAY_LIMIT) {
            frameDelay = 0;
            
            // Update frame based on state
            if (!isOnGround) {
                currentFrame = velocityY < 0 ? 8 : 11; // Jump or fall animation
            } else if (Math.abs(velocityX) > 0.1f) {
                currentFrame = ((currentFrame - 4 + 1) % 4) + 4; // Run animation
            } else {
                currentFrame = ((currentFrame - 1 + 1) % 3) + 1; // Idle animation
            }
        }
    }

    public void jump() {
        if (isOnGround) {
            velocityY = JUMP_FORCE;
            isJumping = true;
            isOnGround = false;
            // Add forward momentum if moving
            if (velocityX > 0) {
                velocityX = JUMP_MOVE_SPEED; // Faster forward movement during jump
            }
        }
    }

    public void moveLeft() {
        float targetSpeed = isOnGround ? -MOVE_SPEED : -MOVE_SPEED * AIR_CONTROL;
        velocityX = targetSpeed;
        facingRight = false;
    }

    public void moveRight() {
        float targetSpeed = isOnGround ? MOVE_SPEED : MOVE_SPEED * AIR_CONTROL;
        velocityX = targetSpeed;
        facingRight = true;
    }

    public void jumpForward() {
        if (isOnGround) {
            velocityY = FORWARD_JUMP_FORCE;
            // velocityX = FORWARD_JUMP_FORCE;
            isJumping = true;
            isOnGround = false;
            facingRight = true;
        }
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