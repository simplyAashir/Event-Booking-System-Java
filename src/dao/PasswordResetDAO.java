package dao;

import util.DatabaseConnection;
import java.sql.*;
import java.util.UUID;

public class PasswordResetDAO {
    
    // Generate and store password reset token
    public String generateResetToken(String email) {
        // First, get user by email
        String userId = getUserIdByEmail(email);
        if (userId == null) {
            return null;
        }
        
        // Generate unique token
        String token = UUID.randomUUID().toString();
        String tokenId = "TOKEN_" + System.currentTimeMillis();
        
        // Calculate expiration (1 hour from now)
        Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + (60 * 60 * 1000));
        
        String sql = "INSERT INTO password_reset_tokens (token_id, user_id, token, expires_at) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tokenId);
            pstmt.setString(2, userId);
            pstmt.setString(3, token);
            pstmt.setTimestamp(4, expiresAt);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return token;
            }
            
        } catch (SQLException e) {
            System.err.println("Error generating reset token: " + e.getMessage());
        }
        
        return null;
    }
    
    // Validate reset token
    public boolean validateResetToken(String token) {
        String sql = "SELECT user_id FROM password_reset_tokens WHERE token = ? AND is_used = 0 AND expires_at > GETDATE()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, token);
            ResultSet rs = pstmt.executeQuery();
            
            return rs.next(); // Token is valid if found
            
        } catch (SQLException e) {
            System.err.println("Error validating reset token: " + e.getMessage());
        }
        
        return false;
    }
    
    // Get user ID from token
    public String getUserIdFromToken(String token) {
        String sql = "SELECT user_id FROM password_reset_tokens WHERE token = ? AND is_used = 0 AND expires_at > GETDATE()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, token);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("user_id");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user from token: " + e.getMessage());
        }
        
        return null;
    }
    
    // Mark token as used
    public boolean markTokenAsUsed(String token) {
        String sql = "UPDATE password_reset_tokens SET is_used = 1 WHERE token = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, token);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error marking token as used: " + e.getMessage());
        }
        
        return false;
    }
    
    // Update user password
    public boolean updateUserPassword(String userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPassword); // In production, hash this!
            pstmt.setString(2, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
        }
        
        return false;
    }
    
    // Get user ID by email
    private String getUserIdByEmail(String email) {
        String sql = "SELECT user_id FROM users WHERE email = ? AND is_active = 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("user_id");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user by email: " + e.getMessage());
        }
        
        return null;
    }
    
    public String getLatestTokenForEmail(String email) {
        String sql = "SELECT token FROM password_reset_tokens t " +
                     "JOIN users u ON t.user_id = u.user_id " +
                     "WHERE u.email = ? AND t.is_used = 0 AND t.expires_at > GETDATE() " +
                     "ORDER BY t.expires_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("token");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching latest token: " + e.getMessage());
        }
        
        return null;
    }

    
    // Clean expired tokens (optional maintenance)
    public void cleanExpiredTokens() {
        String sql = "DELETE FROM password_reset_tokens WHERE expires_at < GETDATE() OR is_used = 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error cleaning expired tokens: " + e.getMessage());
        }
    }
}