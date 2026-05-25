package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class CustomerDashboardController {
    
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    @FXML private Button searchVenuesButton;
    @FXML private Button myBookingsButton;
    @FXML private Button paymentRecordsButton;
    @FXML private Button profileButton;
    @FXML private StackPane contentArea;
    
    private String currentUserId;
    private String currentUserName;
    
    @FXML
    public void initialize() {
        // This will be called when the dashboard loads
        welcomeLabel.setText("Welcome, Customer!");
        
        // Set initial content
        showDefaultContent();
    }
    
    public void setUserData(String userId, String userName) {
        this.currentUserId = userId;
        this.currentUserName = userName;
        welcomeLabel.setText("Welcome, " + userName + "!");
    }
    
    @FXML
    private void handleSearchVenues() {
        try {
            // Load venue search interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/VenueSearch.fxml"));
            Parent venueSearchContent = loader.load();
            
            // Pass customer data to venue search controller
            VenueSearchController controller = loader.getController();
            controller.setCustomerData(currentUserId, currentUserName);
            
            // Replace content area with venue search
            contentArea.getChildren().clear();
            contentArea.getChildren().add(venueSearchContent);
            
        } catch (Exception e) {
            showAlert("Error", "Cannot load venue search: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleMyBookings() {
        try {
            // Load my bookings interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MyBookings.fxml"));
            Parent myBookingsContent = loader.load();
            
            // Pass customer data to my bookings controller
            MyBookingsController controller = loader.getController();
            controller.setCustomerId(currentUserId);
            
            // Replace content area with my bookings
            contentArea.getChildren().clear();
            contentArea.getChildren().add(myBookingsContent);
            
        } catch (Exception e) {
            showAlert("Error", "Cannot load my bookings: " + e.getMessage());
        }
    }
    
 // Add this method to your existing CustomerDashboardController
    @FXML
    private void handlePaymentRecords() {
        try {
            System.out.println("🔧 Loading payment records for customer: " + currentUserId);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PaymentRecords.fxml"));
            Parent paymentRecordsContent = loader.load();
            
            PaymentRecordsController controller = loader.getController();
            controller.setCustomerId(currentUserId);
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(paymentRecordsContent);
            
            System.out.println("✅ Payment records loaded successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error loading payment records: " + e.getMessage());
            showAlert("Error", "Cannot load payment records: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleProfile() {
        try {
            System.out.println("🔧 Loading profile for customer: " + currentUserId);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Profile.fxml")); // ✅ Correct file
            Parent profileContent = loader.load();
            
            ProfileController controller = loader.getController();
            controller.setCustomer(currentUserId);
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(profileContent);
            
            System.out.println("✅ Profile loaded successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error loading profile: " + e.getMessage());
            showAlert("Error", "Cannot load profile: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleLogout() {
        try {
            // Show confirmation
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Logout");
            alert.setHeaderText("Confirm Logout");
            alert.setContentText("Are you sure you want to logout?");
            
            if (alert.showAndWait().get().getText().equals("OK")) {
                // Load login screen
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
                Parent root = loader.load();
                
                Stage stage = new Stage();
                stage.setTitle("Login - Event Booking System");
                stage.setScene(new Scene(root, 700, 750));
                stage.setResizable(false);
                stage.show();
                
                // Close current dashboard
                Stage currentStage = (Stage) logoutButton.getScene().getWindow();
                currentStage.close();
                
                System.out.println("✅ Customer logged out: " + currentUserName);
            }
            
        } catch (Exception e) {
            showAlert("Error", "Cannot logout: " + e.getMessage());
        }
    }
    
    private void showDefaultContent() {
        contentArea.getChildren().clear();
        Label welcome = new Label("Welcome to Customer Dashboard! 👋\n\n"
                + "• Search Venues - Find and book venues\n"
                + "• My Bookings - View your booking history\n"
                + "• Payment Records - Track payments\n"
                + "• My Profile - Update your information\n\n"
                + "Select an option from the left menu to get started!");
        welcome.setStyle("-fx-font-size: 16; -fx-text-alignment: center;");
        welcome.setWrapText(true);
        contentArea.getChildren().add(welcome);
    }
    
    private void showMessage(String message) {
        contentArea.getChildren().clear();
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 18; -fx-text-alignment: center; -fx-padding: 20;");
        messageLabel.setWrapText(true);
        contentArea.getChildren().add(messageLabel);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}