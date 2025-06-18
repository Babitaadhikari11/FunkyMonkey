package demogame.model;
import java.awt.Rectangle;
public class Obstacle {
    // Constants for obstacle properties
    private static final int SPEED = 3;
    private static final int COLLISION_INSET = 15;
    private static final int VERTICAL_OFFSET = 40;
    private static final int MONKEY_DEFAULT_SIZE = 120;
    private static final int JUMP_CLEARANCE = 40;
     // Size presets - correctly defined
    private static final class SizePreset {
        final int width;
        final int height;
        
        SizePreset(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    private static final SizePreset[] SIZE_PRESETS = {
        new SizePreset(60, 80),   // Small obstacle
        new SizePreset(80, 100),  // Medium obstacle
        new SizePreset(100, 120)  // Large obstacle
    };


    
}
