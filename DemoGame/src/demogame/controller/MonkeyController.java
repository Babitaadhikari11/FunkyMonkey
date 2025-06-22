package demogame.controller;

import demogame.model.Monkey;
import demogame.view.GamePanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Logger;

public class MonkeyController implements KeyListener {
    private static final Logger LOGGER = Logger.getLogger(MonkeyController.class.getName());
    private static final float NORMAL_MOVE_SPEED = 2.0f;
    private static final float JUMP_MOVE_SPEED = 9.0f;
    private static final float JUMP_BOOST = 1.2f;
    private static final float AIR_CONTROL = 0.8f;

    private final Monkey monkey;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean spacePressed;
    private boolean jumpInProgress;
    private GamePanel gamePanel;

    public MonkeyController(Monkey monkey) {
        this.monkey = monkey;
        this.leftPressed = false;
        this.rightPressed = false;
        this.spacePressed = false;
        this.jumpInProgress = false;
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gamePanel != null && (gamePanel.isTutorialActive() || gamePanel.isPaused() || gamePanel.isGameOver())) {
            return;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (!leftPressed) {
                    leftPressed = true;
                    updateMovement();
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (!rightPressed) {
                    rightPressed = true;
                    if (spacePressed && monkey.isOnGround()) {
                        performForwardJump();
                    } else {
                        updateMovement();
                    }
                }
                break;
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_UP:
                if (!spacePressed) {
                    spacePressed = true;
                    handleJumpInput();
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gamePanel != null && (gamePanel.isTutorialActive() || gamePanel.isPaused() || gamePanel.isGameOver())) {
            return;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                leftPressed = false;
                updateMovement();
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = false;
                updateMovement();
                break;
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_UP:
                spacePressed = false;
                break;
        }
    }

    private void handleJumpInput() {
        if (!monkey.isOnGround()) {
            LOGGER.fine("Jump input ignored: monkey not on ground, y=" + monkey.getY());
            return;
        }

        if (rightPressed) {
            performForwardJump();
        } else {
            performNormalJump();
        }
        LOGGER.fine("Jump input processed, onGround=" + monkey.isOnGround());
    }

    private void performNormalJump() {
        monkey.jump();
        jumpInProgress = true;
        if (rightPressed) {
            monkey.setVelocityX(NORMAL_MOVE_SPEED * 1.5f);
        } else if (leftPressed) {
            monkey.setVelocityX(-NORMAL_MOVE_SPEED);
        }
        LOGGER.info("Normal jump performed, y=" + monkey.getY() + ", velocityX=" + monkey.getVelocityX());
    }

    private void performForwardJump() {
        float jumpVelocity = JUMP_MOVE_SPEED * JUMP_BOOST;
        monkey.setVelocityX(jumpVelocity);
        monkey.jumpForward();
        jumpInProgress = true;
        monkey.setFacingRight(true);
        LOGGER.info("Forward jump performed, y=" + monkey.getY() + ", velocityX=" + monkey.getVelocityX());
    }

    private void updateMovement() {
        if (jumpInProgress && monkey.isJumping()) {
            if (rightPressed && !leftPressed) {
                float currentVelocity = monkey.getVelocityX();
                if (currentVelocity < JUMP_MOVE_SPEED) {
                    monkey.setVelocityX(Math.min(currentVelocity + 0.5f, JUMP_MOVE_SPEED));
                }
            } else if (leftPressed && !rightPressed) {
                float currentVelocity = monkey.getVelocityX();
                if (currentVelocity > -JUMP_MOVE_SPEED) {
                    monkey.setVelocityX(Math.max(currentVelocity - 0.5f, -JUMP_MOVE_SPEED));
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
                float currentVelocity = monkey.getVelocityX();
                monkey.setVelocityX(currentVelocity * 0.98f);
            }
        }

        if (monkey.isOnGround()) {
            jumpInProgress = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public void reset() {
        leftPressed = false;
        rightPressed = false;
        spacePressed = false;
        jumpInProgress = false;
    }
}