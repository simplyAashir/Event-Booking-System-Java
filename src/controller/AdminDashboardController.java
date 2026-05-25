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

public class AdminDashboardController {
    
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    @FXML private Button systemOverviewButton;
    @FXML private Button manageUsersButton;
    @FXML private Button allBookingsButton;
    @FXML private Button systemReportsButton;
    @FXML private StackPane contentArea;
    
    private String currentUserId;
    private String currentUserName;
    
    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome, Administrator!");
        showDefaultContent();
    }
    
    public void setUserData(String userId, String userName) {
        this.currentUserId = userId;
        this.currentUserName = userName;
        welcomeLabel.setText("Welcome, " + userName + "! 👑");
    }
    
    @FXML
    private void handleSystemOverview() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SystemOverview.fxml"));
            Parent systemOverviewContent = loader.load();
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(systemOverviewContent);
            
        } catch (Exception e) {
            showAlert("Error", "Cannot load system overview: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleManageUsers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ManageUsers.fxml"));
            Parent manageUsersContent = loader.load();
            
            ManageUsersController controller = loader.getController();
            controller.setAdminId(currentUserId);
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(manageUsersContent);
            
        } catch (Exception e) {
            showAlert("Error", "Cannot load user management: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleAllBookings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AllBookings.fxml"));
            Parent allBookingsContent = loader.load();
            
            AllBookingsController controller = loader.getController();
            controller.setAdminId(currentUserId);
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(allBookingsContent);
            
        } catch (Exception e) {
            showAlert("Error", "Cannot load all bookings: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSystemReports() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SystemReports.fxml"));
            Parent systemReportsContent = loader.load();
            
            SystemReportsController controller = loader.getController();
            controller.setAdminId(currentUserId);
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(systemReportsContent);
            
        } catch (Exception e) {
            showAlert("Error", "Cannot load system reports: " + e.getMessage());
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
                
                System.out.println("✅ Admin logged out: " + currentUserName);
            }
            
        } catch (Exception e) {
            showAlert("Error", "Cannot logout: " + e.getMessage());
        }
    }
    
    private void showDefaultContent() {
        contentArea.getChildren().clear();
        Label welcome = new Label("👑 Welcome to Admin Dashboard!\n\n"
                + "• System Overview - View system statistics\n"
                + "• Manage Users - Manage all system users\n"
                + "• All Bookings - View and manage all bookings\n"
                + "• System Reports - Generate comprehensive reports\n\n"
                + "Select an option from the left menu to manage the system!");
        welcome.setStyle("-fx-font-size: 16; -fx-text-alignment: center;");
        welcome.setWrapText(true);
        contentArea.getChildren().add(welcome);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}