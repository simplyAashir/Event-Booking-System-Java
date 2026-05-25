package service;

import dao.UserDAO;
import model.Customer;
import model.User;

public class UserService {
    private UserDAO userDAO;
    
    public UserService() {
        this.userDAO = new UserDAO();
    }
    
    // Get customer by ID
    public Customer getCustomerById(String customerId) {
        try {
            // First get the base user
            User user = getUserById(customerId);
            
            if (user instanceof Customer) {
                return (Customer) user;
            }
            
            return null;
            
        } catch (Exception e) {
            System.err.println("Error getting customer by ID: " + e.getMessage());
            return null;
        }
    }
    
    // Get user by ID (you'll need to add this to UserDAO)
    public User getUserById(String userId) {
        // This method should be implemented in UserDAO
        // For now, we'll create a simple implementation
        return userDAO.getUserById(userId);
    }
    
    // Update customer profile
    public boolean updateCustomerProfile(Customer customer) {
        try {
            // Update in database
            return userDAO.updateUserProfile(customer);
            
        } catch (Exception e) {
            System.err.println("Error updating customer profile: " + e.getMessage());
            return false;
        }
    }
    
    // Change customer password
    public boolean changeCustomerPassword(String customerId, String currentPassword, String newPassword) {
        try {
            // Verify current password
            User user = getUserById(customerId);
            if (user == null || !user.getPassword().equals(currentPassword)) {
                return false; // Invalid current password
            }
            
            // Update password in database
            return userDAO.updateUserPassword(customerId, newPassword);
            
        } catch (Exception e) {
            System.err.println("Error changing password: " + e.getMessage());
            return false;
        }
    }
}