package demogame.model;

public class ResetPasswordRequest {
    private String username;
    private String password;
    private String otp;

    public ResetPasswordRequest(String username, String password, String otp) {
        this.username = username;
        this.password = password;
        this.otp = otp;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getOtp() {
        return otp;
    }
}