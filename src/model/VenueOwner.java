package model;

public class VenueOwner extends User {
    private String ownerId;
    private String taxId;

    public VenueOwner(String userId, String fullName, String email, String phoneNo, String password, String taxId) {
        super(userId, fullName, email, phoneNo, password);
        this.ownerId = userId;
        this.taxId = taxId;
    }

    @Override
    public void logout() {
        System.out.println("Venue Owner " + fullName + " logged out");
    }

    @Override
    public void updateProfile() {
        System.out.println("Venue Owner profile updated");
    }

    @Override
    public void resetPassword() {
        System.out.println("Venue Owner password reset");
    }

    // VenueOwner-specific methods
    public String getOwnerId() { return ownerId; }
    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }
}