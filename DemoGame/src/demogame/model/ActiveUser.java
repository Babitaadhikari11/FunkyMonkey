package demogame.model;

public class ActiveUser {
    private String username;
    private int highScore;
    private int gamesPlayed;

    public ActiveUser(String username, int highScore, int gamesPlayed) {
        this.username = username;
        this.highScore = highScore;
        this.gamesPlayed = gamesPlayed;
    }

    public String getUsername() { 
        return username; 
    }
    public int getHighScore() { 
        return highScore; 
    }
    public int getGamesPlayed() { 
       return gamesPlayed; 
    }
}