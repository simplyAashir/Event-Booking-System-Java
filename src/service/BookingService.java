package service;

import dao.BookingDAO;
import dao.VenueDAO;
import model.Booking;
import model.Venue;
import java.util.Date;
import java.util.List;

public class BookingService {
    private BookingDAO bookingDAO;
    private VenueDAO venueDAO;
    
    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.venueDAO = new VenueDAO();
    }
    
    /**
     * Create new booking with automatic price calculation
     */
    public Booking createBooking(String customerId, String venueId, Date eventDate, 
                                String startTime, String endTime, int numberOfGuests,
                                String eventType, String eventName, String specialRequests,
                                String contactPerson, String contactPhone) {
        
        // Validate inputs
        if (customerId == null || customerId.trim().isEmpty()) {
            System.err.println("❌ Customer ID is required");
            return null;
        }
        
        if (venueId == null || venueId.trim().isEmpty()) {
            System.err.println("❌ Venue ID is required");
            return null;
        }
        
        if (eventDate == null || eventDate.before(new Date())) {
            System.err.println("❌ Valid event date is required");
            return null;
        }
        
        if (numberOfGuests <= 0) {
            System.err.println("❌ Number of guests must be positive");
            return null;
        }
        
        if (startTime == null || endTime == null) {
            System.err.println("❌ Start time and end time are required");
            return null;
        }
        
        // Get venue to calculate price
        Venue venue = venueDAO.getVenueById(venueId);
        if (venue == null) {
            System.err.println("❌ Venue not found: " + venueId);
            return null;
        }
        
        // Calculate duration from start and end time
        int duration = calculateDuration(startTime, endTime);
        if (duration <= 0) {
            System.err.println("❌ Invalid time range: " + startTime + " to " + endTime);
            return null;
        }
        
        // Calculate total price
        double totalPrice = venue.getPricePerHour() * duration;
        
        // Generate booking ID
        String bookingId = bookingDAO.generateBookingId();
        
        // Create booking object
        Booking booking = new Booking(bookingId, customerId, venueId, eventDate, 
                                     startTime, endTime, numberOfGuests, totalPrice);
        booking.setEventType(eventType);
        booking.setEventName(eventName);
        booking.setSpecialRequests(specialRequests);
        booking.setContactPerson(contactPerson);
        booking.setContactPhone(contactPhone);
        booking.setStatus("PENDING"); // Initial status
        
        // Save to database
        boolean success = bookingDAO.createBooking(booking);
        
        if (success) {
            System.out.println("✅ Booking created successfully: " + bookingId);
            System.out.println("   Event: " + eventName + " (" + eventType + ")");
            System.out.println("   Duration: " + duration + " hours");
            System.out.println("   Total Price: $" + String.format("%.2f", totalPrice));
            return booking;
        } else {
            System.err.println("❌ Failed to create booking in database");
            return null;
        }
    }
    
    /**
     * Calculate duration in hours between start and end time
     * @param startTime Format: "HH:MM" (e.g., "09:00")
     * @param endTime Format: "HH:MM" (e.g., "17:00")
     * @return Duration in hours
     */
    private int calculateDuration(String startTime, String endTime) {
        try {
            String[] startParts = startTime.split(":");
            String[] endParts = endTime.split(":");
            
            int startHour = Integer.parseInt(startParts[0]);
            int endHour = Integer.parseInt(endParts[0]);
            
            int duration = endHour - startHour;
            
            // Handle minutes if needed (optional enhancement)
            if (startParts.length > 1 && endParts.length > 1) {
                int startMinutes = Integer.parseInt(startParts[1]);
                int endMinutes = Integer.parseInt(endParts[1]);
                
                if (endMinutes < startMinutes) {
                    duration--; // Reduce by 1 hour if end minutes < start minutes
                }
            }
            
            return Math.max(0, duration);
            
        } catch (Exception e) {
            System.err.println("❌ Error calculating duration: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Get bookings for a specific customer
     */
    public List<Booking> getCustomerBookings(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            System.err.println("❌ Customer ID is required");
            return List.of(); // Return empty list
        }
        return bookingDAO.getBookingsByCustomer(customerId);
    }
    
    /**
     * Get bookings for venue owner
     */
    public List<Booking> getBookingsByVenueOwner(String ownerId) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            System.err.println("❌ Owner ID is required");
            return List.of();
        }
        return bookingDAO.getBookingsByVenueOwner(ownerId);
    }
    
    /**
     * Get all bookings (admin only)
     */
    public List<Booking> getAllBookings() {
        return bookingDAO.getAllBookings();
    }
    
    /**
     * Get booking by ID
     */
    public Booking getBookingById(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            System.err.println("❌ Booking ID is required");
            return null;
        }
        return bookingDAO.getBookingById(bookingId);
    }
    
    /**
     * Update booking status
     */
    public boolean updateBookingStatus(String bookingId, String status) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            System.err.println("❌ Booking ID is required");
            return false;
        }
        
        if (!isValidStatus(status)) {
            System.err.println("❌ Invalid booking status: " + status);
            return false;
        }
        
        boolean success = bookingDAO.updateBookingStatus(bookingId, status);
        if (success) {
            System.out.println("✅ Booking status updated: " + bookingId + " -> " + status);
        } else {
            System.err.println("❌ Failed to update booking status");
        }
        
        return success;
    }
    
    /**
     * Approve booking (venue owner action)
     */
    public boolean approveBooking(String bookingId) {
        System.out.println("📋 Approving booking: " + bookingId);
        return updateBookingStatus(bookingId, "APPROVED");
    }
    
    /**
     * Reject booking (venue owner action)
     */
    public boolean rejectBooking(String bookingId, String reason) {
        System.out.println("📋 Rejecting booking: " + bookingId);
        if (reason != null && !reason.trim().isEmpty()) {
            System.out.println("   Reason: " + reason);
            // TODO: Store rejection reason in database if needed
        }
        return updateBookingStatus(bookingId, "REJECTED");
    }
    
    /**
     * Cancel booking (customer action)
     */
    public boolean cancelBooking(String bookingId) {
        System.out.println("📋 Cancelling booking: " + bookingId);
        return updateBookingStatus(bookingId, "CANCELLED");
    }
    
    /**
     * Complete booking (mark as completed)
     */
    public boolean completeBooking(String bookingId) {
        System.out.println("📋 Completing booking: " + bookingId);
        return updateBookingStatus(bookingId, "COMPLETED");
    }
    
    /**
     * Validate booking status
     */
    private boolean isValidStatus(String status) {
        if (status == null) return false;
        
        return status.equals("PENDING") || 
               status.equals("APPROVED") || 
               status.equals("REJECTED") || 
               status.equals("CANCELLED") || 
               status.equals("COMPLETED");
    }
    
    /**
     * Get booking statistics for a customer
     */
    public BookingStats getCustomerBookingStats(String customerId) {
        List<Booking> bookings = getCustomerBookings(customerId);
        
        int totalBookings = bookings.size();
        int pendingBookings = (int) bookings.stream()
            .filter(b -> "PENDING".equals(b.getStatus())).count();
        int approvedBookings = (int) bookings.stream()
            .filter(b -> "APPROVED".equals(b.getStatus())).count();
        int completedBookings = (int) bookings.stream()
            .filter(b -> "COMPLETED".equals(b.getStatus())).count();
        
        double totalSpent = bookings.stream()
            .filter(b -> "COMPLETED".equals(b.getStatus()))
            .mapToDouble(Booking::getTotalPrice)
            .sum();
        
        return new BookingStats(totalBookings, pendingBookings, approvedBookings, 
                               completedBookings, totalSpent);
    }
    
    /**
     * Inner class for booking statistics
     */
    public static class BookingStats {
        private int totalBookings;
        private int pendingBookings;
        private int approvedBookings;
        private int completedBookings;
        private double totalSpent;
        
        public BookingStats(int total, int pending, int approved, int completed, double spent) {
            this.totalBookings = total;
            this.pendingBookings = pending;
            this.approvedBookings = approved;
            this.completedBookings = completed;
            this.totalSpent = spent;
        }
        
        // Getters
        public int getTotalBookings() { return totalBookings; }
        public int getPendingBookings() { return pendingBookings; }
        public int getApprovedBookings() { return approvedBookings; }
        public int getCompletedBookings() { return completedBookings; }
        public double getTotalSpent() { return totalSpent; }
    }
}