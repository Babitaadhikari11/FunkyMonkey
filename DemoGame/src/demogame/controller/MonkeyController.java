package demogame.controller;
import demogame.model.Monkey;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
public class MonkeyController implements KeyListener {
    // Increased speeds for better obstacle clearing
    private static final float NORMAL_MOVE_SPEED = 2.0f;
    private static final float JUMP_MOVE_SPEED = 8.0f;     // Increased from 6.0f
    private static final float JUMP_BOOST = 1.2f;          // Increased from 1.5f
    private static final float AIR_CONTROL = 0.8f;
    private static final int JUMP_WINDOW = 200;

    private final Monkey monkey;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean spacePressed;
    private boolean jumpInProgress;
    private long lastJumpTime;

    public MonkeyController(Monkey monkey) {
        this.monkey = monkey;
        this.leftPressed = false;
        this.rightPressed = false;
        this.spacePressed = false;
        this.jumpInProgress = false;
        this.lastJumpTime = 0;
    }
     @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> {
                leftPressed = true;
                updateMovement();
            }
            case KeyEvent.VK_RIGHT -> {
                rightPressed = true;
                if (spacePressed && monkey.isOnGround()) {
                    performForwardJump();
                } else {
                    updateMovement();
                }
            }
            case KeyEvent.VK_SPACE, KeyEvent.VK_UP -> {
                if (!spacePressed) {
                    spacePressed = true;
                    handleJumpInput();
                }
            }
        }
    }
       @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> {
                leftPressed = false;
                updateMovement();
            }
            case KeyEvent.VK_RIGHT -> {
                rightPressed = false;
                updateMovement();
            }
            case KeyEvent.VK_SPACE, KeyEvent.VK_UP -> {
                spacePressed = false;
                // Don't reset jumpInProgress here to maintain momentum
            }
        }
    }

    private void handleJumpInput() {
        if (monkey.isOnGround()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastJumpTime > JUMP_WINDOW) {
                if (rightPressed) {
                    performForwardJump();
                } else {
                    performNormalJump();
                }
                lastJumpTime = currentTime;
            }
        }
    }

    private void performNormalJump() {
        monkey.jump();
        jumpInProgress = true;
        // Maintain current horizontal velocity during normal jump
        if (rightPressed) {
            monkey.setVelocityX(NORMAL_MOVE_SPEED * 1.5f); // Slight boost
        } else if (leftPressed) {
            monkey.setVelocityX(-NORMAL_MOVE_SPEED);
        }
    }
     private void performForwardJump() {
        // Significantly increased forward momentum for obstacle clearing
        float jumpVelocity = JUMP_MOVE_SPEED * JUMP_BOOST;
        monkey.setVelocityX(jumpVelocity);
        monkey.jumpForward(); // Use special forward jump with higher arc
        jumpInProgress = true;
        monkey.setFacingRight(true);
    }
    private void updateMovement() {
        if (jumpInProgress && monkey.isJumping()) {
            // Maintain strong forward momentum during jump
            if (rightPressed) {
                float currentVelocity = monkey.getVelocityX();
                if (currentVelocity < JUMP_MOVE_SPEED) {
                    monkey.setVelocityX(currentVelocity + 0.5f); // Gradual acceleration
                }
            }
            return;
        }

        if (leftPressed && !rightPressed) {
            float speed = monkey.isOnGround() ? NORMAL_MOVE_SPEED : (NORMAL_MOVE_SPEED * AIR_CONTROL);
            monkey.setVelocityX(-speed);
            monkey.setFacingRight(false);
        } else if (rightPressed && !leftPressed) {
            float speed = monkey.isOnGround() ? NORMAL_MOVE_SPEED : (NORMAL_MOVE_SPEED * AIR_CONTROL);
            monkey.setVelocityX(speed);
            monkey.setFacingRight(true);
        } else {
            if (monkey.isOnGround()) {
                monkey.setVelocityX(0);
            } else {
                // Gradual air deceleration
                float currentVelocity = monkey.getVelocityX();
                monkey.setVelocityX(currentVelocity * 0.98f);
            }
        }

        if (monkey.isOnGround()) {
            jumpInProgress = false;
        }
    }
    
}
