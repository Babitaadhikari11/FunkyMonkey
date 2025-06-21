package demogame.model;

import java.time.LocalDateTime;

public class Score {
    private int id;
    private int userId;
    private int score;
    private LocalDateTime dateCreated;

    public Score() {
        this.score = 0;
        this.dateCreated = LocalDateTime.now();
    }

    public Score(int userId, int score) {
        this.userId = userId;
        this.score = score;
        this.dateCreated = LocalDateTime.now();
    }

    // Getters and setters
    public int getId() { 
        return id; 
    }
    public void setId(int id) {
         this.id = id; 
        }
    public int getUserId() { 
        return userId; 
    }
    public void setUserId(int userId) {
         this.userId = userId;
         }
    public int getScore() {
         return score; 
        }
    public void setScore(int score) { 
        this.score = score; 
    }
    public LocalDateTime getDateCreated() {
         return dateCreated; 
        }
    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void addPoints(int points) {
        this.score += points;
    }

    public void reset() {
        this.score = 0;
    }
}