package model;

import java.util.Date;

public abstract class User {
    protected String userId;
    protected String fullName;
    protected String email;
    protected String phoneNo;
    protected String password;
    protected Date registrationDate;
    protected boolean isActive;

    // Constructor
    public User(String userId, String fullName, String email, String phoneNo, String password) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.password = password;
        this.registrationDate = new Date();
        this.isActive = true;
    }

    // Abstract methods
    public abstract void logout();
    public abstract void updateProfile();
    public abstract void resetPassword();

    // Getters and Setters
    public String getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Date getRegistrationDate() { return registrationDate; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}