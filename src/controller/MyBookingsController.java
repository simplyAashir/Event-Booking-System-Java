package controller;

import service.BookingService;
import model.Booking;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;
import java.util.List;

public class MyBookingsController {
    
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private Button refreshButton;
    @FXML private Label resultsLabel;
    @FXML private VBox bookingsContainer;
    
    private BookingService bookingService;
    private String currentCustomerId;
    
    public MyBookingsController() {
        this.bookingService = new BookingService();
    }
    
    @FXML
    public void initialize() {
        setupStatusFilter();
        loadBookings();
    }
    
    public void setCustomerId(String customerId) {
        this.currentCustomerId = customerId;
        loadBookings();
    }
    
    private void setupStatusFilter() {
        statusFilterCombo.getItems().addAll(
            "ALL", "PENDING", "APPROVED", "REJECTED", "CANCELLED", "COMPLETED"
        );
        statusFilterCombo.setValue("ALL");
        
        statusFilterCombo.setOnAction(e -> loadBookings());
    }
    
    @FXML
    private void handleRefresh() {
        loadBookings();
    }
    
    private void loadBookings() {
        if (currentCustomerId == null) {
            resultsLabel.setText("Please log in to view bookings");
            return;
        }
        
        List<Booking> bookings = bookingService.getCustomerBookings(currentCustomerId);
        
        // Apply status filter
        String statusFilter = statusFilterCombo.getValue();
        if (!"ALL".equals(statusFilter)) {
            bookings.removeIf(booking -> !booking.getStatus().equals(statusFilter));
        }
        
        displayBookings(bookings);
    }
    
    private void displayBookings(List<Booking> bookings) {
        bookingsContainer.getChildren().clear();
        
        if (bookings.isEmpty()) {
            resultsLabel.setText("No bookings found" + 
                (!"ALL".equals(statusFilterCombo.getValue()) ? " with status: " + statusFilterCombo.getValue() : ""));
            return;
        }
        
        resultsLabel.setText("Found " + bookings.size() + " booking(s)");
        
        for (Booking booking : bookings) {
            VBox bookingCard = createBookingCard(booking);
            bookingsContainer.getChildren().add(bookingCard);
        }
    }
    
    private VBox createBookingCard(Booking booking) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-padding: 15; -fx-border-radius: 5;");
        card.setPrefWidth(700);
        
        // Header with booking ID and status
        HBox header = new HBox();
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label idLabel = new Label("Booking #" + booking.getBookingId());
        idLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label statusLabel = new Label(booking.getFormattedStatus());
        statusLabel.setStyle(getStatusStyle(booking.getStatus()));
        
        header.getChildren().addAll(idLabel, spacer, statusLabel);
        
        // Event details
        Label eventLabel = new Label("🎉 " + booking.getEventName() + " (" + booking.getEventType() + ")");
        eventLabel.setStyle("-fx-font-weight: bold;");
        
        // Date and time
        Label dateLabel = new Label("📅 " + booking.getEventDate() + " | ⏰ " + booking.getStartTime() + " - " + booking.getEndTime());
        
        // Guests and price
        Label detailsLabel = new Label("👥 " + booking.getNumberOfGuests() + " guests | 💰 " + booking.getFormattedPrice());
        
        // ✅ FIXED: Initialize variables outside if blocks
        Label contactLabel = null;
        Label requestsLabel = null;
        
        // Contact info
        if (booking.getContactPerson() != null && !booking.getContactPerson().isEmpty()) {
            contactLabel = new Label("📞 " + booking.getContactPerson() + " | " + booking.getContactPhone());
            contactLabel.setStyle("-fx-text-fill: #7f8c8d;");
        }
        
        // Special requests
        if (booking.getSpecialRequests() != null && !booking.getSpecialRequests().isEmpty()) {
            requestsLabel = new Label("💬 " + booking.getSpecialRequests());
            requestsLabel.setStyle("-fx-text-fill: #3498db; -fx-font-style: italic;");
            requestsLabel.setWrapText(true);
        }
        
        // Action buttons based on status
        HBox actions = new HBox(10);
        if ("PENDING".equals(booking.getStatus())) {
            Button cancelButton = new Button("Cancel Booking");
            cancelButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            cancelButton.setOnAction(e -> handleCancelBooking(booking));
            actions.getChildren().add(cancelButton);
        }
        
        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        viewDetailsButton.setOnAction(e -> handleViewDetails(booking));
        actions.getChildren().add(viewDetailsButton);
        
        // ✅ FIXED: Add all components to card with proper null checks
        card.getChildren().addAll(header, eventLabel, dateLabel, detailsLabel);
        
        // Add contact info if exists
        if (contactLabel != null) {
            card.getChildren().add(contactLabel);
        }
        
        // Add special requests if exists
        if (requestsLabel != null) {
            card.getChildren().add(requestsLabel);
        }
        
        card.getChildren().add(actions);
        
        return card;
    }
    
    private String getStatusStyle(String status) {
        switch (status) {
            case "PENDING": return "-fx-text-fill: #f39c12; -fx-font-weight: bold;";
            case "APPROVED": return "-fx-text-fill: #27ae60; -fx-font-weight: bold;";
            case "REJECTED": return "-fx-text-fill: #e74c3c; -fx-font-weight: bold;";
            case "CANCELLED": return "-fx-text-fill: #95a5a6; -fx-font-weight: bold;";
            case "COMPLETED": return "-fx-text-fill: #3498db; -fx-font-weight: bold;";
            default: return "-fx-text-fill: #34495e;";
        }
    }
    
    private void handleCancelBooking(Booking booking) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Booking");
        alert.setHeaderText("Confirm Cancellation");
        alert.setContentText("Are you sure you want to cancel booking #" + booking.getBookingId() + "?");
        
        if (alert.showAndWait().get().getText().equals("OK")) {
            boolean success = bookingService.cancelBooking(booking.getBookingId());
            if (success) {
                showAlert("Success", "Booking cancelled successfully");
                loadBookings();
            } else {
                showAlert("Error", "Failed to cancel booking");
            }
        }
    }
    
    private void handleViewDetails(Booking booking) {
        showAlert("Booking Details", 
            "📋 Booking #" + booking.getBookingId() + "\n\n" +
            "🎉 Event: " + booking.getEventName() + "\n" +
            "📅 Date: " + booking.getEventDate() + "\n" +
            "⏰ Time: " + booking.getStartTime() + " - " + booking.getEndTime() + "\n" +
            "👥 Guests: " + booking.getNumberOfGuests() + "\n" +
            "💰 Total: " + booking.getFormattedPrice() + "\n" +
            "📞 Contact: " + booking.getContactPerson() + " | " + booking.getContactPhone() + "\n" +
            "📝 Status: " + booking.getFormattedStatus() + "\n\n" +
            "💬 Special Requests:\n" + (booking.getSpecialRequests().isEmpty() ? "None" : booking.getSpecialRequests())
        );
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}