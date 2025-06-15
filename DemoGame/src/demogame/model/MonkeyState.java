package demogame.model;

public class MonkeyState {
    // Animation frame ranges for each state
    public static final int[] IDLE_FRAMES = {1, 2, 3, 4};
    public static final int[] WALKING_FRAMES = {5, 6, 7, 8};
    public static final int[] JUMPING_FRAMES = {9, 10, 11};
    public static final int[] FALLING_FRAMES = {12, 13, 14};
    public static final int VICTORY_FRAME = 16;
    
    public enum State {
        IDLE,
        WALKING,
        JUMPING,
        FALLING,
        VICTORY
    }
}