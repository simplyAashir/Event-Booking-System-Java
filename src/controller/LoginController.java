package controller;

import service.AuthenticationService;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML private Hyperlink registerLink;
    @FXML private Hyperlink forgotPasswordLink;
    
    private AuthenticationService authService;
    
    public LoginController() {
        this.authService = new AuthenticationService();
    }
    
    @FXML
    public void initialize() {
        // Clear message label
        messageLabel.setText("");
    }
    
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        
        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            showMessage("Please enter both email and password", "error");
            return;
        }
        
        if (!email.contains("@")) {
            showMessage("Please enter a valid email address", "error");
            return;
        }
        
        // Attempt login
        User user = authService.login(email, password);
        
        if (user != null) {
            showMessage("Login successful! Welcome " + user.getFullName(), "success");
            
            // Close login window and open dashboard
            openDashboard(user);
            
        } else {
            showMessage("Login failed. Please check your credentials.", "error");
        }
    }
    
    
    @FXML
    private void handleRegisterLink() {
        try {
            // Load registration screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Register.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Register - Event Booking System");
            stage.setScene(new Scene(root, 700, 700));
            stage.setResizable(false);
            stage.show();
            
            // Close current login window
            Stage currentStage = (Stage) registerLink.getScene().getWindow();
            currentStage.close();
            
        } catch (Exception e) {
            showAlert("Error", "Cannot open registration form: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleForgotPassword() {
        try {
            // Load forgot password screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ForgotPassword.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Forgot Password - Event Booking System");
            stage.setScene(new Scene(root, 700, 700));
            stage.setResizable(true);
            stage.show();
            
            // Close current login window (optional)
            // Stage currentStage = (Stage) forgotPasswordLink.getScene().getWindow();
            // currentStage.close();
            
        } catch (Exception e) {
            showAlert("Error", "Cannot open forgot password form: " + e.getMessage());
        }
    }
    
    private void openDashboard(User user) {
        try {
            String fxmlFile = "";
            String title = "";
            
            if (user instanceof model.Customer) {
                fxmlFile = "/view/CustomerDashboard.fxml";
                title = "Customer Dashboard";
            } else if (user instanceof model.VenueOwner) {
                fxmlFile = "/view/OwnerDashboard.fxml";
                title = "Venue Owner Dashboard";
            } else if (user instanceof model.Admin) {
                fxmlFile = "/view/AdminDashboard.fxml";  // ✅ Add Admin dashboard
                title = "Admin Dashboard";
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            
            // Pass user data to dashboard controller
            if (user instanceof model.Customer) {
                CustomerDashboardController controller = loader.getController();
                controller.setUserData(user.getUserId(), user.getFullName());
            } else if (user instanceof model.VenueOwner) {
                OwnerDashboardController controller = loader.getController();
                controller.setUserData(user.getUserId(), user.getFullName());
            } else if (user instanceof model.Admin) {
                AdminDashboardController controller = loader.getController();  // ✅ Add Admin controller
                controller.setUserData(user.getUserId(), user.getFullName());
            }
            
            Stage stage = new Stage();
            stage.setTitle(title + " - Event Booking System");
            stage.setScene(new Scene(root, 1200, 800));
            stage.show();
            
            // Close login window
            Stage currentStage = (Stage) emailField.getScene().getWindow();
            currentStage.close();
            
        } catch (Exception e) {
            showAlert("Error", "Cannot open dashboard: " + e.getMessage());
        }
    }
    private void showMessage(String message, String type) {
        messageLabel.setText(message);
        if ("error".equals(type)) {
            messageLabel.setStyle("-fx-text-fill: #e74c3c;");
        } else {
            messageLabel.setStyle("-fx-text-fill: #27ae60;");
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}