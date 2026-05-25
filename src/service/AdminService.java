package service;

import dao.UserDAO;
import dao.VenueDAO;
import dao.BookingDAO;
import dao.PaymentDAO;
import model.User;
import model.Booking;
import java.util.*;

public class AdminService {
    private UserDAO userDAO;
    private VenueDAO venueDAO;
    private BookingDAO bookingDAO;
    private PaymentDAO paymentDAO;
    
    public AdminService() {
        this.userDAO = new UserDAO();
        this.venueDAO = new VenueDAO();
        this.bookingDAO = new BookingDAO();
        this.paymentDAO = new PaymentDAO();
    }
    
    // Get real system statistics from database
    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Get user statistics
            Map<String, Integer> userStats = userDAO.getUserStatistics();
            stats.put("totalUsers", userStats.getOrDefault("totalUsers", 0));
            stats.put("totalCustomers", userStats.getOrDefault("CUSTOMER", 0));
            stats.put("totalOwners", userStats.getOrDefault("VENUE_OWNER", 0));
            stats.put("totalAdmins", userStats.getOrDefault("ADMIN", 0));
            
            // Get venue statistics
            Map<String, Integer> venueStats = venueDAO.getVenueStatistics();
            stats.put("totalVenues", venueStats.getOrDefault("totalVenues", 0));
            stats.put("activeVenues", venueStats.getOrDefault("ACTIVE", 0));
            stats.put("inactiveVenues", venueStats.getOrDefault("INACTIVE", 0));
            
            // Get booking statistics
            Map<String, Integer> bookingStats = bookingDAO.getBookingStatistics();
            stats.put("totalBookings", bookingStats.getOrDefault("totalBookings", 0));
            stats.put("approvedBookings", bookingStats.getOrDefault("APPROVED", 0));
            stats.put("pendingBookings", bookingStats.getOrDefault("PENDING", 0));
            stats.put("rejectedBookings", bookingStats.getOrDefault("REJECTED", 0));
            stats.put("cancelledBookings", bookingStats.getOrDefault("CANCELLED", 0));
            stats.put("completedBookings", bookingStats.getOrDefault("COMPLETED", 0));
            
