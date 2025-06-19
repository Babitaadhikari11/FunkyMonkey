package demogame.view;
//import demogame.model.Banana;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
public class BananaView {
       private Image bananaImage;
    private final Color SCORE_COLOR = Color.WHITE;
    private final Font SCORE_FONT = new Font("Arial", Font.BOLD, 20);

    public BananaView() {
        loadBananaImage();
    }

    private void loadBananaImage() {
        try {
            bananaImage = new ImageIcon(getClass().getResource("/resources/banana.png")).getImage();
        } catch (Exception e) {
            System.err.println("Error loading banana image: " + e.getMessage());
        }
    }

    
}
