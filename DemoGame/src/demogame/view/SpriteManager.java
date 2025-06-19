package demogame.view;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class SpriteManager {
    private Map<Integer, BufferedImage> sprites;
    private static final int TOTAL_FRAMES = 14; // Adjust based on  sprite count

    public SpriteManager() {
        sprites = new HashMap<>();
        loadSprites();
    }
     private void loadSprites() {
        try {
            for (int i = 1; i <= TOTAL_FRAMES; i++) {
                String imagePath = "/resources/" + i + ".png";
                try {
                    BufferedImage image = ImageIO.read(getClass().getResourceAsStream(imagePath));
                    if (image != null) {
                        sprites.put(i, image);
                        System.out.println("Successfully loaded sprite: " + i);
                    } else {
                        System.err.println("Failed to load sprite: " + i);
                    }
                } catch (IOException e) {
                    System.err.println("Error loading sprite " + i + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error in sprite loading: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
}
