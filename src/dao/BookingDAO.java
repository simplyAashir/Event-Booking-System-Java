package dao;

import model.Booking;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingDAO {
    
    // Create new booking
    public boolean createBooking(Booking booking) {
        String sql = "INSERT INTO bookings (booking_id, customer_id, venue_id, event_date, start_time, end_time, " +
                    "number_of_guests, total_price, status, special_requests, event_type, event_name, " +
                    "contact_person, contact_phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, booking.getBookingId());
            pstmt.setString(2, booking.getCustomerId());
            pstmt.setString(3, booking.getVenueId());
            pstmt.setDate(4, new java.sql.Date(booking.getEventDate().getTime()));
            pstmt.setString(5, booking.getStartTime());
            pstmt.setString(6, booking.getEndTime());
            pstmt.setInt(7, booking.getNumberOfGuests());
            pstmt.setDouble(8, booking.getTotalPrice());
            pstmt.setString(9, booking.getStatus());
            pstmt.setString(10, booking.getSpecialRequests());
            pstmt.setString(11, booking.getEventType());
            pstmt.setString(12, booking.getEventName());
            pstmt.setString(13, booking.getContactPerson());
            pstmt.setString(14, booking.getContactPhone());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating booking: " + e.getMessage());
            return false;
        }
    }
    
    // Get bookings by customer
    public List<Booking> getBookingsByCustomer(String customerId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE customer_id = ? ORDER BY event_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Booking booking = createBookingFromResultSet(rs);
                bookings.add(booking);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting customer bookings: " + e.getMessage());
        }
        
        return bookings;
    }
    
    // Get bookings by venue owner
    public List<Booking> getBookingsByVenueOwner(String ownerId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.* FROM bookings b " +
                    "JOIN venues v ON b.venue_id = v.venue_id " +
                    "WHERE v.owner_id = ? ORDER BY b.event_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ownerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Booking booking = createBookingFromResultSet(rs);
                bookings.add(booking);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting owner bookings: " + e.getMessage());
        }
        
        return bookings;
    }
    
    // Get all bookings (for admin)
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Booking booking = createBookingFromResultSet(rs);
                bookings.add(booking);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all bookings: " + e.getMessage());
        }
        
        return bookings;
    }
    
    // Update booking status
    public boolean updateBookingStatus(String bookingId, String status) {
        String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setString(2, bookingId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating booking status: " + e.getMessage());
            return false;
        }
    }
    
    // Get booking by ID
    public Booking getBookingById(String bookingId) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, bookingId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return createBookingFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting booking: " + e.getMessage());
        }
        
        return null;
    }
    
    // Generate booking ID
    public String generateBookingId() {
        String sql = "SELECT COUNT(*) FROM bookings";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return "BOOK" + String.format("%05d", count);
            }
            
        } catch (SQLException e) {
            System.err.println("Error generating booking ID: " + e.getMessage());
        }
        
        return "BOOK00001";
    }
    
    // Helper method to create Booking from ResultSet
    private Booking createBookingFromResultSet(ResultSet rs) throws SQLException {
        Booking booking = new Booking(
            rs.getString("booking_id"),
            rs.getString("customer_id"),
            rs.getString("venue_id"),
            rs.getDate("event_date"),
            rs.getString("start_time"),
            rs.getString("end_time"),
            rs.getInt("number_of_guests"),
            rs.getDouble("total_price")
        );
        
        booking.setBookingDate(rs.getTimestamp("booking_date"));
        booking.setStatus(rs.getString("status"));
        booking.setSpecialRequests(rs.getString("special_requests"));
        booking.setEventType(rs.getString("event_type"));
        booking.setEventName(rs.getString("event_name"));
        booking.setContactPerson(rs.getString("contact_person"));
        booking.setContactPhone(rs.getString("contact_phone"));
        booking.setCreatedAt(rs.getTimestamp("created_at"));
        
        return booking;
    }
    
 // Get bookings with advanced filtering for admin
    public List<Booking> getBookingsWithFilters(String statusFilter, String searchText, Date fromDate, Date toDate, int limit, int offset) {
        List<Booking> bookings = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder("SELECT * FROM bookings WHERE 1=1");
        List<Object> parameters = new ArrayList<>();
        
        if (statusFilter != null && !"ALL".equals(statusFilter)) {
            sql.append(" AND status = ?");
            parameters.add(statusFilter);
        }
        
        if (fromDate != null) {
            sql.append(" AND event_date >= ?");
            parameters.add(new java.sql.Date(fromDate.getTime()));
        }
        
        if (toDate != null) {
            sql.append(" AND event_date <= ?");
            parameters.add(new java.sql.Date(toDate.getTime()));
        }
        
        if (searchText != null && !searchText.trim().isEmpty()) {
            sql.append(" AND (event_name LIKE ? OR customer_id LIKE ? OR venue_id LIKE ? OR booking_id LIKE ?)");
            parameters.add("%" + searchText + "%");
            parameters.add("%" + searchText + "%");
            parameters.add("%" + searchText + "%");
            parameters.add("%" + searchText + "%");
        }
        
        sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");
        parameters.add(limit);
        parameters.add(offset);
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Booking booking = createBookingFromResultSet(rs);
                bookings.add(booking);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting filtered bookings: " + e.getMessage());
        }
        
        return bookings;
    }

    // Get booking statistics
    public Map<String, Integer> getBookingStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT status, COUNT(*) as count FROM bookings GROUP BY status";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                stats.put(rs.getString("status"), rs.getInt("count"));
            }
            
            // Add total
            sql = "SELECT COUNT(*) as total FROM bookings";
            try (PreparedStatement pstmt2 = conn.prepareStatement(sql)) {
                ResultSet rs2 = pstmt2.executeQuery();
                if (rs2.next()) {
                    stats.put("totalBookings", rs2.getInt("total"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting booking statistics: " + e.getMessage());
        }
        
        return stats;
    }
}