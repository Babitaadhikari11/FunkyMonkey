package demogame.model;

public class MonkeyState {
      // Animation frame ranges for each state
    public static final int[] IDLE_FRAMES = {0, 1, 2, 3, 4, 5}; // 6 frames for idle
    public static final int[] WALKING_FRAMES = {6, 7, 8, 9, 10, 11}; // 6 frames for walking
    public static final int[] JUMPING_FRAMES = {12, 13, 14, 15}; // 4 frames for jumping
    public static final int[] FALLING_FRAMES = {16, 17, 18, 19}; // 4 frames for falling
    public static final int[] VICTORY_FRAMES = {20, 21, 22, 23}; // 4 frames for victory
     public enum State {
        IDLE,
        WALKING,
        JUMPING,
        FALLING,
        VICTORY
    }
    
}
