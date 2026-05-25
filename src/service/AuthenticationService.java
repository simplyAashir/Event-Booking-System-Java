package service;

import dao.UserDAO;
import model.User;
import java.util.Map;
import dao.PasswordResetDAO;

public class AuthenticationService {
    private UserDAO userDAO;
    private PasswordResetDAO passwordResetDAO;
    
    public AuthenticationService() {
        this.userDAO = new UserDAO();
        this.passwordResetDAO = new PasswordResetDAO();
    }
    
    // Register user
    public User register(String userType, Map<String, String> details) {
        // Validate input
        if (!validateRegistrationDetails(details)) {
            return null;
        }
        
        // Check if email already exists
        if (userDAO.emailExists(details.get("email"))) {
            System.err.println("Email already registered");
            return null;
        }
        
        // Generate user ID
        String userId = userDAO.generateUserId(userType);
        
        // Create user object based on type
        User user = null;
        switch (userType) {
            case "CUSTOMER":
                user = new model.Customer(userId, details.get("fullName"), details.get("email"), 
                                        details.get("phoneNo"), details.get("password"));
                ((model.Customer) user).setPreferences(details.get("preferences"));
                break;
                
            case "VENUE_OWNER":
                user = new model.VenueOwner(userId, details.get("fullName"), details.get("email"), 
                                          details.get("phoneNo"), details.get("password"), 
                                          details.get("taxId"));
                break;
                
            case "ADMIN":
                user = new model.Admin(userId, details.get("fullName"), details.get("email"), 
                                     details.get("phoneNo"), details.get("password"), 
                                     details.get("adminLevel"));
                break;
        }
        
        // Save to database
        if (user != null && userDAO.registerUser(user, userType)) {
            System.out.println("✅ User registered successfully: " + user.getEmail());
            return user;
        }
        
        return null;
    }
    
    // Login user
    public User login(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.err.println("Email and password are required");
            return null;
        }
        
        User user = userDAO.login(email, password);
        if (user != null) {
            System.out.println("✅ Login successful: " + user.getEmail());
        } else {
            System.err.println("❌ Login failed: Invalid credentials");
        }
        
        return user;
    }
    
    // Validate registration details
    private boolean validateRegistrationDetails(Map<String, String> details) {
        if (details.get("fullName") == null || details.get("fullName").trim().isEmpty()) {
            System.err.println("Full name is required");
            return false;
        }
        if (details.get("email") == null || !details.get("email").contains("@")) {
            System.err.println("Valid email is required");
            return false;
        }
        if (details.get("password") == null || details.get("password").length() < 6) {
            System.err.println("Password must be at least 6 characters");
            return false;
        }
        return true;
    }
    
    // Forgot password (basic implementation)
    public boolean forgotPassword(String email) {
        if (email == null || email.trim().isEmpty()) {
            System.err.println("Email is required");
            return false;
        }
        
        // Check if email exists
        if (!userDAO.emailExists(email)) {
            System.err.println("Email not found: " + email);
            return false; // Don't reveal that email doesn't exist for security
        }
        
        // Generate reset token
        String resetToken = passwordResetDAO.generateResetToken(email);
        
        if (resetToken != null) {
            // In a real application, you would send an email here
            // For simulation, we'll just display the token
            System.out.println("📧 Password Reset Simulation:");
            System.out.println("   To: " + email);
            System.out.println("   Reset Link: http://localhost:8080/reset-password?token=" + resetToken);
            System.out.println("   Token: " + resetToken);
            System.out.println("   (This token expires in 1 hour)");
            
            // For demo purposes, we'll store the token somewhere accessible
            // In real app, this would be sent via email
            DemoTokenStorage.setLastResetToken(resetToken);
            DemoTokenStorage.setUserEmail(email);
            
            return true;
        }
        
        return false;
    }
    
    // Reset password with token
    public boolean resetPassword(String token, String newPassword) {
        if (token == null || token.trim().isEmpty()) {
            System.err.println("Reset token is required");
            return false;
        }
        
        if (newPassword == null || newPassword.length() < 6) {
            System.err.println("Password must be at least 6 characters");
            return false;
        }
        
        // Validate token
        if (!passwordResetDAO.validateResetToken(token)) {
            System.err.println("Invalid or expired reset token");
            return false;
        }
        
        // Get user ID from token
        String userId = passwordResetDAO.getUserIdFromToken(token);
        if (userId == null) {
            System.err.println("Cannot find user for this token");
            return false;
        }
        
        // Update password
        boolean passwordUpdated = passwordResetDAO.updateUserPassword(userId, newPassword);
        
        if (passwordUpdated) {
            // Mark token as used
            passwordResetDAO.markTokenAsUsed(token);
            System.out.println("✅ Password reset successfully for user: " + userId);
            return true;
        }
        
        return false;
    }
    
    // Validate reset token
    public boolean validateResetToken(String token) {
        return passwordResetDAO.validateResetToken(token);
    }
}

// Helper class to simulate token storage for demo
class DemoTokenStorage {
    private static String lastResetToken;
    private static String userEmail;
    
    public static void setLastResetToken(String token) {
        lastResetToken = token;
    }
    
    public static String getLastResetToken() {
        return lastResetToken;
    }
    
    public static void setUserEmail(String email) {
        userEmail = email;
    }
    
    public static String getUserEmail() {
        return userEmail;
    }
}
