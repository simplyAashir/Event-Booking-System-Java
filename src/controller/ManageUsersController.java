package controller;

import service.AdminService;
import service.UserService;
import model.User;
import model.Customer;
import model.VenueOwner;
import model.Admin;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.ArrayList;

public class ManageUsersController {
    
    @FXML private ComboBox<String> userTypeFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button refreshButton;
    @FXML private Label resultsLabel;
    @FXML private VBox usersContainer;
    
    private AdminService adminService;
    private UserService userService;
    private String currentAdminId;
    
    // Demo data - in real app, fetch from database
    private ObservableList<User> allUsers = FXCollections.observableArrayList();
    
    public ManageUsersController() {
        this.adminService = new AdminService();
        this.userService = new UserService();
    }
    
    @FXML
    public void initialize() {
        setupFilters();
        loadDemoUsers(); // Load demo data
        System.out.println("🔧 ManageUsersController initialized");
    }
    
    public void setAdminId(String adminId) {
        this.currentAdminId = adminId;
        loadUsers();
    }
    
    private void setupFilters() {
        // User type filter
        userTypeFilterCombo.getItems().addAll("ALL", "CUSTOMER", "VENUE_OWNER", "ADMIN");
        userTypeFilterCombo.setValue("ALL");
        userTypeFilterCombo.setOnAction(e -> filterUsers());
        
        // Status filter
        statusFilterCombo.getItems().addAll("ALL", "ACTIVE", "INACTIVE");
        statusFilterCombo.setValue("ALL");
        statusFilterCombo.setOnAction(e -> filterUsers());
    }
    
    @FXML
    private void handleSearch() {
        filterUsers();
    }
    
    @FXML
    private void handleRefresh() {
        loadUsers();
    }
    
    private void loadDemoUsers() {
        // Create demo users
        allUsers.clear();
        
        // Demo Customers
        allUsers.add(new Customer("CUST001", "John Smith", "john@example.com", "+1234567890", "password"));
        allUsers.add(new Customer("CUST002", "Sarah Johnson", "sarah@example.com", "+1234567891", "password"));
        allUsers.add(new Customer("CUST003", "Mike Wilson", "mike@example.com", "+1234567892", "password"));
        
        // Demo Venue Owners
        allUsers.add(new VenueOwner("OWN001", "Alice Brown", "alice@venues.com", "+1234567893", "password", "TAX001"));
        allUsers.add(new VenueOwner("OWN002", "Bob Davis", "bob@venues.com", "+1234567894", "password", "TAX002"));
        
        // Demo Admins
        allUsers.add(new Admin("ADM001", "System Admin", "admin@system.com", "+1234567895", "password", "SUPER_ADMIN"));
        allUsers.add(new Admin("ADM002", "Manager Admin", "manager@system.com", "+1234567896", "password", "MANAGER"));
    }
    
    private void loadUsers() {
        filterUsers(); // Apply current filters
    }
    
    private void filterUsers() {
        String userTypeFilter = userTypeFilterCombo.getValue();
        String statusFilter = statusFilterCombo.getValue();
        String searchText = searchField.getText().toLowerCase();
        
        List<User> filteredUsers = new ArrayList<>();
        
        for (User user : allUsers) {
            // Apply user type filter
            if (!"ALL".equals(userTypeFilter)) {
                String userType = getUserType(user);
                if (!userType.equals(userTypeFilter)) {
                    continue;
                }
            }
            
            // Apply search filter
            if (!searchText.isEmpty()) {
                boolean matchesSearch = user.getFullName().toLowerCase().contains(searchText) ||
                                      user.getEmail().toLowerCase().contains(searchText) ||
                                      user.getUserId().toLowerCase().contains(searchText);
                if (!matchesSearch) {
                    continue;
                }
            }
            
            // All users are active in demo - in real app, check isActive()
            filteredUsers.add(user);
        }
        
        displayUsers(filteredUsers);
    }
    
    private String getUserType(User user) {
        if (user instanceof Customer) return "CUSTOMER";
        if (user instanceof VenueOwner) return "VENUE_OWNER";
        if (user instanceof Admin) return "ADMIN";
        return "UNKNOWN";
    }
    
    private void displayUsers(List<User> users) {
        usersContainer.getChildren().clear();
        
        if (users.isEmpty()) {
            resultsLabel.setText("No users found matching your criteria");
            return;
        }
        
        resultsLabel.setText("Found " + users.size() + " user(s)");
        
        for (User user : users) {
            VBox userCard = createUserCard(user);
            usersContainer.getChildren().add(userCard);
        }
    }
    
    private VBox createUserCard(User user) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-padding: 15; -fx-border-radius: 5;");
        card.setPrefWidth(700);
        
        // Header with user ID and type
        HBox header = new HBox();
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        String userIcon = getUserIcon(user);
        Label idLabel = new Label(userIcon + " " + user.getFullName() + " (" + user.getUserId() + ")");
        idLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label typeLabel = new Label(getUserType(user));
        typeLabel.setStyle(getUserTypeStyle(user));
        
        header.getChildren().addAll(idLabel, spacer, typeLabel);
        
