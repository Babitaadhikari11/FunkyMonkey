package demogame.model;

public class GameTip {
    private int id;
    private String tipText;

    public GameTip(int id, String tipText) {
        this.id = id;
        this.tipText = tipText;
    }

    // Getters and setters
    public int getId() { 
        return id; 
    }
    public String getTipText() { 
        return tipText; 
    }

    
}
