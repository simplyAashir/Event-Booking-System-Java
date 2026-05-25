package service;

import dao.VenueDAO;
import model.Venue;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class VenueService {
    private VenueDAO venueDAO;
    
    public VenueService() {
        this.venueDAO = new VenueDAO();
    }
    
    // Add venue
    public boolean addVenue(Venue venue) {
        return venueDAO.addVenue(venue);
    }
    
    // Search venues
    public List<Venue> searchVenues(Map<String, Object> criteria) {
        String location = (String) criteria.get("location");
        Integer minCapacity = (Integer) criteria.get("minCapacity");
        Integer maxCapacity = (Integer) criteria.get("maxCapacity");
        Double maxPrice = (Double) criteria.get("maxPrice");
        String eventType = (String) criteria.get("eventType");
        
        return venueDAO.searchVenues(location, minCapacity, maxCapacity, maxPrice, eventType);
    }
    
    // Get venue by ID
    public Venue getVenueById(String venueId) {
        return venueDAO.getVenueById(venueId);
    }
    
    // Get venues by owner
    public List<Venue> getVenuesByOwner(String ownerId) {
        return venueDAO.getVenuesByOwner(ownerId);
    }
    
    // Update venue status - FIXED: Use the existing updateVenue method
    public boolean updateVenueStatus(String venueId, String status) {
        Venue venue = venueDAO.getVenueById(venueId);
        if (venue != null) {
            venue.setStatus(status);
            return venueDAO.updateVenue(venue); // Use the existing updateVenue method
        }
        return false;
    }
    
    // Check availability (demo method)
    public boolean checkAvailability(String venueId, String date) {
        // In real app, check against bookings table
        // For demo, return random availability
        return Math.random() > 0.3; // 70% chance of availability
    }
    
    // Generate venue ID
    public String generateVenueId() {
        return venueDAO.generateVenueId();
    }
    
    // Get revenue data for owner (demo - implement properly in DAO)
    public Map<String, Double> getRevenueData(String ownerId, String timePeriod) {
        // Implement proper revenue calculation in DAO
        Map<String, Double> demoData = new HashMap<>();
        demoData.put("Total Revenue", 28500.0);
        demoData.put("Completed Payments", 22400.0);
        demoData.put("Pending Payments", 6100.0);
        return demoData;
    }
}