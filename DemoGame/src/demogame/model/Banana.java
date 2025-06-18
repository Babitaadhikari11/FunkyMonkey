package demogame.model;
import java.awt.Rectangle;
public class Banana {
     private int x;
    private int y;
    private final int WIDTH = 30;
    private final int HEIGHT = 30;
    private boolean isCollected;
    private Rectangle bounds;
    private final int SPEED = 3;

    public Banana(int x, int y) {
        this.x = x;
        this.y = y;
        this.isCollected = false;
        this.bounds = new Rectangle(x, y, WIDTH, HEIGHT);
    }
     public void update() {
        x -= SPEED;
        bounds.setLocation(x, y);
    }
    // Getters and Setters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return WIDTH; }
    public int getHeight() { return HEIGHT; }
    public boolean isCollected() { return isCollected; }
    public void setCollected(boolean collected) { isCollected = collected; }
    public Rectangle getBounds() { return bounds; }
    
}
