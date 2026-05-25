package controller;

import service.BookingService;
import model.Booking;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import java.util.List;

public class BookingRequestsController {
    
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private Button refreshButton;
    @FXML private Label resultsLabel;
    @FXML private VBox requestsContainer;
    
    private BookingService bookingService;
    private String currentOwnerId;
    
    public BookingRequestsController() {
        this.bookingService = new BookingService();
    }
    
    @FXML
    public void initialize() {
        setupStatusFilter();
        System.out.println("🔧 BookingRequestsController initialized");
    }
    
    public void setOwnerId(String ownerId) {
        this.currentOwnerId = ownerId;
        loadBookingRequests();
    }
    
    private void setupStatusFilter() {
        statusFilterCombo.getItems().addAll(
            "ALL", "PENDING", "APPROVED", "REJECTED", "CANCELLED", "COMPLETED"
        );
        statusFilterCombo.setValue("PENDING"); // Default to pending requests
        statusFilterCombo.setOnAction(e -> loadBookingRequests());
    }
    
    @FXML
    private void handleRefresh() {
        loadBookingRequests();
    }
    
    private void loadBookingRequests() {
        if (currentOwnerId == null) {
            resultsLabel.setText("Please log in to view booking requests");
            return;
        }
        
        List<Booking> requests = bookingService.getBookingsByVenueOwner(currentOwnerId);
        
        // Apply status filter
        String statusFilter = statusFilterCombo.getValue();
        if (!"ALL".equals(statusFilter)) {
            requests.removeIf(booking -> !booking.getStatus().equals(statusFilter));
        }
        
        displayRequests(requests);
    }
    
    private void displayRequests(List<Booking> requests) {
        requestsContainer.getChildren().clear();
        
        if (requests.isEmpty()) {
            resultsLabel.setText("No booking requests found");
            return;
        }
        
        resultsLabel.setText("Found " + requests.size() + " booking request(s)");
        
        for (Booking booking : requests) {
            VBox requestCard = createRequestCard(booking);
            requestsContainer.getChildren().add(requestCard);
        }
    }
    
    private VBox createRequestCard(Booking booking) {
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
        
        // Contact info
        Label contactLabel = new Label("📞 " + booking.getContactPerson() + " | " + booking.getContactPhone());
        contactLabel.setStyle("-fx-text-fill: #7f8c8d;");
        
        // Special requests
        Label requestsLabel = null;
        if (booking.getSpecialRequests() != null && !booking.getSpecialRequests().isEmpty()) {
            requestsLabel = new Label("💬 Special Requests: " + booking.getSpecialRequests());
            requestsLabel.setWrapText(true);
            requestsLabel.setStyle("-fx-text-fill: #3498db; -fx-font-style: italic;");
        }
        
        // Action buttons based on status
        HBox actions = new HBox(10);
        
        if ("PENDING".equals(booking.getStatus())) {
            Button approveButton = new Button("✅ Approve");
            approveButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
            approveButton.setOnAction(e -> handleApproveBooking(booking));
            
            Button rejectButton = new Button("❌ Reject");
            rejectButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            rejectButton.setOnAction(e -> handleRejectBooking(booking));
            
            actions.getChildren().addAll(approveButton, rejectButton);
        }
        
        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        viewDetailsButton.setOnAction(e -> handleViewDetails(booking));
        actions.getChildren().add(viewDetailsButton);
        
        // Add all components to card
        card.getChildren().addAll(header, eventLabel, dateLabel, detailsLabel, contactLabel);
        
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
    
    private void handleApproveBooking(Booking booking) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Approve Booking");
        alert.setHeaderText("Confirm Approval");
        alert.setContentText("Approve booking #" + booking.getBookingId() + " for " + booking.getEventName() + "?");
        
        if (alert.showAndWait().get().getText().equals("OK")) {
            boolean success = bookingService.approveBooking(booking.getBookingId());
            if (success) {
                showAlert("Success", "✅ Booking approved successfully!\n\nBooking #" + booking.getBookingId() + " has been approved.");
                loadBookingRequests();
            } else {
                showAlert("Error", "❌ Failed to approve booking");
            }
        }
    }
    
    private void handleRejectBooking(Booking booking) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject Booking");
        dialog.setHeaderText("Reject Booking #" + booking.getBookingId());
        dialog.setContentText("Please provide a reason for rejection:");
        
        dialog.showAndWait().ifPresent(reason -> {
            if (!reason.trim().isEmpty()) {
                boolean success = bookingService.rejectBooking(booking.getBookingId(), reason);
                if (success) {
                    showAlert("Booking Rejected", "❌ Booking #" + booking.getBookingId() + " has been rejected.\n\nReason: " + reason);
                    loadBookingRequests();
                } else {
                    showAlert("Error", "Failed to reject booking");
                }
            } else {
                showAlert("Error", "Please provide a reason for rejection");
            }
        });
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