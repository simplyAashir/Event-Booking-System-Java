package controller;

import service.BookingService;
import service.PaymentService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Map;
import java.util.HashMap;

public class RevenueReportsController {
    
    @FXML private Label summaryLabel;
    @FXML private ComboBox<String> timePeriodCombo;
    @FXML private Button generateReportButton;
    @FXML private VBox chartsContainer;
    
    private BookingService bookingService;
    private PaymentService paymentService;
    private String currentOwnerId;
    
    public RevenueReportsController() {
        this.bookingService = new BookingService();
        this.paymentService = new PaymentService();
    }
    
    @FXML
    public void initialize() {
        setupTimePeriods();
        System.out.println("🔧 RevenueReportsController initialized");
    }
    
    public void setOwnerId(String ownerId) {
        this.currentOwnerId = ownerId;
        generateReport();
    }
    
    private void setupTimePeriods() {
        timePeriodCombo.getItems().addAll(
            "Last 7 Days", "Last 30 Days", "Last 3 Months", "Last 6 Months", "This Year"
        );
        timePeriodCombo.setValue("Last 30 Days");
    }
    
    @FXML
    private void handleGenerateReport() {
        generateReport();
    }
    
    private void generateReport() {
        if (currentOwnerId == null) return;
        
        chartsContainer.getChildren().clear();
        
        // Demo data - in real app, fetch from database
        Map<String, Double> revenueData = getDemoRevenueData();
        Map<String, Integer> bookingData = getDemoBookingData();
        
        displaySummary(revenueData);
        createRevenueChart(revenueData);
        createBookingChart(bookingData);
    }
    
    private Map<String, Double> getDemoRevenueData() {
        Map<String, Double> data = new HashMap<>();
        data.put("Jan", 4500.0);
        data.put("Feb", 5200.0);
        data.put("Mar", 4800.0);
        data.put("Apr", 6100.0);
        data.put("May", 5500.0);
        data.put("Jun", 7200.0);
        return data;
    }
    
    private Map<String, Integer> getDemoBookingData() {
        Map<String, Integer> data = new HashMap<>();
        data.put("Wedding", 15);
        data.put("Conference", 12);
        data.put("Seminar", 8);
        data.put("Party", 20);
        data.put("Meeting", 25);
        return data;
    }
    
    private void displaySummary(Map<String, Double> revenueData) {
        double totalRevenue = revenueData.values().stream().mapToDouble(Double::doubleValue).sum();
        double averageRevenue = totalRevenue / revenueData.size();
        
        summaryLabel.setText(String.format("💰 Revenue Summary: Total: $%.2f | Average: $%.2f/month", 
                                          totalRevenue, averageRevenue));
    }
    
    private void createRevenueChart(Map<String, Double> revenueData) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        
        barChart.setTitle("Monthly Revenue");
        barChart.setLegendVisible(false);
        barChart.setPrefHeight(300);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");
        
        for (Map.Entry<String, Double> entry : revenueData.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        
        barChart.getData().add(series);
        chartsContainer.getChildren().add(barChart);
    }
    
    private void createBookingChart(Map<String, Integer> bookingData) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        
        barChart.setTitle("Bookings by Event Type");
        barChart.setLegendVisible(false);
        barChart.setPrefHeight(300);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Bookings");
        
        for (Map.Entry<String, Integer> entry : bookingData.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        
        barChart.getData().add(series);
        chartsContainer.getChildren().add(barChart);
    }
}