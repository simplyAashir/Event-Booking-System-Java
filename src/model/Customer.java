package model;

public class Customer extends User {
    private String customerId;
    private String preferences;

    public Customer(String userId, String fullName, String email, String phoneNo, String password) {
        super(userId, fullName, email, phoneNo, password);
        this.customerId = userId;
        this.preferences = "";
    }

    @Override
    public void logout() {
        System.out.println("Customer " + fullName + " logged out");
    }

    @Override
    public void updateProfile() {
        System.out.println("Customer profile updated");
    }

    @Override
    public void resetPassword() {
        System.out.println("Customer password reset");
    }

    // Customer-specific methods
    public String getCustomerId() { return customerId; }
    public String getPreferences() { return preferences; }
    public void setPreferences(String preferences) { this.preferences = preferences; }
}