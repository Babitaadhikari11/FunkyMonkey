package demogame.model;

public class TutorialOverlay {
    private boolean isVisible;
    private static final String[] INSTRUCTIONS = {
        "← → Arrow Keys: Move Left/Right",
        "SPACE: Jump",
        "P: Pause Game",
        "Quit: To return to Menu",
        "Press any key to continue..."
    };

    public TutorialOverlay() {
        this.isVisible = true;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public String[] getInstructions() {
        return INSTRUCTIONS;
    }
}