package model;

import java.util.Date;

public class Venue {
    private String venueId;
    private String name;
    private String location;
    private int capacity;
    private double pricePerHour;
    private String facilities;
    private String description;
    private double rating;
    private String status;
    private Date createdDate;
    private String ownerId;

    public Venue(String venueId, String name, String location, int capacity, double pricePerHour, 
                 String facilities, String description, String ownerId) {
        this.venueId = venueId;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.pricePerHour = pricePerHour;
        this.facilities = facilities;
        this.description = description;
        this.rating = 0.0;
        this.status = "ACTIVE";
        this.createdDate = new Date();
        this.ownerId = ownerId;
    }

    // Getters and Setters
    public String getVenueId() { return venueId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public double getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(double pricePerHour) { this.pricePerHour = pricePerHour; }
    public String getFacilities() { return facilities; }
    public void setFacilities(String facilities) { this.facilities = facilities; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreatedDate() { return createdDate; }
    
    // ✅ ADD THIS MISSING METHOD
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    // Business methods
    public double calculatePrice(int hours) {
        return hours * pricePerHour;
    }

    public String getFormattedPrice() {
        return String.format("$%.2f/hour", pricePerHour);
    }

    public String getCapacityInfo() {
        return capacity + " people";
    }

    @Override
    public String toString() {
        return "Venue{" +
                "name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", capacity=" + capacity +
                ", pricePerHour=" + pricePerHour +
                '}';
    }
}