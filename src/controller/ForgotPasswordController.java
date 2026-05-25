package controller;

import service.AuthenticationService;
import util.DemoTokenStorage;
import dao.PasswordResetDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ForgotPasswordController {
    
    @FXML private TextField emailField;
    @FXML private Label messageLabel;
    @FXML private Hyperlink backToLoginLink;
    @FXML private VBox demoSection;
    @FXML private Label demoTokenLabel;
    @FXML private Hyperlink demoResetLink;
    
    private AuthenticationService authService;
    
    public ForgotPasswordController() {
        this.authService = new AuthenticationService();
    }
    
    @FXML
    public void initialize() {
        messageLabel.setText("");
        demoSection.setVisible(false);
    }
    
    @FXML
    private void handleSendReset() {
        String email = emailField.getText().trim();
        
        if (email.isEmpty() || !email.contains("@")) {
            showMessage("Please enter a valid email address", "error");
            return;
        }
        
        // Send reset instructions
        boolean success = authService.forgotPassword(email);
        
        if (success) {
            showMessage("Password reset instructions have been sent to your email.\nCheck the console for demo token.", "success");
            
            // Show demo section for testing
            demoSection.setVisible(true);
            String token = DemoTokenStorage.getLastResetToken();
            demoTokenLabel.setText("Demo Token: " + token + "\n(For testing purposes - copy this token)");
            
        } else {
            showMessage("If this email is registered, reset instructions will be sent.", "info");
            // For security, we don't reveal if email exists or not
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
    
    @FXML
    private void handleDemoReset() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showMessage("Enter email first", "error");
            return;
        }

        PasswordResetDAO passwordResetDAO = new PasswordResetDAO();
        String token = passwordResetDAO.getLatestTokenForEmail(email); // <-- implement this
        if (token != null) {
            openResetPasswordForm(token);
        } else {
            showMessage("No reset token available", "error");
        }
    }

    
    private void openResetPasswordForm(String token) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ResetPassword.fxml"));
            Parent root = loader.load();
            
            // Pass token to reset password controller
            ResetPasswordController controller = loader.getController();
            controller.setResetToken(token);
            
            Stage stage = new Stage();
            stage.setTitle("Reset Password - Event Booking System");
            stage.setScene(new Scene(root, 400, 400));
            stage.setResizable(false);
            stage.show();
            
            Stage currentStage = (Stage) demoResetLink.getScene().getWindow();
            currentStage.close();
            
        } catch (Exception e) {
            showAlert("Error", "Cannot open reset password form: " + e.getMessage());
        }
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
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}