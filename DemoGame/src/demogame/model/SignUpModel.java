package demogame.model;

import demogame.dao.SignUpDAO;

import java.sql.SQLException;

public class SignUpModel {
    private SignUpDAO signUpDAO;
    private String errorMessage;

    public SignUpModel() {
        this.signUpDAO = new SignUpDAO();
    }

    public boolean register(String username, String email, String password) {
        this.errorMessage = null;

        if (username == null || username.trim().isEmpty()) {
            this.errorMessage = "Username cannot be empty.";
            return false;
        }
        if (email == null || email.trim().isEmpty()) {
            this.errorMessage = "Email cannot be empty.";
            return false;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            this.errorMessage = "Invalid email format.";
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            this.errorMessage = "Password cannot be empty.";
            return false;
        }

        try {
            return signUpDAO.registerUser(username, email, password);
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("username")) {
                this.errorMessage = "Username already exists.";
            } else if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("email")) {
                this.errorMessage = "Email already exists.";
            } else {
                this.errorMessage = "Database error: " + e.getMessage();
            }
            return false;
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}