package demogame.model;

import java.awt.Rectangle;
import java.util.logging.Logger;

public class Monkey {
    private static final Logger LOGGER = Logger.getLogger(Monkey.class.getName());
    // Physics constants
    private static final float JUMP_FORCE = -19.0f;
    private static final float GRAVITY = 0.6f;
    private static final float MOVE_SPEED = 0.5f;
    private static final float AIR_CONTROL = 0.7f;
    private static final float JUMP_MOVE_SPEED = 0.8f;
    private static final float MAX_FALL_SPEED = 8.0f;
    private static final float FORWARD_JUMP_FORCE = -23.0f;
    private static final float HORIZONTAL_DAMPING = 0.99f;

    // Animation constants
    private static final double FRAME_DURATION = 1.0 / 12.0; // 24 FPS (~41.67ms per frame)
    private static final int TOTAL_RUN_FRAMES = 24; // Use all 24 frames for running
    private static final int IDLE_FRAME_START = 1; // Frames 1–3 for idle
    private static final int IDLE_FRAME_COUNT = 3;
    private static final int JUMP_ASCEND_FRAME = 8; // Frame for ascending jump
    private static final int JUMP_DESCEND_FRAME = 11; // Frame for descending jump

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
    private double elapsedTime; // Time-based animation control
    private enum AnimationState { IDLE, RUNNING, JUMPING }
    private AnimationState animationState;

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
        this.elapsedTime = 0;
        this.animationState = AnimationState.IDLE;
        this.bounds = new Rectangle(startX, startY, 120, 120);
        if (bounds.width <= 0 || bounds.height <= 0) {
            LOGGER.severe("Invalid Monkey dimensions: width=" + bounds.width + ", height=" + bounds.height);
            throw new IllegalArgumentException("Monkey dimensions must be positive");
        }
        LOGGER.info("Monkey initialized at (" + startX + ", " + startY + ") with width=" + bounds.width + ", height=" + bounds.height);
    }

    public void update() {
        // Apply gravity if not on ground
        if (!isOnGround) {
            velocityY += GRAVITY;
            if (velocityY > MAX_FALL_SPEED) {
                velocityY = MAX_FALL_SPEED;
            }
            if (isJumping && velocityX > 0) {
                velocityX *= HORIZONTAL_DAMPING;
            }
        }

        // Update position based on velocity
        x += velocityX;
        y += velocityY;

        // Update collision bounds
        bounds.setLocation(Math.round(x), Math.round(y));

        // Update animation
        updateAnimation();
    }

    private void updateAnimation() {
        // Determine animation state
        if (!isOnGround) {
            animationState = AnimationState.JUMPING;
        } else if (Math.abs(velocityX) > 0.1f) {
            animationState = AnimationState.RUNNING;
        } else {
            animationState = AnimationState.IDLE;
        }

        // Update frame based on state
        elapsedTime += 1.0 / 60.0; // Game loop runs at 60 FPS (~16.67ms per tick)
        if (elapsedTime >= FRAME_DURATION) {
            switch (animationState) {
                case RUNNING:
                    currentFrame = ((currentFrame - 1 + 1) % TOTAL_RUN_FRAMES) + 1; // Cycle 1–24
                    break;
                case IDLE:
                    currentFrame = ((currentFrame - IDLE_FRAME_START + 1) % IDLE_FRAME_COUNT) + IDLE_FRAME_START; // Cycle 1–3
                    break;
                case JUMPING:
                    currentFrame = velocityY < 0 ? JUMP_ASCEND_FRAME : JUMP_DESCEND_FRAME; // 8 or 11
                    break;
            }
            elapsedTime -= FRAME_DURATION;
        }
    }

    public void jump() {
        if (isOnGround) {
            velocityY = JUMP_FORCE;
            isJumping = true;
            isOnGround = false;
            if (velocityX > 0) {
                velocityX = JUMP_MOVE_SPEED;
            }
            LOGGER.info("Monkey jumped at y=" + y);
        }
    }

    public void moveLeft() {
        float targetSpeed = isOnGround ? -MOVE_SPEED*2.0f : -MOVE_SPEED* 1.0f* AIR_CONTROL;
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
            isJumping = true;
            isOnGround = false;
            facingRight = true;
            LOGGER.info("Monkey jumped forward at y=" + y);
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
    public int getWidth() { return bounds.width; }
    public int getHeight() { return bounds.height; }

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