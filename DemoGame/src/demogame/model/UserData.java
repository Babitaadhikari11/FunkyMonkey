/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demogame.model;


/**
 *
 * @author babitaadhikari
 */


public class UserData {
    private int id;
    private String username;
    private String email;
    private String password;

    // Constructor for registration (without ID)
    public UserData(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Constructor for existing user (with ID)
    public UserData(int id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}