        // User details
        Label emailLabel = new Label("📧 " + user.getEmail());
        Label phoneLabel = new Label("📞 " + (user.getPhoneNo() != null ? user.getPhoneNo() : "Not provided"));
        
        // User-specific details
        VBox detailsBox = new VBox(5);
        
        if (user instanceof Customer) {
            Customer customer = (Customer) user;
            String preferences = customer.getPreferences() != null ? customer.getPreferences() : "No preferences set";
            detailsBox.getChildren().add(new Label("🎯 Preferences: " + preferences));
        } else if (user instanceof VenueOwner) {
            VenueOwner owner = (VenueOwner) user;
            detailsBox.getChildren().add(new Label("🏢 Business: " + owner.getTaxId()));
        } else if (user instanceof Admin) {
            Admin admin = (Admin) user;
            detailsBox.getChildren().add(new Label("👑 Level: " + admin.getAdminLevel()));
        }
        
        // Registration info
        Label regLabel = new Label("📅 Registered: " + user.getRegistrationDate());
        regLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12;");
        
        // Action buttons
        HBox actions = new HBox(10);
        
        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        viewDetailsButton.setOnAction(e -> handleViewDetails(user));
        
        Button editButton = new Button("Edit");
        editButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        editButton.setOnAction(e -> handleEditUser(user));
        
        // Only show deactivate for non-admin users
        if (!(user instanceof Admin) || !user.getUserId().equals(currentAdminId)) {
            Button toggleButton = new Button(user.isActive() ? "Deactivate" : "Activate");
            toggleButton.setStyle(user.isActive() ? 
                "-fx-background-color: #e74c3c; -fx-text-fill: white;" : 
                "-fx-background-color: #27ae60; -fx-text-fill: white;");
            toggleButton.setOnAction(e -> handleToggleUserStatus(user));
            actions.getChildren().add(toggleButton);
        }
        
        actions.getChildren().addAll(viewDetailsButton, editButton);
        
        // Add all components to card
        card.getChildren().addAll(header, emailLabel, phoneLabel);
        
        if (!detailsBox.getChildren().isEmpty()) {
            card.getChildren().add(detailsBox);
        }
        
        card.getChildren().addAll(regLabel, actions);
        
        return card;
    }
    
    private String getUserIcon(User user) {
        if (user instanceof Customer) return "👤";
        if (user instanceof VenueOwner) return "🏢";
        if (user instanceof Admin) return "👑";
        return "❓";
    }
    
    private String getUserTypeStyle(User user) {
        switch (getUserType(user)) {
            case "CUSTOMER": return "-fx-text-fill: #3498db; -fx-font-weight: bold;";
            case "VENUE_OWNER": return "-fx-text-fill: #e67e22; -fx-font-weight: bold;";
            case "ADMIN": return "-fx-text-fill: #9b59b6; -fx-font-weight: bold;";
            default: return "-fx-text-fill: #34495e;";
        }
    }
    
    private void handleViewDetails(User user) {
        String userType = getUserType(user);
        String details = "";
        
        if (user instanceof Customer) {
            Customer customer = (Customer) user;
            details = "Preferences: " + (customer.getPreferences() != null ? customer.getPreferences() : "Not set");
        } else if (user instanceof VenueOwner) {
            VenueOwner owner = (VenueOwner) user;
            details = "Tax ID: " + owner.getTaxId();
        } else if (user instanceof Admin) {
            Admin admin = (Admin) user;
            details = "Admin Level: " + admin.getAdminLevel();
        }
        
        showAlert("User Details", 
            getUserIcon(user) + " " + user.getFullName() + "\n\n" +
            "🆔 User ID: " + user.getUserId() + "\n" +
            "📧 Email: " + user.getEmail() + "\n" +
            "📞 Phone: " + (user.getPhoneNo() != null ? user.getPhoneNo() : "Not provided") + "\n" +
            "👤 Type: " + userType + "\n" +
            "📅 Registered: " + user.getRegistrationDate() + "\n" +
            "✅ Status: " + (user.isActive() ? "Active" : "Inactive") + "\n" +
            "📝 " + details
        );
    }
    
    private void handleEditUser(User user) {
        showAlert("Edit User", 
            "✏️ Edit functionality for: " + user.getFullName() + "\n\n" +
            "In a full implementation, this would open an edit form.\n" +
            "User ID: " + user.getUserId()
        );
    }
    
    private void handleToggleUserStatus(User user) {
        String action = user.isActive() ? "deactivate" : "activate";
        String newStatus = user.isActive() ? "inactive" : "active";
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm User " + (user.isActive() ? "Deactivation" : "Activation"));
        alert.setHeaderText("Confirm User Status Change");
        alert.setContentText("Are you sure you want to " + action + " user:\n" + user.getFullName() + "?");
        
        if (alert.showAndWait().get().getText().equals("OK")) {
            // In real app, update in database
            user.setActive(!user.isActive());
            
            showAlert("Success", "✅ User status updated to: " + newStatus);
            loadUsers(); // Refresh the list
        }
    }
    
    @FXML
    private void handleAddUser() {
        showAlert("Add New User", 
            "➕ Add New User functionality\n\n" +
            "In a full implementation, this would open a user registration form\n" +
            "with options to create Customer, Venue Owner, or Admin accounts."
        );
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}