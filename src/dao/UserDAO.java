package dao;

import model.*;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO {
    
	// Get user by ID
    public User getUserById(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = ? AND is_active = 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String userType = rs.getString("user_type");
                String fullName = rs.getString("full_name");
                String email = rs.getString("email");
                String phoneNo = rs.getString("phone_no");
                String password = rs.getString("password");
                
                switch (userType) {
                    case "CUSTOMER":
                        Customer customer = new Customer(userId, fullName, email, phoneNo, password);
                        customer.setPreferences(rs.getString("preferences"));
                        return customer;
                        
                    case "VENUE_OWNER":
                        VenueOwner owner = new VenueOwner(userId, fullName, email, phoneNo, password, rs.getString("tax_id"));
                        return owner;
                        
                    case "ADMIN":
                        Admin admin = new Admin(userId, fullName, email, phoneNo, password, rs.getString("admin_level"));
                        return admin;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    // Update user profile
    public boolean updateUserProfile(User user) {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone_no = ?";
        
        // Add user-type specific fields
        if (user instanceof Customer) {
            sql += ", preferences = ?";
        } else if (user instanceof VenueOwner) {
            sql += ", tax_id = ?";
        } else if (user instanceof Admin) {
            sql += ", admin_level = ?";
        }
        
        sql += " WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPhoneNo());
            
            int paramIndex = 4;
            if (user instanceof Customer) {
                Customer customer = (Customer) user;
                pstmt.setString(paramIndex++, customer.getPreferences());
            } else if (user instanceof VenueOwner) {
                VenueOwner owner = (VenueOwner) user;
                pstmt.setString(paramIndex++, owner.getTaxId());
            } else if (user instanceof Admin) {
                Admin admin = (Admin) user;
                pstmt.setString(paramIndex++, admin.getAdminLevel());
            }
            
            pstmt.setString(paramIndex, user.getUserId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating user profile: " + e.getMessage());
            return false;
        }
    }
    
    // Update user password
    public boolean updateUserPassword(String userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPassword); // In production, hash this password!
            pstmt.setString(2, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating user password: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateUserStatus(String userId, boolean isActive) {
        String sql = "UPDATE users SET is_active = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, isActive);
            pstmt.setString(2, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating user status: " + e.getMessage());
            return false;
        }
    }
    
    // Register new user
    public boolean registerUser(User user, String userType) {
        String sql = "INSERT INTO users (user_id, full_name, email, phone_no, password, user_type, preferences, tax_id, admin_level) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getFullName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPhoneNo());
            pstmt.setString(5, user.getPassword()); // In production, hash this!
            pstmt.setString(6, userType);
            
            // Set user-type specific fields
            if (user instanceof Customer) {
                Customer customer = (Customer) user;
                pstmt.setString(7, customer.getPreferences());
                pstmt.setString(8, null);
                pstmt.setString(9, null);
            } else if (user instanceof VenueOwner) {
                VenueOwner owner = (VenueOwner) user;
                pstmt.setString(7, null);
                pstmt.setString(8, owner.getTaxId());
                pstmt.setString(9, null);
            } else if (user instanceof Admin) {
                Admin admin = (Admin) user;
                pstmt.setString(7, null);
                pstmt.setString(8, null);
                pstmt.setString(9, admin.getAdminLevel());
            }
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }
    
    // Login user
    public User login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ? AND is_active = 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, password); // In production, compare hashed passwords
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String userType = rs.getString("user_type");
                String userId = rs.getString("user_id");
                String fullName = rs.getString("full_name");
                String phoneNo = rs.getString("phone_no");
                
                switch (userType) {
                    case "CUSTOMER":
                        Customer customer = new Customer(userId, fullName, email, phoneNo, password);
                        customer.setPreferences(rs.getString("preferences"));
                        return customer;
                        
                    case "VENUE_OWNER":
                        VenueOwner owner = new VenueOwner(userId, fullName, email, phoneNo, password, rs.getString("tax_id"));
                        return owner;
                        
                    case "ADMIN":
                        Admin admin = new Admin(userId, fullName, email, phoneNo, password, rs.getString("admin_level"));
                        return admin;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
        }
        
        return null; // Login failed
    }
    
    // Check if email exists
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
        }
        
        return false;
    }
    
    // Generate unique user ID
    public String generateUserId(String userType) {
        String prefix = "";
        switch (userType) {
            case "CUSTOMER": prefix = "CUST"; break;
            case "VENUE_OWNER": prefix = "OWN"; break;
            case "ADMIN": prefix = "ADM"; break;
        }
        
        String sql = "SELECT COUNT(*) FROM users WHERE user_type = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userType);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return prefix + String.format("%03d", count);
            }
            
        } catch (SQLException e) {
            System.err.println("Error generating user ID: " + e.getMessage());
        }
        
        return prefix + "001";
    }
    
 // Get all users with filtering and pagination
    public List<User> getAllUsers(String userTypeFilter, String statusFilter, String searchText, int limit, int offset) {
        List<User> users = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE 1=1");
        List<Object> parameters = new ArrayList<>();
        
        if (userTypeFilter != null && !"ALL".equals(userTypeFilter)) {
            sql.append(" AND user_type = ?");
            parameters.add(userTypeFilter);
        }
        
        if (statusFilter != null && !"ALL".equals(statusFilter)) {
            sql.append(" AND is_active = ?");
            parameters.add("ACTIVE".equals(statusFilter) ? 1 : 0);
        }
        
        if (searchText != null && !searchText.trim().isEmpty()) {
            sql.append(" AND (full_name LIKE ? OR email LIKE ? OR user_id LIKE ?)");
            parameters.add("%" + searchText + "%");
            parameters.add("%" + searchText + "%");
            parameters.add("%" + searchText + "%");
        }
        
        sql.append(" ORDER BY registration_date DESC LIMIT ? OFFSET ?");
        parameters.add(limit);
        parameters.add(offset);
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                User user = createUserFromResultSet(rs);
                users.add(user);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        
        return users;
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        String userType = rs.getString("user_type");
        String userId = rs.getString("user_id");
        String fullName = rs.getString("full_name");
        String email = rs.getString("email");
        String phoneNo = rs.getString("phone_no");
        String password = rs.getString("password");
        
        switch (userType) {
            case "CUSTOMER":
                Customer customer = new Customer(userId, fullName, email, phoneNo, password);
                customer.setPreferences(rs.getString("preferences"));
                // Remove the lines that call methods that don't exist
                // customer.setRegistrationDate(rs.getTimestamp("registration_date"));
                // customer.setActive(rs.getBoolean("is_active"));
                return customer;
                
            case "VENUE_OWNER":
                VenueOwner owner = new VenueOwner(userId, fullName, email, phoneNo, password, rs.getString("tax_id"));
                // Remove the lines that call methods that don't exist
                // owner.setBusinessName(rs.getString("business_name"));
                // owner.setRegistrationDate(rs.getTimestamp("registration_date"));
                // owner.setActive(rs.getBoolean("is_active"));
                return owner;
                
            case "ADMIN":
                Admin admin = new Admin(userId, fullName, email, phoneNo, password, rs.getString("admin_level"));
                // Remove the lines that call methods that don't exist
                // admin.setRegistrationDate(rs.getTimestamp("registration_date"));
                // admin.setActive(rs.getBoolean("is_active"));
                return admin;
                
            default:
                return null;
        }
    }
	// Get user statistics
    public Map<String, Integer> getUserStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT user_type, COUNT(*) as count FROM users WHERE is_active = 1 GROUP BY user_type";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                stats.put(rs.getString("user_type"), rs.getInt("count"));
            }
            
            // Add total
            sql = "SELECT COUNT(*) as total FROM users WHERE is_active = 1";
            try (PreparedStatement pstmt2 = conn.prepareStatement(sql)) {
                ResultSet rs2 = pstmt2.executeQuery();
                if (rs2.next()) {
                    stats.put("totalUsers", rs2.getInt("total"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user statistics: " + e.getMessage());
        }
        
        return stats;
    }
    
    
}