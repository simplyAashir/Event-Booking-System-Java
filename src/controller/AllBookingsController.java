package controller;

import service.AdminService;
import service.BookingService;
import model.Booking;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.ArrayList;

public class AllBookingsController {
    
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private ComboBox<String> userTypeFilterCombo;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private Button searchButton;
    @FXML private Button refreshButton;
    @FXML private Label resultsLabel;
    @FXML private VBox bookingsContainer;
    
    private AdminService adminService;
    private BookingService bookingService;
    private String currentAdminId;
    
    // Demo data - in real app, fetch from database
    private ObservableList<Booking> allBookings = FXCollections.observableArrayList();
    
    public AllBookingsController() {
        this.adminService = new AdminService();
        this.bookingService = new BookingService();
    }
    
    @FXML
    public void initialize() {
        setupFilters();
        loadDemoBookings(); // Load demo data
        System.out.println("🔧 AllBookingsController initialized");
    }
    
    public void setAdminId(String adminId) {
        this.currentAdminId = adminId;
        loadBookings();
    }
    
    private void setupFilters() {
        // Status filter
        statusFilterCombo.getItems().addAll("ALL", "PENDING", "APPROVED", "REJECTED", "CANCELLED", "COMPLETED");
        statusFilterCombo.setValue("ALL");
        
        // User type filter
        userTypeFilterCombo.getItems().addAll("ALL", "CUSTOMER", "VENUE_OWNER");
        userTypeFilterCombo.setValue("ALL");
    }
    
    @FXML
    private void handleSearch() {
        filterBookings();
    }
    
    @FXML
    private void handleRefresh() {
        loadBookings();
    }
    
    private void loadDemoBookings() {
        // Create demo bookings
        allBookings.clear();
        
        // Demo bookings with different statuses
        allBookings.add(createDemoBooking("BOOK001", "CUST001", "VEN001", "Conference", "Annual Tech Conference", "PENDING"));
        allBookings.add(createDemoBooking("BOOK002", "CUST002", "VEN002", "Wedding", "Smith-Johnson Wedding", "APPROVED"));
        allBookings.add(createDemoBooking("BOOK003", "CUST003", "VEN001", "Seminar", "Business Leadership Seminar", "APPROVED"));
        allBookings.add(createDemoBooking("BOOK004", "CUST001", "VEN003", "Birthday Party", "Sarah's 30th Birthday", "REJECTED"));
        allBookings.add(createDemoBooking("BOOK005", "CUST004", "VEN002", "Conference", "Marketing Summit 2024", "COMPLETED"));
        allBookings.add(createDemoBooking("BOOK006", "CUST002", "VEN001", "Workshop", "Digital Marketing Workshop", "CANCELLED"));
        allBookings.add(createDemoBooking("BOOK007", "CUST005", "VEN003", "Networking Event", "Industry Networking Night", "PENDING"));
    }
    
    private Booking createDemoBooking(String bookingId, String customerId, String venueId, String eventType, String eventName, String status) {
        Booking booking = new Booking(
            bookingId, customerId, venueId, 
            new java.util.Date(System.currentTimeMillis() + (long)(Math.random() * 30 * 24 * 60 * 60 * 1000)), // Random future date
            "09:00", "17:00", 
            (int)(Math.random() * 200) + 50, // Random guests 50-250
            (Math.random() * 2000) + 500 // Random price $500-$2500
        );
        
        booking.setEventType(eventType);
        booking.setEventName(eventName);
        booking.setStatus(status);
        booking.setContactPerson("Demo Contact");
        booking.setContactPhone("+1234567890");
        booking.setSpecialRequests("Special requests for " + eventName);
        
        return booking;
    }
    
    private void loadBookings() {
        filterBookings(); // Apply current filters
    }
    
    private void filterBookings() {
        String statusFilter = statusFilterCombo.getValue();
        String userTypeFilter = userTypeFilterCombo.getValue();
        
        List<Booking> filteredBookings = new ArrayList<>();
        
        for (Booking booking : allBookings) {
            // Apply status filter
            if (!"ALL".equals(statusFilter) && !booking.getStatus().equals(statusFilter)) {
                continue;
            }
            
            // Note: User type filter would require customer/owner info in real app
            // For demo, we'll apply a simple filter based on booking ID pattern
            if (!"ALL".equals(userTypeFilter)) {
                // In real app, you would check the actual user type from user data
                // For demo, we'll use a simple pattern
                boolean isCustomer = booking.getCustomerId().startsWith("CUST");
                boolean isOwner = booking.getCustomerId().startsWith("OWN");
                
                if ("CUSTOMER".equals(userTypeFilter) && !isCustomer) {
                    continue;
                }
                if ("VENUE_OWNER".equals(userTypeFilter) && !isOwner) {
                    continue;
                }
            }
            
            filteredBookings.add(booking);
        }
        
        displayBookings(filteredBookings);
    }
    
