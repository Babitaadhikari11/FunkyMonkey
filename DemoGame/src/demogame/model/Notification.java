package demogame.model;

import java.io.Serializable;


public class Notification implements Serializable {
    private int id; // Unique identifier for the notification
    private String message; // Notification message content

    // Constructor for creating a notification with a message
    public Notification(String message) {
        this.message = message;
    }

    // Constructor for creating a notification with ID and message (from database)
    public Notification(int id, String message) {
        this.id = id;
        this.message = message;
    }

    // Getter for ID
    public int getId() {
        return id;
    }

    // Setter for ID
    public void setId(int id) {
        this.id = id;
    }

    // Getter for message
    public String getMessage() {
        return message;
    }

    // Setter for message
    public void setMessage(String message) {
        this.message = message;
    }
}