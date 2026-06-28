package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Last update 17:50 27/06/2026
 *
 * @author LinhLTHE200306
 */
public class StaffAccount {

    private int staffId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private String resetCode;
    private LocalDateTime resetExpiry;
    private boolean resetUsed;

    public StaffAccount() {
    }

    public StaffAccount(int staffId, String username, String passwordHash, String fullName, String email, String phone, String role, boolean active, LocalDateTime createdAt, LocalDateTime deletedAt, String resetCode, LocalDateTime resetExpiry, boolean resetUsed) {
        this.staffId = staffId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.active = active;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.resetCode = resetCode;
        this.resetExpiry = resetExpiry;
        this.resetUsed = resetUsed;
    }

    // Getter & Setter
    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public String getResetCode() { return resetCode; }
    public void setResetCode(String resetCode) { this.resetCode = resetCode; }

    public LocalDateTime getResetExpiry() { return resetExpiry; }
    public void setResetExpiry(LocalDateTime resetExpiry) { this.resetExpiry = resetExpiry; }

    public boolean isResetUsed() { return resetUsed; }
    public void setResetUsed(boolean resetUsed) { this.resetUsed = resetUsed; }

    // Phương thức hỗ trợ hiển thị Role tiếng Anh cho hệ thống
    public String getRoleEn() {
        if (role == null) return "";
        switch (role) {
            case "Quản trị viên": return "ADMIN";
            case "Quản lý": return "MANAGER";
            case "Lễ tân": return "RECEPTIONIST";
            default: return role;
        }
    }
    
    public String getCreatedAtFormatted() {
    if (createdAt == null) {
        return "";
    }
    return createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
}
}