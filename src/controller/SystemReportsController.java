package controller;

import service.AdminService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.HashMap;
import java.util.Map;

public class SystemReportsController {
    
    @FXML private ComboBox<String> reportTypeCombo;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private ComboBox<String> formatCombo;
    @FXML private Button generateButton;
    @FXML private Button exportButton;
    @FXML private TextArea reportOutput;
    @FXML private VBox reportContainer;
    
    private AdminService adminService;
    private String currentAdminId;
    
    public SystemReportsController() {
        this.adminService = new AdminService();
    }
    
    @FXML
    public void initialize() {
        setupReportTypes();
        setupFormats();
        System.out.println("📊 SystemReportsController initialized");
    }
    
    public void setAdminId(String adminId) {
        this.currentAdminId = adminId;
    }
    
    private void setupReportTypes() {
        reportTypeCombo.getItems().addAll(
            "BOOKING_SUMMARY",
            "REVENUE", 
            "USER_ACTIVITY",
            "VENUE_PERFORMANCE",
            "PAYMENT_STATUS"
        );
        reportTypeCombo.setValue("BOOKING_SUMMARY");
    }
    
    private void setupFormats() {
        formatCombo.getItems().addAll("PDF", "EXCEL", "CSV", "SCREEN");
        formatCombo.setValue("SCREEN");
    }
    
    @FXML
    private void handleGenerateReport() {
        try {
            String reportType = reportTypeCombo.getValue();
            String format = formatCombo.getValue();
            
            Map<String, Object> parameters = new HashMap<>();
            
            // Add date parameters if selected
            if (fromDatePicker.getValue() != null) {
                parameters.put("fromDate", java.sql.Date.valueOf(fromDatePicker.getValue()));
            }
            if (toDatePicker.getValue() != null) {
                parameters.put("toDate", java.sql.Date.valueOf(toDatePicker.getValue()));
            }
            
            // Generate report
            Map<String, Object> report = adminService.generateSystemReport(reportType, parameters);
            
            // Display report
            if (report.containsKey("error")) {
                reportOutput.setText("❌ Error: " + report.get("error"));
            } else {
                String reportData = formatReportData(report);
                reportOutput.setText(reportData);
                
                if ("SCREEN".equals(format)) {
                    showAlert("Report Generated", 
                        "✅ " + reportType + " report generated successfully!\n\n" +
                        "Generated at: " + report.get("generatedAt") + "\n" +
                        "Preview displayed below."
                    );
                }
            }
            
        } catch (Exception e) {
            showAlert("Error", "Failed to generate report: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleExportReport() {
        String format = formatCombo.getValue();
        String reportType = reportTypeCombo.getValue();
        
        if ("SCREEN".equals(format)) {
            showAlert("Export", "Report is already displayed on screen.");
            return;
        }
        
        showAlert("Export Report", 
            "📤 Exporting " + reportType + " report to " + format + " format\n\n" +
            "In a full implementation, this would:\n" +
            "• Generate " + format + " file\n" +
            "• Download to user's computer\n" +
            "• Include all charts and data tables\n" +
            "• Have professional formatting"
        );
    }
    
    private String formatReportData(Map<String, Object> report) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("📊 SYSTEM REPORT\n");
        sb.append("================\n\n");
        
        sb.append("Report Type: ").append(report.get("reportType")).append("\n");
        sb.append("Generated: ").append(report.get("generatedAt")).append("\n\n");
        
        Object data = report.get("data");
        if (data instanceof String) {
            sb.append(data);
        } else {
            sb.append("Report Data:\n").append(data.toString());
        }
        
        return sb.toString();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}