            // Get total revenue (you'll need to add this method to PaymentDAO)
            double totalRevenue = getTotalRevenue();
            stats.put("totalRevenue", totalRevenue);
            
        } catch (Exception e) {
            System.err.println("Error getting system stats: " + e.getMessage());
            setDefaultStats(stats);
        }
        
        return stats;
    }
    
    // Get total revenue from payments
    private double getTotalRevenue() {
        // You'll need to add this method to PaymentDAO
        // For now, using a placeholder calculation
        String sql = "SELECT SUM(amount) as total_revenue FROM payments WHERE status = 'COMPLETED'";
        // Implement this query in PaymentDAO
        return 45250.75; // Placeholder
    }
    
    private void setDefaultStats(Map<String, Object> stats) {
        stats.put("totalUsers", 0);
        stats.put("totalVenues", 0);
        stats.put("totalBookings", 0);
        stats.put("totalRevenue", 0.0);
        stats.put("approvedBookings", 0);
        stats.put("pendingBookings", 0);
        stats.put("rejectedBookings", 0);
        stats.put("activeVenues", 0);
        stats.put("totalCustomers", 0);
        stats.put("totalOwners", 0);
        stats.put("totalAdmins", 0);
        stats.put("inactiveVenues", 0);
        stats.put("cancelledBookings", 0);
        stats.put("completedBookings", 0);
    }
    
    // Get all users with filtering and pagination
    public List<User> getAllUsers(String userTypeFilter, String statusFilter, String searchText, int page, int pageSize) {
        try {
            int offset = page * pageSize;
            return userDAO.getAllUsers(userTypeFilter, statusFilter, searchText, pageSize, offset);
        } catch (Exception e) {
            System.err.println("Error getting users: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Get total user count for pagination
    public int getTotalUsersCount(String userTypeFilter, String statusFilter, String searchText) {
        try {
            // You'll need to add this method to UserDAO
            List<User> allUsers = userDAO.getAllUsers(userTypeFilter, statusFilter, searchText, Integer.MAX_VALUE, 0);
            return allUsers.size();
        } catch (Exception e) {
            System.err.println("Error getting user count: " + e.getMessage());
            return 0;
        }
    }
    
 // Get all bookings with filtering and pagination
    public List<Booking> getAllBookings(String statusFilter, String searchText, Date fromDate, Date toDate, int page, int pageSize) {
        try {
            int offset = page * pageSize;
            // Convert java.util.Date to java.sql.Date
            java.sql.Date sqlFromDate = fromDate != null ? new java.sql.Date(fromDate.getTime()) : null;
            java.sql.Date sqlToDate = toDate != null ? new java.sql.Date(toDate.getTime()) : null;
            
            return bookingDAO.getBookingsWithFilters(statusFilter, searchText, sqlFromDate, sqlToDate, pageSize, offset);
        } catch (Exception e) {
            System.err.println("Error getting bookings: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    // Get total booking count for pagination
 // Get total booking count for pagination
    public int getTotalBookingsCount(String statusFilter, String searchText, Date fromDate, Date toDate) {
        try {
            // Convert java.util.Date to java.sql.Date
            java.sql.Date sqlFromDate = fromDate != null ? new java.sql.Date(fromDate.getTime()) : null;
            java.sql.Date sqlToDate = toDate != null ? new java.sql.Date(toDate.getTime()) : null;
            
            List<Booking> allBookings = bookingDAO.getBookingsWithFilters(statusFilter, searchText, sqlFromDate, sqlToDate, Integer.MAX_VALUE, 0);
            return allBookings.size();
        } catch (Exception e) {
            System.err.println("Error getting booking count: " + e.getMessage());
            return 0;
        }
    }
    
    // Update user status
    public boolean updateUserStatus(String userId, boolean isActive) {
        try {
            return userDAO.updateUserStatus(userId, isActive);
        } catch (Exception e) {
            System.err.println("Error updating user status: " + e.getMessage());
            return false;
        }
    }
    
    // Create new user
    public boolean createUser(User user, String userType) {
        try {
            return userDAO.registerUser(user, userType);
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            return false;
        }
    }
    
    // Update booking status
    public boolean updateBookingStatus(String bookingId, String status, String reason) {
        try {
            boolean success = bookingDAO.updateBookingStatus(bookingId, status);
            if (success && reason != null && !reason.trim().isEmpty()) {
                // Log the reason (you might want to add a booking_notes table)
                System.out.println("Booking " + bookingId + " updated to " + status + ". Reason: " + reason);
            }
            return success;
        } catch (Exception e) {
            System.err.println("Error updating booking status: " + e.getMessage());
            return false;
        }
    }
    
    // Generate reports
    public Map<String, Object> generateSystemReport(String reportType, Map<String, Object> parameters) {
        Map<String, Object> report = new HashMap<>();
        
        try {
            switch (reportType) {
                case "BOOKING_SUMMARY":
                    report.put("data", generateBookingSummaryReport(parameters));
                    break;
                case "REVENUE":
                    report.put("data", generateRevenueReport(parameters));
                    break;
                case "USER_ACTIVITY":
                    report.put("data", generateUserActivityReport(parameters));
                    break;
                default:
                    report.put("error", "Unknown report type: " + reportType);
            }
            
            report.put("generatedAt", new Date());
            report.put("reportType", reportType);
            
        } catch (Exception e) {
            report.put("error", "Error generating report: " + e.getMessage());
        }
        
        return report;
    }
    
    private Object generateBookingSummaryReport(Map<String, Object> parameters) {
        // Implement booking summary report logic
        return "Booking Summary Report Data";
    }
    
    private Object generateRevenueReport(Map<String, Object> parameters) {
        // Implement revenue report logic
        return "Revenue Report Data";
    }
    
    private Object generateUserActivityReport(Map<String, Object> parameters) {
        // Implement user activity report logic
        return "User Activity Report Data";
    }
}