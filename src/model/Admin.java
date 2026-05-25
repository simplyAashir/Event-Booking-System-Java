package model;

public class Admin extends User {
    private String adminId;
    private String adminLevel;

    public Admin(String userId, String fullName, String email, String phoneNo, String password, String adminLevel) {
        super(userId, fullName, email, phoneNo, password);
        this.adminId = userId;
        this.adminLevel = adminLevel;
    }

    @Override
    public void logout() {
        System.out.println("Admin " + fullName + " logged out");
    }

    @Override
    public void updateProfile() {
        System.out.println("Admin profile updated");
    }

    @Override
    public void resetPassword() {
        System.out.println("Admin password reset");
    }

    // Admin-specific methods
    public String getAdminId() { return adminId; }
    public String getAdminLevel() { return adminLevel; }
    public void setAdminLevel(String adminLevel) { this.adminLevel = adminLevel; }
}