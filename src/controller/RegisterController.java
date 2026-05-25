package controller;

import service.AuthenticationService;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class RegisterController {
    
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> userTypeComboBox;
    @FXML private VBox additionalFields;
    @FXML private Label messageLabel;
    @FXML private Hyperlink loginLink;
    
    private AuthenticationService authService;
    private TextField preferencesField;
    private TextField taxIdField;
    private TextField adminLevelField;
    
    public RegisterController() {
        this.authService = new AuthenticationService();
    }
    
    @FXML
    public void initialize() {
        // Setup user type options
        userTypeComboBox.getItems().addAll("CUSTOMER", "VENUE_OWNER", "ADMIN");
        userTypeComboBox.setValue("CUSTOMER");
        
        // Listen for user type changes
        userTypeComboBox.setOnAction(e -> handleUserTypeChange());
        
        // Initialize additional fields
        handleUserTypeChange();
        
        // Clear message label
        messageLabel.setText("");
    }
    
    private void handleUserTypeChange() {
        additionalFields.getChildren().clear();
        
        String userType = userTypeComboBox.getValue();
        
        switch (userType) {
            case "CUSTOMER":
                Label prefLabel = new Label("Preferences");
                preferencesField = new TextField();
                preferencesField.setPromptText("e.g., Wedding, Conference, Party");
                additionalFields.getChildren().addAll(prefLabel, preferencesField);
                break;
                
            case "VENUE_OWNER":
                Label taxLabel = new Label("Tax ID");
                taxIdField = new TextField();
                taxIdField.setPromptText("Enter your tax identification number");
                additionalFields.getChildren().addAll(taxLabel, taxIdField);
                break;
                
            case "ADMIN":
                Label adminLabel = new Label("Admin Level");
                adminLevelField = new TextField();
                adminLevelField.setPromptText("e.g., SUPER_ADMIN, MANAGER");
                adminLevelField.setText("MANAGER"); // Default value
                additionalFields.getChildren().addAll(adminLabel, adminLevelField);
                break;
        }
    }
    
    @FXML
    private void handleRegister() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }
        
        // Prepare registration details
        Map<String, String> details = new HashMap<>();
        details.put("fullName", fullNameField.getText().trim());
        details.put("email", emailField.getText().trim());
        details.put("phoneNo", phoneField.getText().trim());
        details.put("password", passwordField.getText().trim());
        
        // Add user-type specific details
        String userType = userTypeComboBox.getValue();
        switch (userType) {
            case "CUSTOMER":
                details.put("preferences", preferencesField.getText().trim());
                break;
            case "VENUE_OWNER":
                details.put("taxId", taxIdField.getText().trim());
                break;
            case "ADMIN":
                details.put("adminLevel", adminLevelField.getText().trim());
                break;
        }
        
        // Register user
        User user = authService.register(userType, details);
        
        if (user != null) {
            showMessage("Registration successful! You can now login.", "success");
            
            // Auto-login and open dashboard
            openDashboard(user);
            
        } else {
            showMessage("Registration failed. Please try again.", "error");
        }
    }
    
    @FXML
    private void handleLoginLink() {
        try {
            // Load login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Login - Event Booking System");
            stage.setScene(new Scene(root, 400, 400));
            stage.setResizable(false);
            stage.show();
            
            // Close current registration window
            Stage currentStage = (Stage) loginLink.getScene().getWindow();
            currentStage.close();
            
        } catch (Exception e) {
            showAlert("Error", "Cannot open login form: " + e.getMessage());
        }
    }
    
    private boolean validateInputs() {
        // Check required fields
        if (fullNameField.getText().trim().isEmpty()) {
            showMessage("Full name is required", "error");
            return false;
        }
        
        if (emailField.getText().trim().isEmpty() || !emailField.getText().contains("@")) {
            showMessage("Valid email is required", "error");
            return false;
        }
        
        if (passwordField.getText().length() < 6) {
            showMessage("Password must be at least 6 characters", "error");
            return false;
        }
        
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showMessage("Passwords do not match", "error");
            return false;
        }
        
        // User-type specific validation
        String userType = userTypeComboBox.getValue();
        if ("VENUE_OWNER".equals(userType) && (taxIdField.getText().trim().isEmpty())) {
            showMessage("Tax ID is required for Venue Owners", "error");
            return false;
        }
        
        return true;
    }
    
    private void openDashboard(User user) {
        try {
            // Load appropriate dashboard
            String fxmlFile = "";
            String title = "";
            
            if (user instanceof model.Customer) {
                fxmlFile = "/view/CustomerDashboard.fxml";
                title = "Customer Dashboard";
            } else if (user instanceof model.VenueOwner) {
                fxmlFile = "/view/OwnerDashboard.fxml";
                title = "Venue Owner Dashboard";
            } else if (user instanceof model.Admin) {
                fxmlFile = "/view/AdminDashboard.fxml";
                title = "Admin Dashboard";
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle(title + " - Event Booking System");
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();
            
            // Close registration window
            Stage currentStage = (Stage) loginLink.getScene().getWindow();
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