    private void displayBookings(List<Booking> bookings) {
        bookingsContainer.getChildren().clear();
        
        if (bookings.isEmpty()) {
            resultsLabel.setText("No bookings found matching your criteria");
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
        card.setPrefWidth(750);
        
        // Header with booking ID and status
        HBox header = new HBox();
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label idLabel = new Label("📋 Booking #" + booking.getBookingId());
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
        
        // Customer and venue info
        Label customerLabel = new Label("👤 Customer: " + booking.getCustomerId());
        Label venueLabel = new Label("🏢 Venue: " + booking.getVenueId());
        
        // Guests and price
        Label detailsLabel = new Label("👥 " + booking.getNumberOfGuests() + " guests | 💰 " + booking.getFormattedPrice());
        
        // Contact info
        Label contactLabel = new Label("📞 " + booking.getContactPerson() + " | " + booking.getContactPhone());
        contactLabel.setStyle("-fx-text-fill: #7f8c8d;");
        
        // Special requests
        Label requestsLabel = null;
        if (booking.getSpecialRequests() != null && !booking.getSpecialRequests().isEmpty()) {
            requestsLabel = new Label("💬 " + booking.getSpecialRequests());
            requestsLabel.setStyle("-fx-text-fill: #3498db; -fx-font-style: italic;");
            requestsLabel.setWrapText(true);
        }
        
        // Booking timeline
        Label timelineLabel = new Label("🕒 Created: " + booking.getCreatedAt());
        timelineLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12;");
        
        // Action buttons
        HBox actions = new HBox(10);
        
        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        viewDetailsButton.setOnAction(e -> handleViewDetails(booking));
        
        Button editButton = new Button("Edit");
        editButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        editButton.setOnAction(e -> handleEditBooking(booking));
        
        // Status management buttons
        if ("PENDING".equals(booking.getStatus())) {
            Button approveButton = new Button("✅ Approve");
            approveButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
            approveButton.setOnAction(e -> handleApproveBooking(booking));
            
            Button rejectButton = new Button("❌ Reject");
            rejectButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            rejectButton.setOnAction(e -> handleRejectBooking(booking));
            
            actions.getChildren().addAll(approveButton, rejectButton);
        }
        
        // Cancel button for approved/pending bookings
        if ("APPROVED".equals(booking.getStatus()) || "PENDING".equals(booking.getStatus())) {
            Button cancelButton = new Button("🚫 Cancel");
            cancelButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            cancelButton.setOnAction(e -> handleCancelBooking(booking));
            actions.getChildren().add(cancelButton);
        }
        
        actions.getChildren().addAll(viewDetailsButton, editButton);
        
        // Add all components to card
        card.getChildren().addAll(header, eventLabel, dateLabel, customerLabel, venueLabel, detailsLabel, contactLabel, timelineLabel);
        
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
    
    private void handleViewDetails(Booking booking) {
        showAlert("Booking Details", 
            "📋 Booking #" + booking.getBookingId() + "\n\n" +
            "🎉 Event: " + booking.getEventName() + "\n" +
            "📅 Date: " + booking.getEventDate() + "\n" +
            "⏰ Time: " + booking.getStartTime() + " - " + booking.getEndTime() + "\n" +
            "👥 Guests: " + booking.getNumberOfGuests() + "\n" +
            "💰 Total: " + booking.getFormattedPrice() + "\n" +
            "📞 Contact: " + booking.getContactPerson() + " | " + booking.getContactPhone() + "\n" +
            "📝 Status: " + booking.getFormattedStatus() + "\n" +
            "👤 Customer ID: " + booking.getCustomerId() + "\n" +
            "🏢 Venue ID: " + booking.getVenueId() + "\n" +
            "🕒 Created: " + booking.getCreatedAt() + "\n\n" +
            "💬 Special Requests:\n" + (booking.getSpecialRequests().isEmpty() ? "None" : booking.getSpecialRequests())
        );
    }
    
    private void handleEditBooking(Booking booking) {
        showAlert("Edit Booking", 
            "✏️ Edit functionality for Booking #" + booking.getBookingId() + "\n\n" +
            "In a full implementation, this would open an edit form.\n" +
            "Event: " + booking.getEventName()
        );
    }
    
    private void handleApproveBooking(Booking booking) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Approve Booking");
        alert.setHeaderText("Confirm Approval");
        alert.setContentText("Approve booking #" + booking.getBookingId() + "?\n\nEvent: " + booking.getEventName());
        
        if (alert.showAndWait().get().getText().equals("OK")) {
            // In real app, update in database
            booking.setStatus("APPROVED");
            
            showAlert("Success", "✅ Booking approved successfully!\n\nBooking #" + booking.getBookingId());
            loadBookings(); // Refresh the list
        }
    }
    
    private void handleRejectBooking(Booking booking) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject Booking");
        dialog.setHeaderText("Reject Booking #" + booking.getBookingId());
        dialog.setContentText("Please provide a reason for rejection:");
        
        dialog.showAndWait().ifPresent(reason -> {
            if (!reason.trim().isEmpty()) {
                // In real app, update in database
                booking.setStatus("REJECTED");
                
                showAlert("Booking Rejected", "❌ Booking #" + booking.getBookingId() + " has been rejected.\n\nReason: " + reason);
                loadBookings(); // Refresh the list
            }
        });
    }
    
    private void handleCancelBooking(Booking booking) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Booking");
        alert.setHeaderText("Confirm Cancellation");
        alert.setContentText("Cancel booking #" + booking.getBookingId() + "?\n\nEvent: " + booking.getEventName());
        
        if (alert.showAndWait().get().getText().equals("OK")) {
            // In real app, update in database
            booking.setStatus("CANCELLED");
            
            showAlert("Success", "🚫 Booking cancelled successfully!\n\nBooking #" + booking.getBookingId());
            loadBookings(); // Refresh the list
        }
    }
    
    @FXML
    private void handleExportBookings() {
        showAlert("Export Bookings", 
            "📤 Export Bookings functionality\n\n" +
            "In a full implementation, this would export all filtered bookings\n" +
            "to CSV, Excel, or PDF format with comprehensive booking details."
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