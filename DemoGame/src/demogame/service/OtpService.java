package demogame.service;

import javax.mail.*;
import javax.mail.internet.*;
import java.sql.*;
import java.security.SecureRandom;
import java.util.Properties;

public class OtpService {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final String FROM_EMAIL = "babeetaadhikari123@gmail.com"; // Replace with your Gmail
    private static final String FROM_PASSWORD = "ndes kgsv gjdq swuj";      // Replace with your App Password
    private static final int OTP_EXPIRY_MINUTES = 10;

    // Database connection (match your UserDao)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/funkymonkey?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "funkymonkey123";

    public static void sendOtp(String email) {
        String otp = generateSecureOtp();
        if (storeOtp(email, otp)) {
            if (sendEmail(email, otp)) {
                System.out.println("OTP " + otp + " sent successfully to " + email + " at " + new java.util.Date());
            } else {
                System.err.println("Failed to send OTP email to " + email);
                deleteOtp(email); // Clean up on failure
            }
        } else {
            System.err.println("Failed to store OTP for " + email);
        }
    }

    public static boolean verifyOtp(String email, String otp) {
        String url = DB_URL;
        try (Connection conn = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT otp, expires_at FROM otps WHERE email = ?")) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedOtp = rs.getString("otp");
                    Timestamp expiresAt = rs.getTimestamp("expires_at");
                    if (expiresAt != null && System.currentTimeMillis() > expiresAt.getTime()) {
                        deleteOtp(email); // Clean up expired OTP
                        return false;
                    }
                    return storedOtp.equals(otp);
                }
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String generateSecureOtp() {
        SecureRandom secureRandom = new SecureRandom();
        int otpValue = secureRandom.nextInt(900000) + 100000; // 100000 to 999999
        return String.valueOf(otpValue);
    }

    private static boolean storeOtp(String email, String otp) {
        String url = DB_URL;
        try (Connection conn = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO otps (email, otp, expires_at) VALUES (?, ?, DATE_ADD(NOW(), INTERVAL ? MINUTE)) " +
                     "ON DUPLICATE KEY UPDATE otp = ?, expires_at = DATE_ADD(NOW(), INTERVAL ? MINUTE)")) {
            stmt.setString(1, email);
            stmt.setString(2, otp);
            stmt.setInt(3, OTP_EXPIRY_MINUTES);
            stmt.setString(4, otp);
            stmt.setInt(5, OTP_EXPIRY_MINUTES);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void deleteOtp(String email) {
        String url = DB_URL;
        try (Connection conn = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM otps WHERE email = ?")) {
            stmt.setString(1, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean sendEmail(String toEmail, String otp) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.debug", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
            }
        });
        session.setDebug(true);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Your DemoGame OTP");
            message.setText("Your OTP is: " + otp + ". Valid for " + OTP_EXPIRY_MINUTES + " minutes. Do not share this code.");
            Transport.send(message);
            System.out.println("Email sent successfully to " + toEmail + " with OTP: " + otp);
            return true;
        } catch (MessagingException e) {
            System.err.println("Failed to send email to " + toEmail + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}