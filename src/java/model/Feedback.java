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
    private Integer guestId; //guestId can be null
    private int rating;
    private String comment;
    private LocalDateTime submittedAt;
    private String guestName;

    //Constructor
    public Feedback() {
    }

    public Feedback(int feedbackId, int bookingId, Integer guestId, int rating, String comment, LocalDateTime submittedAt, String guestName) {
        this.feedbackId = feedbackId;
        this.bookingId = bookingId;
        this.guestId = guestId;
        this.rating = rating;
        this.comment = comment;
        this.submittedAt = submittedAt;
        this.guestName = guestName;
    }

    //Getter & Setter
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

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
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

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

}
