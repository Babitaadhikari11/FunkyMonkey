package demogame.model;

public class UserData {
    private int id;
    private String username;
    private String email;
    private String password;
    private String role;

    
    public UserData(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = "player"; // Default role
    }


    public UserData(int id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = "player"; // Default role
    }

   
    public UserData(int id, String username, String email, String password, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role != null ? role : "player";
    }

    // All  getters and setters
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
    public String getRole() { 
        return role; 
    }
    public void setRole(String role) { 
        this.role = role; 
    }
}