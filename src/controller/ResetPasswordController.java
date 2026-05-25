package controller;

import service.AuthenticationService;
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

public class ResetPasswordController {
    
    @FXML private TextField tokenField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;
    @FXML private Hyperlink backToLoginLink;
    
    private AuthenticationService authService;
    private String resetToken;
    
    public ResetPasswordController() {
        this.authService = new AuthenticationService();
    }
    
    @FXML
    public void initialize() {
        messageLabel.setText("");
    }
    
    public void setResetToken(String token) {
        this.resetToken = token;
        tokenField.setText(token);
    }
    
    @FXML
    private void handleResetPassword() {
        String token = tokenField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        
        // Validate inputs
        if (token.isEmpty()) {
            showMessage("Reset token is required", "error");
            return;
        }
        
        if (newPassword.length() < 6) {
            showMessage("Password must be at least 6 characters", "error");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            showMessage("Passwords do not match", "error");
            return;
        }
        
        // Validate token first
        if (!authService.validateResetToken(token)) {
            showMessage("Invalid or expired reset token. Please request a new reset link.", "error");
            return;
        }
        
        // Reset password
        boolean success = authService.resetPassword(token, newPassword);
        
        if (success) {
            showMessage("Password reset successfully! You can now login with your new password.", "success");
            
            // Auto-redirect to login after 2 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(this::handleBackToLogin);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            
        } else {
            showMessage("Password reset failed. Please try again.", "error");
        }
    }
    
    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Login - Event Booking System");
            stage.setScene(new Scene(root, 400, 400));
            stage.setResizable(false);
            stage.show();
            
            Stage currentStage = (Stage) backToLoginLink.getScene().getWindow();
            currentStage.close();
            
        } catch (Exception e) {
            showAlert("Error", "Cannot open login form: " + e.getMessage());
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