package controller;

import service.VenueService;
import model.Venue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import java.util.*;

public class VenueSearchController {
    
    @FXML private TextField locationField;
    @FXML private ComboBox<String> eventTypeCombo;
    @FXML private TextField minCapacityField;
    @FXML private TextField maxCapacityField;
    @FXML private TextField maxPriceField;
    @FXML private Button searchButton;
    @FXML private Button clearButton;
    @FXML private Label resultsLabel;
    @FXML private VBox resultsContainer;
    
    private VenueService venueService;
    private String currentCustomerId;
    private String currentCustomerName;
    
    public VenueSearchController() {
        this.venueService = new VenueService();
    }
    
    @FXML
    public void initialize() {
        // Setup event types
        eventTypeCombo.getItems().addAll(
            "Wedding", "Conference", "Seminar", "Party", 
            "Meeting", "Workshop", "Exhibition", "Concert"
        );
        
        // Clear previous results
        resultsContainer.getChildren().clear();
    }
    
    public void setCustomerData(String customerId, String customerName) {
        this.currentCustomerId = customerId;
        this.currentCustomerName = customerName;
    }
    
    @FXML
    private void handleSearch() {
        try {
            Map<String, Object> criteria = new HashMap<>();
            
            // Location
            if (!locationField.getText().trim().isEmpty()) {
                criteria.put("location", locationField.getText().trim());
            }
            
            // Event type
            if (eventTypeCombo.getValue() != null) {
                criteria.put("eventType", eventTypeCombo.getValue());
            }
            
            // Min capacity
            if (!minCapacityField.getText().trim().isEmpty()) {
                try {
                    criteria.put("minCapacity", Integer.parseInt(minCapacityField.getText().trim()));
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Min capacity must be a number");
                    return;
                }
            }
            
            // Max capacity
            if (!maxCapacityField.getText().trim().isEmpty()) {
                try {
                    criteria.put("maxCapacity", Integer.parseInt(maxCapacityField.getText().trim()));
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Max capacity must be a number");
                    return;
                }
            }
            
            // Max price
            if (!maxPriceField.getText().trim().isEmpty()) {
                try {
                    criteria.put("maxPrice", Double.parseDouble(maxPriceField.getText().trim()));
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Max price must be a number");
                    return;
                }
            }
            
            // Search venues
            List<Venue> venues = venueService.searchVenues(criteria);
            displayResults(venues);
            
        } catch (Exception e) {
            showAlert("Search Error", "Error searching venues: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleClearFilters() {
        locationField.clear();
        eventTypeCombo.setValue(null);
        minCapacityField.clear();
        maxCapacityField.clear();
        maxPriceField.clear();
        resultsContainer.getChildren().clear();
        resultsLabel.setText("Enter search criteria and click 'Search Venues'");
    }
    
    private void displayResults(List<Venue> venues) {
        resultsContainer.getChildren().clear();
        
        if (venues.isEmpty()) {
            resultsLabel.setText("❌ No venues found matching your criteria");
            return;
        }
        
        resultsLabel.setText("✅ Found " + venues.size() + " venues:");
        
        for (Venue venue : venues) {
            VBox venueCard = createVenueCard(venue);
            resultsContainer.getChildren().add(venueCard);
        }
    }
    
    private VBox createVenueCard(Venue venue) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-padding: 15; -fx-border-radius: 5;");
        card.setPrefWidth(700);
        
        // Header with name and rating
        HBox header = new HBox();
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label nameLabel = new Label(venue.getName());
        nameLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label ratingLabel = new Label("⭐ " + String.format("%.1f", venue.getRating()));
        ratingLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
        
        header.getChildren().addAll(nameLabel, spacer, ratingLabel);
        
        // Location
        Label locationLabel = new Label("📍 " + venue.getLocation());
        locationLabel.setStyle("-fx-text-fill: #7f8c8d;");
        
        // Details row
        HBox details = new HBox(20);
        details.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label capacityLabel = new Label("👥 " + venue.getCapacityInfo());
        Label priceLabel = new Label("💰 " + venue.getFormattedPrice());
        
        details.getChildren().addAll(capacityLabel, priceLabel);
        
        // Facilities
        Label facilitiesLabel = null;
        if (venue.getFacilities() != null && !venue.getFacilities().isEmpty()) {
            facilitiesLabel = new Label("🏢 Facilities: " + venue.getFacilities());
            facilitiesLabel.setStyle("-fx-text-fill: #27ae60;");
            facilitiesLabel.setWrapText(true);
        }
        
        // Description
        Label descLabel = null;
        if (venue.getDescription() != null && !venue.getDescription().isEmpty()) {
            descLabel = new Label(venue.getDescription());
            descLabel.setWrapText(true);
            descLabel.setStyle("-fx-text-fill: #34495e;");
        }
        
        // Action buttons
        HBox actions = new HBox(10);
        
        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        viewDetailsButton.setOnAction(e -> handleViewDetails(venue));
        
        Button checkAvailabilityButton = new Button("Check Availability");
        checkAvailabilityButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        checkAvailabilityButton.setOnAction(e -> handleCheckAvailability(venue));
        
        // ✅ ADDED BOOK NOW BUTTON
        Button bookButton = new Button("Book Now");
        bookButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        bookButton.setOnAction(e -> handleBookVenue(venue));
        
        actions.getChildren().addAll(viewDetailsButton, checkAvailabilityButton, bookButton);
        
        // Add all components to card
        card.getChildren().addAll(header, locationLabel, details);
        
        if (facilitiesLabel != null) {
            card.getChildren().add(facilitiesLabel);
        }
        
        if (descLabel != null) {
            card.getChildren().add(descLabel);
        }
        
        card.getChildren().add(actions);
        
        return card;
    }
    
    private void handleViewDetails(Venue venue) {
        showAlert("Venue Details", 
            "🏢 " + venue.getName() + "\n\n" +
            "📍 " + venue.getLocation() + "\n" +
            "👥 Capacity: " + venue.getCapacityInfo() + "\n" +
            "💰 Price: " + venue.getFormattedPrice() + "\n" +
            "⭐ Rating: " + String.format("%.1f", venue.getRating()) + "\n" +
            "🏢 Facilities: " + venue.getFacilities() + "\n\n" +
            venue.getDescription()
        );
    }
    
    private void handleCheckAvailability(Venue venue) {
        // For demo - in real app, show calendar
        boolean available = venueService.checkAvailability(venue.getVenueId(), "2025-01-15");
        
        if (available) {
            showAlert("Availability", 
                "✅ " + venue.getName() + " is available!\n\n" +
                "This venue appears to be available for your selected dates.\n" +
                "Please contact us for exact availability and booking."
            );
        } else {
            showAlert("Availability", 
                "❌ " + venue.getName() + " is not available.\n\n" +
                "This venue is booked for your selected dates.\n" +
                "Please try different dates or contact us for alternatives."
            );
        }
    }
    
    private void handleBookVenue(Venue venue) {
        try {
            if (currentCustomerId == null) {
                showAlert("Login Required", "Please log in to book a venue.");
                return;
            }

            System.out.println("🔧 DEBUG: Attempting to load BookingForm.fxml...");

            // ✅ FIXED: Load the correct FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BookingForm.fxml"));
            Parent root = loader.load();
            System.out.println("✅ FXML loaded successfully!");

            // Get controller
            BookingFormController controller = loader.getController();
            controller.setVenue(venue);
            controller.setCustomer(currentCustomerId, currentCustomerName);

            // Open booking window
            Stage stage = new Stage();
            stage.setTitle("Book Venue - " + venue.getName());
            stage.setScene(new Scene(root, 600, 800));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {
            System.err.println("❌ ERROR: Cannot load booking form");
            e.printStackTrace();
            showAlert("Error", "Cannot open booking form!\n\nDetails: " + e.getMessage() + 
                      "\n\nPlease ensure BookingForm.fxml exists in src/view/");
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