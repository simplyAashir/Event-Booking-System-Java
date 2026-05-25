package dao;

import model.Venue;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VenueDAO {
    
    // Add new venue
    public boolean addVenue(Venue venue) {
        String sql = "INSERT INTO venues (venue_id, name, location, capacity, price_per_hour, facilities, description, owner_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, venue.getVenueId());
            pstmt.setString(2, venue.getName());
            pstmt.setString(3, venue.getLocation());
            pstmt.setInt(4, venue.getCapacity());
            pstmt.setDouble(5, venue.getPricePerHour());
            pstmt.setString(6, venue.getFacilities());
            pstmt.setString(7, venue.getDescription());
            pstmt.setString(8, venue.getOwnerId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding venue: " + e.getMessage());
            return false;
        }
    }
    
    // Search venues with filters
    public List<Venue> searchVenues(String location, Integer minCapacity, Integer maxCapacity, 
                                   Double maxPrice, String eventType) {
        List<Venue> venues = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder(
            "SELECT * FROM venues WHERE status = 'ACTIVE'"
        );
        
        List<Object> parameters = new ArrayList<>();
        
        if (location != null && !location.trim().isEmpty()) {
            sql.append(" AND location LIKE ?");
            parameters.add("%" + location + "%");
        }
        
        if (minCapacity != null) {
            sql.append(" AND capacity >= ?");
            parameters.add(minCapacity);
        }
        
        if (maxCapacity != null) {
            sql.append(" AND capacity <= ?");
            parameters.add(maxCapacity);
        }
        
        if (maxPrice != null) {
            sql.append(" AND price_per_hour <= ?");
            parameters.add(maxPrice);
        }
        
        if (eventType != null && !eventType.trim().isEmpty()) {
            sql.append(" AND facilities LIKE ?");
            parameters.add("%" + eventType + "%");
        }
        
        sql.append(" ORDER BY rating DESC, name ASC");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Venue venue = new Venue(
                    rs.getString("venue_id"),
                    rs.getString("name"),
                    rs.getString("location"),
                    rs.getInt("capacity"),
                    rs.getDouble("price_per_hour"),
                    rs.getString("facilities"),
                    rs.getString("description"),
                    rs.getString("owner_id")
                );
                venue.setRating(rs.getDouble("rating"));
                venue.setStatus(rs.getString("status"));
                venue.setCreatedDate(rs.getTimestamp("created_date"));
                
                venues.add(venue);
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching venues: " + e.getMessage());
        }
        
        return venues;
    }
    
    // Get venue by ID
    public Venue getVenueById(String venueId) {
        String sql = "SELECT * FROM venues WHERE venue_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, venueId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Venue venue = new Venue(
                    rs.getString("venue_id"),
                    rs.getString("name"),
                    rs.getString("location"),
                    rs.getInt("capacity"),
                    rs.getDouble("price_per_hour"),
                    rs.getString("facilities"),
                    rs.getString("description"),
                    rs.getString("owner_id")
                );
                venue.setRating(rs.getDouble("rating"));
                venue.setStatus(rs.getString("status"));
                venue.setCreatedDate(rs.getTimestamp("created_date"));
                
                return venue;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting venue: " + e.getMessage());
        }
        
        return null;
    }
    
    // Get venues by owner
    public List<Venue> getVenuesByOwner(String ownerId) {
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT * FROM venues WHERE owner_id = ? ORDER BY created_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ownerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Venue venue = new Venue(
                    rs.getString("venue_id"),
                    rs.getString("name"),
                    rs.getString("location"),
                    rs.getInt("capacity"),
                    rs.getDouble("price_per_hour"),
                    rs.getString("facilities"),
                    rs.getString("description"),
                    rs.getString("owner_id")
                );
                venue.setRating(rs.getDouble("rating"));
                venue.setStatus(rs.getString("status"));
                venue.setCreatedDate(rs.getTimestamp("created_date"));
                
                venues.add(venue);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting owner venues: " + e.getMessage());
        }
        
        return venues;
    }
    
    // Update venue
    public boolean updateVenue(Venue venue) {
        String sql = "UPDATE venues SET name = ?, location = ?, capacity = ?, price_per_hour = ?, facilities = ?, description = ?, status = ? WHERE venue_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, venue.getName());
            pstmt.setString(2, venue.getLocation());
            pstmt.setInt(3, venue.getCapacity());
            pstmt.setDouble(4, venue.getPricePerHour());
            pstmt.setString(5, venue.getFacilities());
            pstmt.setString(6, venue.getDescription());
            pstmt.setString(7, venue.getStatus());
            pstmt.setString(8, venue.getVenueId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating venue: " + e.getMessage());
            return false;
        }
    }
    
    // Generate venue ID
    public String generateVenueId() {
        String sql = "SELECT COUNT(*) FROM venues";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return "VEN" + String.format("%03d", count);
            }
            
        } catch (SQLException e) {
            System.err.println("Error generating venue ID: " + e.getMessage());
        }
        
        return "VEN001";
    }
    
 // Get venue statistics
    public Map<String, Integer> getVenueStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT status, COUNT(*) as count FROM venues GROUP BY status";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                stats.put(rs.getString("status"), rs.getInt("count"));
            }
            
            // Add total
            sql = "SELECT COUNT(*) as total FROM venues";
            try (PreparedStatement pstmt2 = conn.prepareStatement(sql)) {
                ResultSet rs2 = pstmt2.executeQuery();
                if (rs2.next()) {
                    stats.put("totalVenues", rs2.getInt("total"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting venue statistics: " + e.getMessage());
        }
        
        return stats;
    }
}