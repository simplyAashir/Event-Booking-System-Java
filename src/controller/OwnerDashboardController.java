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

public class OwnerDashboardController {
    
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    @FXML private Button manageVenuesButton;
    @FXML private Button bookingRequestsButton;
    @FXML private Button revenueButton;
    @FXML private Button ownerProfileButton;
    @FXML private StackPane contentArea;
    
    private String currentUserId;
    private String currentUserName;
    
    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome, Venue Owner!");
        showDefaultContent();
    }
    
    public void setUserData(String userId, String userName) {
        this.currentUserId = userId;
        this.currentUserName = userName;
        welcomeLabel.setText("Welcome, " + userName + "!");
    }
    
   
    
 // Add this method to your existing OwnerDashboardController
    @FXML
    private void handleBookingRequests() {
        try {
            System.out.println("🔧 Loading booking requests for owner: " + currentUserId);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BookingRequests.fxml"));
            Parent bookingRequestsContent = loader.load();
            
            BookingRequestsController controller = loader.getController();
            controller.setOwnerId(currentUserId);
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(bookingRequestsContent);
            
            System.out.println("✅ Booking requests loaded successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error loading booking requests: " + e.getMessage());
            showAlert("Error", "Cannot load booking requests: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRevenueReports() {
        try {
            System.out.println("🔧 Loading revenue reports for owner: " + currentUserId);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/RevenueReports.fxml"));
            Parent revenueReportsContent = loader.load();
            
            RevenueReportsController controller = loader.getController();
            controller.setOwnerId(currentUserId);
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(revenueReportsContent);
            
            System.out.println("✅ Revenue reports loaded successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error loading revenue reports: " + e.getMessage());
            showAlert("Error", "Cannot load revenue reports: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleProfile() {
        showMessage("Profile management coming soon! 👤");
    }
    @FXML
    private void handleManageVenues() {
        try {
            System.out.println("🔧 Loading venue management for owner: " + currentUserId);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ManageVenues.fxml"));
            Parent manageVenuesContent = loader.load();
            
            ManageVenuesController controller = loader.getController();
            controller.setOwnerId(currentUserId);
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(manageVenuesContent);
            
            System.out.println("✅ Venue management loaded successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error loading venue management: " + e.getMessage());
            showAlert("Error", "Cannot load venue management: " + e.getMessage());
        }
    }
    
    
    
    @FXML
    private void handleLogout() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Logout");
            alert.setHeaderText("Confirm Logout");
            alert.setContentText("Are you sure you want to logout?");
            
            if (alert.showAndWait().get().getText().equals("OK")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
                Parent root = loader.load();
                
                Stage stage = new Stage();
                stage.setTitle("Login - Event Booking System");
                stage.setScene(new Scene(root, 700, 700));
                stage.setResizable(false);
                stage.show();
                
                Stage currentStage = (Stage) logoutButton.getScene().getWindow();
                currentStage.close();
                
                System.out.println("✅ Venue Owner logged out: " + currentUserName);
            }
            
        } catch (Exception e) {
            showAlert("Error", "Cannot logout: " + e.getMessage());
        }
    }
    
    private void showDefaultContent() {
        contentArea.getChildren().clear();
        Label welcome = new Label("Welcome to Venue Owner Dashboard! 🏢\n\n"
                + "• Manage Venues - Add and edit your venues\n"
                + "• Booking Requests - Review customer requests\n"
                + "• Revenue Reports - View financial reports\n"
                + "• My Profile - Update owner information\n\n"
                + "Select an option from the left menu to manage your business!");
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