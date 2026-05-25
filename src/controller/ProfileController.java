package controller;

import service.UserService;
import model.Customer;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;

public class ProfileController {
    
    @FXML private Label welcomeLabel;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextArea preferencesArea;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button saveProfileButton;
    @FXML private Button changePasswordButton;
    @FXML private Label messageLabel;
    
    private UserService userService;
    private Customer currentCustomer;
    private String currentCustomerId;
    
    public ProfileController() {
        this.userService = new UserService();
    }
    
    @FXML
    public void initialize() {
        messageLabel.setText("");
        System.out.println("🔧 ProfileController initialized");
    }
    
    public void setCustomer(String customerId) {
        this.currentCustomerId = customerId;
        loadCustomerData();
    }
    
    private void loadCustomerData() {
        try {
            // Fetch customer data from database
            this.currentCustomer = userService.getCustomerById(currentCustomerId);
            
            if (currentCustomer != null) {
                displayCustomerData();
                System.out.println("✅ Customer data loaded from database: " + currentCustomer.getFullName());
            } else {
                showMessage("❌ Error: Customer data not found in database", "error");
                System.err.println("❌ Customer not found in database for ID: " + currentCustomerId);
            }
            
        } catch (Exception e) {
            showMessage("❌ Error loading customer data: " + e.getMessage(), "error");
            System.err.println("❌ Error loading customer data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void displayCustomerData() {
        if (currentCustomer != null) {
            if (welcomeLabel != null) {
                welcomeLabel.setText("👤 Profile - " + currentCustomer.getFullName());
            }
            if (fullNameField != null) {
                fullNameField.setText(currentCustomer.getFullName());
            }
            if (emailField != null) {
                emailField.setText(currentCustomer.getEmail());
            }
            if (phoneField != null) {
                phoneField.setText(currentCustomer.getPhoneNo() != null ? currentCustomer.getPhoneNo() : "");
            }
            if (preferencesArea != null) {
                preferencesArea.setText(currentCustomer.getPreferences() != null ? currentCustomer.getPreferences() : "");
            }
        }
    }
    
    @FXML
    private void handleSaveProfile() {
        try {
            // Validate inputs
            if (fullNameField.getText().trim().isEmpty()) {
                showMessage("Full name is required", "error");
                return;
            }
            
            if (emailField.getText().trim().isEmpty() || !emailField.getText().contains("@")) {
                showMessage("Valid email is required", "error");
                return;
            }
            
            // Update customer object
            currentCustomer.setFullName(fullNameField.getText().trim());
            currentCustomer.setEmail(emailField.getText().trim());
            currentCustomer.setPhoneNo(phoneField.getText().trim());
            currentCustomer.setPreferences(preferencesArea.getText().trim());
            
            // Save to database
            boolean success = userService.updateCustomerProfile(currentCustomer);
            
            if (success) {
                showMessage("✅ Profile updated successfully!", "success");
                
                // Update welcome label
                if (welcomeLabel != null) {
                    welcomeLabel.setText("👤 Profile - " + currentCustomer.getFullName());
                }
                
                System.out.println("✅ Profile saved to database for customer: " + currentCustomerId);
            } else {
                showMessage("❌ Failed to update profile in database", "error");
            }
            
        } catch (Exception e) {
            showMessage("❌ Error updating profile: " + e.getMessage(), "error");
            System.err.println("❌ Error updating profile: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleChangePassword() {
        try {
            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            
            // Validate inputs
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showMessage("All password fields are required", "error");
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                showMessage("New passwords do not match", "error");
                return;
            }
            
            if (newPassword.length() < 6) {
                showMessage("New password must be at least 6 characters", "error");
                return;
            }
            
            // Verify current password and update in database
            boolean success = userService.changeCustomerPassword(currentCustomerId, currentPassword, newPassword);
            
            if (success) {
                showMessage("✅ Password changed successfully!", "success");
                
                // Clear password fields
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
                
                System.out.println("✅ Password changed for customer: " + currentCustomerId);
            } else {
                showMessage("❌ Failed to change password. Please check your current password.", "error");
            }
            
        } catch (Exception e) {
            showMessage("❌ Error changing password: " + e.getMessage(), "error");
            System.err.println("❌ Error changing password: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleResetForm() {
        loadCustomerData(); // Reload from database
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
        showMessage("Form reset to original values", "info");
    }
    
    private void showMessage(String message, String type) {
        messageLabel.setText(message);
        switch (type) {
            case "error":
                messageLabel.setStyle("-fx-text-fill: #e74c3c;");
                break;
            case "success":
                messageLabel.setStyle("-fx-text-fill: #27ae60;");
                break;
            case "info":
                messageLabel.setStyle("-fx-text-fill: #3498db;");
                break;
        }
    }
}