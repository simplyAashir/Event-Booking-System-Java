package controller;

import service.VenueService;
import model.Venue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;
import java.util.List;

public class ManageVenuesController {
    
    @FXML private Button addVenueButton;
    @FXML private Button refreshButton;
    @FXML private Label resultsLabel;
    @FXML private VBox venuesContainer;
    
    private VenueService venueService;
    private String currentOwnerId;
    
    public ManageVenuesController() {
        this.venueService = new VenueService();
    }
    
    @FXML
    public void initialize() {
        System.out.println("🔧 ManageVenuesController initialized");
    }
    
    public void setOwnerId(String ownerId) {
        this.currentOwnerId = ownerId;
        loadVenues();
    }
    
    @FXML
    private void handleRefresh() {
        loadVenues();
    }
    
    @FXML
    private void handleAddVenue() {
        try {
            // Create a simple dialog to add venue (in real app, use proper form)
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add New Venue");
            dialog.setHeaderText("Add New Venue");
            dialog.setContentText("Venue Name:");
            
            dialog.showAndWait().ifPresent(venueName -> {
                if (!venueName.trim().isEmpty()) {
                    // Create demo venue (in real app, use proper form with all fields)
                    Venue newVenue = new Venue(
                        venueService.generateVenueId(),
                        venueName.trim(),
                        "Location to be set",
                        100, // default capacity
                        50.0, // default price
                        "Basic facilities",
                        "Venue description",
                        currentOwnerId
                    );
                    
                    boolean success = venueService.addVenue(newVenue);
                    if (success) {
                        showAlert("Success", "✅ Venue added successfully!\n\nVenue: " + venueName);
                        loadVenues();
                    } else {
                        showAlert("Error", "❌ Failed to add venue");
                    }
                }
            });
            
        } catch (Exception e) {
            showAlert("Error", "Cannot add venue: " + e.getMessage());
        }
    }
    
    private void loadVenues() {
        if (currentOwnerId == null) {
            resultsLabel.setText("Please log in to manage venues");
            return;
        }
        
        List<Venue> venues = venueService.getVenuesByOwner(currentOwnerId);
        displayVenues(venues);
    }
    
    private void displayVenues(List<Venue> venues) {
        venuesContainer.getChildren().clear();
        
        if (venues.isEmpty()) {
            resultsLabel.setText("No venues found. Click 'Add New Venue' to get started!");
            return;
        }
        
        resultsLabel.setText("Managing " + venues.size() + " venue(s)");
        
        for (Venue venue : venues) {
            VBox venueCard = createVenueCard(venue);
            venuesContainer.getChildren().add(venueCard);
        }
    }
    
    private VBox createVenueCard(Venue venue) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-padding: 15; -fx-border-radius: 5;");
        card.setPrefWidth(700);
        
        // Header with name and status
        HBox header = new HBox();
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label nameLabel = new Label("🏢 " + venue.getName());
        nameLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label statusLabel = new Label(venue.getStatus());
        statusLabel.setStyle(getStatusStyle(venue.getStatus()));
        
        header.getChildren().addAll(nameLabel, spacer, statusLabel);
        
        // Venue details
        Label locationLabel = new Label("📍 " + venue.getLocation());
        Label capacityLabel = new Label("👥 Capacity: " + venue.getCapacityInfo());
        Label priceLabel = new Label("💰 " + venue.getFormattedPrice());
        
        // Facilities
        Label facilitiesLabel = null;
        if (venue.getFacilities() != null && !venue.getFacilities().isEmpty()) {
            facilitiesLabel = new Label("🏢 Facilities: " + venue.getFacilities());
            facilitiesLabel.setStyle("-fx-text-fill: #27ae60;");
        }
        
        // Description
        Label descLabel = null;
        if (venue.getDescription() != null && !venue.getDescription().isEmpty()) {
            descLabel = new Label(venue.getDescription());
            descLabel.setWrapText(true);
            descLabel.setStyle("-fx-text-fill: #7f8c8d;");
        }
        
        // Action buttons
        HBox actions = new HBox(10);
        
        Button editButton = new Button("✏️ Edit");
        editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        editButton.setOnAction(e -> handleEditVenue(venue));
        
        Button viewBookingsButton = new Button("📋 View Bookings");
        viewBookingsButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        viewBookingsButton.setOnAction(e -> handleViewBookings(venue));
        
        Button toggleStatusButton = new Button(venue.getStatus().equals("ACTIVE") ? "⏸️ Deactivate" : "▶️ Activate");
        toggleStatusButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        toggleStatusButton.setOnAction(e -> handleToggleStatus(venue));
        
        actions.getChildren().addAll(editButton, viewBookingsButton, toggleStatusButton);
        
        // Add all components to card
        card.getChildren().addAll(header, locationLabel, capacityLabel, priceLabel);
        
        if (facilitiesLabel != null) {
            card.getChildren().add(facilitiesLabel);
        }
        
        if (descLabel != null) {
            card.getChildren().add(descLabel);
        }
        
        card.getChildren().add(actions);
        
        return card;
    }
    
    private String getStatusStyle(String status) {
        switch (status) {
            case "ACTIVE": return "-fx-text-fill: #27ae60; -fx-font-weight: bold;";
            case "INACTIVE": return "-fx-text-fill: #e74c3c; -fx-font-weight: bold;";
            case "UNDER_MAINTENANCE": return "-fx-text-fill: #f39c12; -fx-font-weight: bold;";
            default: return "-fx-text-fill: #34495e;";
        }
    }
    
    private void handleEditVenue(Venue venue) {
        showAlert("Edit Venue", 
            "✏️ Edit functionality for: " + venue.getName() + "\n\n" +
            "In a full implementation, this would open an edit form.\n" +
            "Venue ID: " + venue.getVenueId()
        );
    }
    
    private void handleViewBookings(Venue venue) {
        showAlert("Venue Bookings", 
            "📋 Bookings for: " + venue.getName() + "\n\n" +
            "This would show all bookings for this venue.\n" +
            "Venue ID: " + venue.getVenueId()
        );
    }
    
    private void handleToggleStatus(Venue venue) {
        String newStatus = venue.getStatus().equals("ACTIVE") ? "INACTIVE" : "ACTIVE";
        boolean success = venueService.updateVenueStatus(venue.getVenueId(), newStatus);
        
        if (success) {
            showAlert("Status Updated", "✅ Venue status changed to: " + newStatus);
            loadVenues();
        } else {
            showAlert("Error", "❌ Failed to update venue status");
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}