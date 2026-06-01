package model;

import java.sql.Timestamp;

public class StaffAccount {
    private int staffId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String role; // 'Receptionist', 'Manager', 'Administrator'
    private boolean isActive;
    private Timestamp createdAt;

    public StaffAccount() {}

    public StaffAccount(int staffId, String username, String fullName, String email, String phone, String role, boolean isActive, Timestamp createdAt) {
        this.staffId = staffId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}