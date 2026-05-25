package dao;

import model.Payment;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {
    
    // Create new payment
    public boolean createPayment(Payment payment) {
        String sql = "INSERT INTO payments (transaction_id, booking_id, payment_date, amount, " +
                    "payment_method, reference, status, payment_details) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, payment.getTransactionId());
            pstmt.setString(2, payment.getBookingId());
            pstmt.setTimestamp(3, new Timestamp(payment.getPaymentDate().getTime()));
            pstmt.setDouble(4, payment.getAmount());
            pstmt.setString(5, payment.getPaymentMethod());
            pstmt.setString(6, payment.getReference());
            pstmt.setString(7, payment.getStatus());
            pstmt.setString(8, payment.getPaymentDetails());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating payment: " + e.getMessage());
            return false;
        }
    }
    
    // Get payments by booking ID
    public List<Payment> getPaymentsByBooking(String bookingId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE booking_id = ? ORDER BY payment_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, bookingId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Payment payment = createPaymentFromResultSet(rs);
                payments.add(payment);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting payments by booking: " + e.getMessage());
        }
        
        return payments;
    }
    
    // Get payments by customer
    public List<Payment> getPaymentsByCustomer(String customerId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.* FROM payments p " +
                    "JOIN bookings b ON p.booking_id = b.booking_id " +
                    "WHERE b.customer_id = ? ORDER BY p.payment_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Payment payment = createPaymentFromResultSet(rs);
                payments.add(payment);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting customer payments: " + e.getMessage());
        }
        
        return payments;
    }
    
    // Update payment status
    public boolean updatePaymentStatus(String transactionId, String status) {
        String sql = "UPDATE payments SET status = ? WHERE transaction_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setString(2, transactionId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating payment status: " + e.getMessage());
            return false;
        }
    }
    
    // Get payment by ID
    public Payment getPaymentById(String transactionId) {
        String sql = "SELECT * FROM payments WHERE transaction_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, transactionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return createPaymentFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting payment: " + e.getMessage());
        }
        
        return null;
    }
    
    // Generate payment ID
    public String generatePaymentId() {
        String sql = "SELECT COUNT(*) FROM payments";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return "PAY" + String.format("%06d", count);
            }
            
        } catch (SQLException e) {
            System.err.println("Error generating payment ID: " + e.getMessage());
        }
        
        return "PAY000001";
    }
    
    // Helper method to create Payment from ResultSet
    private Payment createPaymentFromResultSet(ResultSet rs) throws SQLException {
        Payment payment = new Payment(
            rs.getString("transaction_id"),
            rs.getString("booking_id"),
            rs.getDouble("amount"),
            rs.getString("payment_method")
        );
        
        payment.setPaymentDate(rs.getTimestamp("payment_date"));
        payment.setReference(rs.getString("reference"));
        payment.setStatus(rs.getString("status"));
        payment.setPaymentDetails(rs.getString("payment_details"));
        
        return payment;
    }
}