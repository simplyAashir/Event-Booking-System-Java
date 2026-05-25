package controller;

import service.PaymentService;
import service.BookingService;
import model.Payment;
import model.Booking;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import java.util.List;

public class PaymentRecordsController {
    
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private Button refreshButton;
    @FXML private Label summaryLabel;
    @FXML private Label resultsLabel;
    @FXML private VBox paymentsContainer;
    
    private PaymentService paymentService;
    private BookingService bookingService;
    private String currentCustomerId;
    
    public PaymentRecordsController() {
        this.paymentService = new PaymentService();
        this.bookingService = new BookingService();
    }
    
    @FXML
    public void initialize() {
        setupStatusFilter();
        System.out.println("🔧 PaymentRecordsController initialized");
    }
    
    public void setCustomerId(String customerId) {
        this.currentCustomerId = customerId;
        loadPayments();
    }
    
    private void setupStatusFilter() {
        statusFilterCombo.getItems().addAll(
            "ALL", "PENDING", "COMPLETED", "FAILED", "REFUNDED"
        );
        statusFilterCombo.setValue("ALL");
        statusFilterCombo.setOnAction(e -> loadPayments());
    }
    
    @FXML
    private void handleRefresh() {
        loadPayments();
    }
    
    private void loadPayments() {
        if (currentCustomerId == null) {
            resultsLabel.setText("Please log in to view payment records");
            return;
        }
        
        List<Payment> payments = paymentService.getCustomerPayments(currentCustomerId);
        
        // Apply status filter
        String statusFilter = statusFilterCombo.getValue();
        if (!"ALL".equals(statusFilter)) {
            payments.removeIf(payment -> !payment.getStatus().equals(statusFilter));
        }
        
        displayPayments(payments);
        updateSummary(payments);
    }
    
    private void displayPayments(List<Payment> payments) {
        paymentsContainer.getChildren().clear();
        
        if (payments.isEmpty()) {
            resultsLabel.setText("No payment records found");
            return;
        }
        
        resultsLabel.setText("Found " + payments.size() + " payment record(s)");
        
        for (Payment payment : payments) {
            VBox paymentCard = createPaymentCard(payment);
            paymentsContainer.getChildren().add(paymentCard);
        }
    }
    
    private VBox createPaymentCard(Payment payment) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-padding: 15; -fx-border-radius: 5;");
        card.setPrefWidth(700);
        
        // Header with transaction ID and status
        HBox header = new HBox();
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label idLabel = new Label(payment.getPaymentMethodIcon() + " Payment #" + payment.getTransactionId());
        idLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label statusLabel = new Label(payment.getFormattedStatus());
        statusLabel.setStyle(getStatusStyle(payment.getStatus()));
        
        header.getChildren().addAll(idLabel, spacer, statusLabel);
        
        // Payment details
        Label amountLabel = new Label("💰 Amount: " + payment.getFormattedAmount());
        amountLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        
        Label dateLabel = new Label("📅 Date: " + payment.getFormattedDate());
        Label methodLabel = new Label("💳 Method: " + formatPaymentMethod(payment.getPaymentMethod()));
        
        // Reference number
        Label referenceLabel = null;
        if (payment.getReference() != null && !payment.getReference().isEmpty()) {
            referenceLabel = new Label("🔢 Reference: " + payment.getReference());
            referenceLabel.setStyle("-fx-text-fill: #7f8c8d;");
        }
        
        // Booking info
        Label bookingLabel = new Label("📋 Booking ID: " + payment.getBookingId());
        bookingLabel.setStyle("-fx-text-fill: #3498db;");
        
        // Action buttons
        HBox actions = new HBox(10);
        
        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        viewDetailsButton.setOnAction(e -> handleViewDetails(payment));
        
        // Add payment action for pending payments
        if ("PENDING".equals(payment.getStatus())) {
            Button payButton = new Button("Pay Now");
            payButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
            payButton.setOnAction(e -> handleMakePayment(payment));
            actions.getChildren().add(payButton);
        }
        
        actions.getChildren().add(viewDetailsButton);
        
        // Add all components to card
        card.getChildren().addAll(header, amountLabel, dateLabel, methodLabel, bookingLabel);
        
        if (referenceLabel != null) {
            card.getChildren().add(referenceLabel);
        }
        
        card.getChildren().add(actions);
        
        return card;
    }
    
    private String getStatusStyle(String status) {
        switch (status) {
            case "PENDING": return "-fx-text-fill: #f39c12; -fx-font-weight: bold;";
            case "COMPLETED": return "-fx-text-fill: #27ae60; -fx-font-weight: bold;";
            case "FAILED": return "-fx-text-fill: #e74c3c; -fx-font-weight: bold;";
            case "REFUNDED": return "-fx-text-fill: #3498db; -fx-font-weight: bold;";
            default: return "-fx-text-fill: #34495e;";
        }
    }
    
    private String formatPaymentMethod(String method) {
        switch (method.toUpperCase()) {
            case "CREDIT_CARD": return "Credit Card";
            case "DEBIT_CARD": return "Debit Card";
            case "BANK_TRANSFER": return "Bank Transfer";
            case "CASH": return "Cash";
            default: return method;
        }
    }
    
    private void updateSummary(List<Payment> payments) {
        double totalPaid = payments.stream()
            .filter(p -> "COMPLETED".equals(p.getStatus()))
            .mapToDouble(Payment::getAmount)
            .sum();
        
        double pendingAmount = payments.stream()
            .filter(p -> "PENDING".equals(p.getStatus()))
            .mapToDouble(Payment::getAmount)
            .sum();
        
        summaryLabel.setText(String.format("💰 Summary: Total Paid: $%.2f | Pending: $%.2f", totalPaid, pendingAmount));
    }
    
    private void handleViewDetails(Payment payment) {
        // Get booking details for context
        Booking booking = bookingService.getBookingById(payment.getBookingId());
        String bookingInfo = (booking != null) ? 
            "Event: " + booking.getEventName() + " (" + booking.getEventType() + ")" :
            "Booking information not available";
        
        showAlert("Payment Details", 
            "📋 Payment #" + payment.getTransactionId() + "\n\n" +
            "💰 Amount: " + payment.getFormattedAmount() + "\n" +
            "📅 Date: " + payment.getFormattedDate() + "\n" +
            "💳 Method: " + formatPaymentMethod(payment.getPaymentMethod()) + "\n" +
            "📝 Status: " + payment.getFormattedStatus() + "\n" +
            "🔢 Reference: " + (payment.getReference().isEmpty() ? "N/A" : payment.getReference()) + "\n" +
            "📋 Booking ID: " + payment.getBookingId() + "\n" +
            "🎉 " + bookingInfo + "\n\n" +
            "📄 Details: " + (payment.getPaymentDetails().isEmpty() ? "No additional details" : payment.getPaymentDetails())
        );
    }
    
    private void handleMakePayment(Payment payment) {
        // Simulate payment process
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Complete Payment");
        dialog.setHeaderText("Complete Payment #" + payment.getTransactionId());
        dialog.setContentText("Enter payment reference number:");
        
        dialog.showAndWait().ifPresent(reference -> {
            if (!reference.trim().isEmpty()) {
                boolean success = paymentService.completePayment(payment.getTransactionId(), reference);
                if (success) {
                    showAlert("Payment Completed", "✅ Payment completed successfully!\n\nReference: " + reference);
                    loadPayments();
                } else {
                    showAlert("Error", "❌ Failed to complete payment");
                }
            }
        });
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}