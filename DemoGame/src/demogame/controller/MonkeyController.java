package demogame.controller;

import demogame.model.Monkey;
import demogame.view.GamePanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MonkeyController implements KeyListener {
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
    private GamePanel gamePanel;

    
    public MonkeyController(Monkey monkey) {
        this.monkey = monkey;
        this.leftPressed = false;
        this.rightPressed = false;
        this.spacePressed = false;
        this.jumpInProgress = false;
        this.lastJumpTime = 0;
    }

    // game panel to check tutorial state
    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    // this for key presses to control monkey movement and jumping
    @Override
    public void keyPressed(KeyEvent e) {
        // ignore input if tutorial is active
        if (gamePanel != null && gamePanel.isTutorialActive()) {
            return;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> {
                leftPressed = true;
                updateMovement();
            }
            case KeyEvent.VK_RIGHT -> {
                rightPressed = true;
                //  forward jump if space is also pressed
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

    // Handles key releases to update movement states
    @Override
    public void keyReleased(KeyEvent e) {
        if (gamePanel != null && gamePanel.isTutorialActive()) {
            return;
        }
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
            }
        }
    }

    // Processes jump input, deciding between normal or forward jump-> they are special moves
    private void handleJumpInput() {
        if (monkey.isOnGround()) {
            long currentTime = System.currentTimeMillis();
            // Prevent rapid jumps within 200ms
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

    //   normal jump maintaining current direction
    private void performNormalJump() {
        monkey.jump();
        jumpInProgress = true;
        if (rightPressed) {
            monkey.setVelocityX(NORMAL_MOVE_SPEED * 1.5f);
        } else if (leftPressed) {
            monkey.setVelocityX(-NORMAL_MOVE_SPEED);
        }
    }

    //forward jump with extra speed to clear obstacles
    private void performForwardJump() {
        float jumpVelocity = JUMP_MOVE_SPEED * JUMP_BOOST;
        monkey.setVelocityX(jumpVelocity);
        monkey.jumpForward();
        jumpInProgress = true;
        monkey.setFacingRight(true);
    }

    // update monkey movement based on key states and ground air status
    private void updateMovement() {
        // maintain forward momentum during jump
        if (jumpInProgress && monkey.isJumping()) {
            if (rightPressed) {
                float currentVelocity = monkey.getVelocityX();
                if (currentVelocity < JUMP_MOVE_SPEED) {
                    monkey.setVelocityX(currentVelocity + 0.5f);
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
                // gradually slow down in air
                float currentVelocity = monkey.getVelocityX();
                monkey.setVelocityX(currentVelocity * 0.98f);
            }
        }

        // reset jump state when on ground
        if (monkey.isOnGround()) {
            jumpInProgress = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    // resets all input and jump states for a new game
    public void reset() {
        leftPressed = false;
        rightPressed = false;
        spacePressed = false;
        jumpInProgress = false;
        lastJumpTime = 0;
    }
}