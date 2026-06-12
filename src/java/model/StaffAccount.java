package model;

import java.sql.Timestamp;

public class StaffAccount {

    private int staffId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private boolean active;
    private Timestamp createdAt;
    private String resetCode;
    private Timestamp resetExpiry;
    private boolean resetUsed;

    public StaffAccount() {
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }

    public Timestamp getResetExpiry() {
        return resetExpiry;
    }

    public void setResetExpiry(Timestamp resetExpiry) {
        this.resetExpiry = resetExpiry;
    }

    public boolean isResetUsed() {
        return resetUsed;
    }

    public void setResetUsed(boolean resetUsed) {
        this.resetUsed = resetUsed;
    }

    public String getRoleEn() {
        if (role == null) {
            return "";
        }

        if (role.equalsIgnoreCase("Quản trị viên")) {
            return "ADMIN";
        }

        if (role.equalsIgnoreCase("Quản lý")) {
            return "MANAGER";
        }

        if (role.equalsIgnoreCase("Lễ tân")) {
            return "RECEPTIONIST";
        }

        return role;
    }
}