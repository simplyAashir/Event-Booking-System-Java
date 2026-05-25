package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Booking;
import model.Venue;
import dao.BookingDAO;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class BookingFormController {

    @FXML private VBox venueInfoBox;
    @FXML private Label venueNameLabel;
    @FXML private Label venueDetailsLabel;
    @FXML private Label venuePriceLabel;
    @FXML private TextField eventNameField;
    @FXML private ComboBox<String> eventTypeCombo;
    @FXML private DatePicker eventDatePicker;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    @FXML private TextField numberOfGuestsField;
    @FXML private TextField contactPersonField;
    @FXML private TextField contactPhoneField;
    @FXML private TextArea specialRequestsArea;
    @FXML private Label totalPriceLabel;
    @FXML private Button cancelButton;
    @FXML private Button submitButton;

    private Venue selectedVenue;
    private String customerId;
    private String customerName;
    private BookingDAO bookingDAO;

    public BookingFormController() {
        this.bookingDAO = new BookingDAO();
    }

    @FXML
    public void initialize() {
        System.out.println("✅ BookingFormController initialized!");
        
        eventTypeCombo.getItems().addAll(
            "Wedding", "Conference", "Seminar", "Party",
            "Meeting", "Workshop", "Exhibition", "Concert", "Other"
        );

        startTimeField.textProperty().addListener((obs, oldVal, newVal) -> calculateTotalPrice());
        endTimeField.textProperty().addListener((obs, oldVal, newVal) -> calculateTotalPrice());
    }

    public void setVenue(Venue venue) {
        this.selectedVenue = venue;
        displayVenueInfo();
    }

    public void setCustomer(String customerId, String customerName) {
        this.customerId = customerId;
        this.customerName = customerName;
        if (contactPersonField != null) {
            contactPersonField.setText(customerName);
        }
    }

    private void displayVenueInfo() {
        if (selectedVenue != null) {
            venueNameLabel.setText("🏢 " + selectedVenue.getName());
            venueDetailsLabel.setText("📍 " + selectedVenue.getLocation() + 
                                    " | 👥 Capacity: " + selectedVenue.getCapacity());
            venuePriceLabel.setText("💰 Price: $" + String.format("%.2f", selectedVenue.getPricePerHour()) + "/hour");
        }
    }

    private void calculateTotalPrice() {
        if (selectedVenue == null) return;

        try {
            String startTime = startTimeField.getText().trim();
            String endTime = endTimeField.getText().trim();

            if (!startTime.isEmpty() && !endTime.isEmpty() && 
                startTime.matches("\\d{2}:\\d{2}") && endTime.matches("\\d{2}:\\d{2}")) {
                
                double hours = calculateHours(startTime, endTime);
                
                if (hours > 0) {
                    double total = hours * selectedVenue.getPricePerHour();
                    totalPriceLabel.setText(String.format("$%.2f", total));
                } else {
                    totalPriceLabel.setText("$0.00");
                }
            }
        } catch (Exception e) {
            totalPriceLabel.setText("$0.00");
        }
    }

    @FXML
    public void handleSubmit() {
        System.out.println("📝 Submit button clicked!");
        
        if (!validateForm()) {
            return;
        }

        try {
            String bookingId = bookingDAO.generateBookingId();
            LocalDate eventLocalDate = eventDatePicker.getValue();
            Date eventDate = Date.from(eventLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            String startTime = startTimeField.getText().trim();
            String endTime = endTimeField.getText().trim();
            int numberOfGuests = Integer.parseInt(numberOfGuestsField.getText().trim());

            double hours = calculateHours(startTime, endTime);
            double totalAmount = hours * selectedVenue.getPricePerHour();

            Booking booking = new Booking(
                bookingId,
                customerId,
                selectedVenue.getVenueId(),
                eventDate,
                startTime,
                endTime,
                numberOfGuests,
                totalAmount
            );

            booking.setEventType(eventTypeCombo.getValue());
            booking.setEventName(eventNameField.getText().trim());
            booking.setContactPerson(contactPersonField.getText().trim());
            booking.setContactPhone(contactPhoneField.getText().trim());
            booking.setSpecialRequests(specialRequestsArea.getText().trim());

            boolean success = bookingDAO.createBooking(booking);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success",
                    "✅ Booking Confirmed!\n\n" +
                    "Booking ID: " + bookingId + "\n" +
                    "Event: " + booking.getEventName() + "\n" +
                    "Date: " + eventLocalDate + "\n" +
                    "Time: " + startTime + " - " + endTime + "\n" +
                    "Total: $" + String.format("%.2f", totalAmount));

                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", 
                    "❌ Failed to create booking. Please try again.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", 
                "An error occurred: " + e.getMessage());
        }
    }

    @FXML
    public void handleCancel() {
        System.out.println("❌ Cancel button clicked!");
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Booking");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("Do you want to cancel this booking?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            closeWindow();
        }
    }

    private boolean validateForm() {
        if (eventNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter event name");
            return false;
        }

        if (eventTypeCombo.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select event type");
            return false;
        }

        if (eventDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select event date");
            return false;
        }

        if (eventDatePicker.getValue().isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                "Event date must be in the future");
            return false;
        }

        String startTime = startTimeField.getText().trim();
        if (startTime.isEmpty() || !startTime.matches("\\d{2}:\\d{2}")) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                "Start time must be in format HH:MM (e.g., 09:00)");
            return false;
        }

        String endTime = endTimeField.getText().trim();
        if (endTime.isEmpty() || !endTime.matches("\\d{2}:\\d{2}")) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                "End time must be in format HH:MM (e.g., 17:00)");
            return false;
        }

        if (calculateHours(startTime, endTime) <= 0) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                "End time must be after start time");
            return false;
        }

        if (numberOfGuestsField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                "Please enter number of guests");
            return false;
        }

        try {
            int guests = Integer.parseInt(numberOfGuestsField.getText().trim());
            if (guests <= 0) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", 
                    "Number of guests must be positive");
                return false;
            }
            if (guests > selectedVenue.getCapacity()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Guests (" + guests + ") exceed venue capacity (" + 
                    selectedVenue.getCapacity() + ")");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                "Number of guests must be a valid number");
            return false;
        }

        if (contactPersonField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                "Please enter contact person name");
            return false;
        }

        String phone = contactPhoneField.getText().trim();
        if (phone.isEmpty() || !phone.matches("\\d{10,15}")) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                "Phone number must be 10-15 digits");
            return false;
        }

        return true;
    }

    private double calculateHours(String startTime, String endTime) {
        try {
            String[] startParts = startTime.split(":");
            String[] endParts = endTime.split(":");
            
            int startHour = Integer.parseInt(startParts[0]);
            int startMin = Integer.parseInt(startParts[1]);
            int endHour = Integer.parseInt(endParts[0]);
            int endMin = Integer.parseInt(endParts[1]);

            return (endHour + endMin/60.0) - (startHour + startMin/60.0);
        } catch (Exception e) {
            return 0;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }
}
