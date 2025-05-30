package demogame.model;

import demogame.dao.LoginDAO;

import java.sql.SQLException;

public class LoginModel {
    private LoginDAO loginDAO;
    private String errorMessage;
    private int userId;

    public LoginModel() {
        this.loginDAO = new LoginDAO();
    }

    public boolean validateLogin(String username, String password) {
        this.errorMessage = null;
        this.userId = -1;

        if (username == null || username.trim().isEmpty()) {
            this.errorMessage = "Username cannot be empty.";
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            this.errorMessage = "Password cannot be empty.";
            return false;
        }

        try {
            int userId = loginDAO.validateCredentials(username, password);
            if (userId != -1) {
                this.userId = userId;
                loginDAO.logLoginAttempt(userId);
                return true;
            } else {
                this.errorMessage = "Invalid username or password.";
                return false;
            }
        } catch (SQLException e) {
            this.errorMessage = "Database error: " + e.getMessage();
            return false;
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getUserId() {
        return userId;
    }
}