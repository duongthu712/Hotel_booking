package model;

import java.time.LocalDateTime;

/**
 * Last update 23:07 02/06/2026
 *
 * @author LinhLTHE200306
 */
public class Feedback {

    private int feedbackId;
    private int bookingId;
    private Integer guestId;
    private int rating;
    private String comment;
    private LocalDateTime submittedAt;

    /*
     * Các cột quản lý trạng thái hiển thị trong bảng Feedback.
     */
    private boolean visible;
    private LocalDateTime hiddenAt;
    private String hiddenReason;

    /*
     * Các field lấy từ bảng Guests và Bookings bằng JOIN.
     * Không phải cột trong bảng Feedback.
     */
    private String guestName;
    private String guestEmail;
    private String bookingCode;

    public Feedback() {
    }

    public Feedback(
            int feedbackId,
            int bookingId,
            Integer guestId,
            int rating,
            String comment,
            LocalDateTime submittedAt,
            boolean visible,
            LocalDateTime hiddenAt,
            String hiddenReason,
            String guestName,
            String guestEmail,
            String bookingCode) {

        this.feedbackId = feedbackId;
        this.bookingId = bookingId;
        this.guestId = guestId;
        this.rating = rating;
        this.comment = comment;
        this.submittedAt = submittedAt;
        this.visible = visible;
        this.hiddenAt = hiddenAt;
        this.hiddenReason = hiddenReason;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.bookingCode = bookingCode;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getGuestId() {
        return guestId;
    }

    public void setGuestId(Integer guestId) {
        this.guestId = guestId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public LocalDateTime getHiddenAt() {
        return hiddenAt;
    }

    public void setHiddenAt(LocalDateTime hiddenAt) {
        this.hiddenAt = hiddenAt;
    }

    public String getHiddenReason() {
        return hiddenReason;
    }

    public void setHiddenReason(String hiddenReason) {
        this.hiddenReason = hiddenReason;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }
}