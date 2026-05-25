package model;

import java.util.Date;

public class Booking {
    private String bookingId;
    private String customerId;
    private String venueId;
    private Date bookingDate;
    private Date eventDate;
    private String startTime;
    private String endTime;
    private int numberOfGuests;
    private double totalPrice;
    private String status;
    private String specialRequests;
    private String eventType;
    private String eventName;
    private String contactPerson;
    private String contactPhone;
    private Date createdAt;

    public Booking(String bookingId, String customerId, String venueId, Date eventDate, 
                   String startTime, String endTime, int numberOfGuests, double totalPrice) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.venueId = venueId;
        this.bookingDate = new Date();
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.numberOfGuests = numberOfGuests;
        this.totalPrice = totalPrice;
        this.status = "PENDING";
        this.specialRequests = "";
        this.eventType = "";
        this.eventName = "";
        this.contactPerson = "";
        this.contactPhone = "";
        this.createdAt = new Date();
    }

    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public String getCustomerId() { return customerId; }
    public String getVenueId() { return venueId; }
    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }
    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public int getNumberOfGuests() { return numberOfGuests; }
    public void setNumberOfGuests(int numberOfGuests) { this.numberOfGuests = numberOfGuests; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    // Business methods
    public int calculateDuration() {
        // Simple duration calculation (in real app, parse time strings)
        return 4; // Default 4 hours for demo
    }

    public String getFormattedStatus() {
        switch (status) {
            case "PENDING": return "⏳ Pending";
            case "APPROVED": return "✅ Approved";
            case "REJECTED": return "❌ Rejected";
            case "CANCELLED": return "🚫 Cancelled";
            case "COMPLETED": return "🎉 Completed";
            default: return status;
        }
    }

    public String getFormattedPrice() {
        return String.format("$%.2f", totalPrice);
    }
}