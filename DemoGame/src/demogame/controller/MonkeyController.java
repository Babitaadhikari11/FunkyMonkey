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
    
}
