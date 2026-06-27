package model;

import java.time.LocalDateTime;

/**
 * Last update 16:30 27/06/2026
 *
 * @author Minh Thu
 */
public class EmailNotification {

    private int notificationId;
    private Integer bookingId;
    private String recipientEmail;
    private String subject;
    private String notificationType;
    private LocalDateTime sentAt; // Chuyển sang LocalDateTime
    private String status;

    public EmailNotification() {
    }

    public EmailNotification(int notificationId, Integer bookingId, String recipientEmail, String subject, String notificationType, LocalDateTime sentAt, String status) {
        this.notificationId = notificationId;
        this.bookingId = bookingId;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.notificationType = notificationType;
        this.sentAt = sentAt;
        this.status = status;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
