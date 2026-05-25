package controller;

import service.AdminService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.util.Map;

public class SystemOverviewController {
    
    @FXML private Label totalUsersLabel;
    @FXML private Label totalVenuesLabel;
    @FXML private Label totalBookingsLabel;
    @FXML private Label revenueLabel;
    @FXML private VBox statsContainer;
    
    private AdminService adminService;
    
    public SystemOverviewController() {
        this.adminService = new AdminService();
    }
    
    @FXML
    public void initialize() {
        loadSystemStats();
    }
    
    private void loadSystemStats() {
        try {
            Map<String, Object> stats = adminService.getSystemStats();
            
            // Update main stats
            totalUsersLabel.setText("👥 " + stats.get("totalUsers") + " Users");
            totalVenuesLabel.setText("🏢 " + stats.get("totalVenues") + " Venues");
            totalBookingsLabel.setText("📋 " + stats.get("totalBookings") + " Bookings");
            revenueLabel.setText("💰 $" + stats.get("totalRevenue"));
            
            // Display additional stats
            displayAdditionalStats(stats);
            
        } catch (Exception e) {
            System.err.println("Error loading system stats: " + e.getMessage());
        }
    }
    
    private void displayAdditionalStats(Map<String, Object> stats) {
        statsContainer.getChildren().clear();
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: white; -fx-border-radius: 10;");
        
        // Row 0
        grid.add(createStatCard("✅ Approved Bookings", stats.get("approvedBookings").toString(), "#27ae60"), 0, 0);
        grid.add(createStatCard("⏳ Pending Bookings", stats.get("pendingBookings").toString(), "#f39c12"), 1, 0);
        
        // Row 1
        grid.add(createStatCard("❌ Rejected Bookings", stats.get("rejectedBookings").toString(), "#e74c3c"), 0, 1);
        grid.add(createStatCard("🏪 Active Venues", stats.get("activeVenues").toString(), "#3498db"), 1, 1);
        
        // Row 2
        grid.add(createStatCard("👤 Customers", stats.get("totalCustomers").toString(), "#9b59b6"), 0, 2);
        grid.add(createStatCard("🏢 Venue Owners", stats.get("totalOwners").toString(), "#e67e22"), 1, 2);
        
        statsContainer.getChildren().add(grid);
    }
    
    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: " + color + "; -fx-padding: 15; -fx-border-radius: 8;");
        card.setPrefWidth(180);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
}