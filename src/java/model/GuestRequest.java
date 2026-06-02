package model;

import java.time.LocalDateTime;

public class GuestRequest {

    private int requestId;
    private int bookingId;
    private int guestId;
    private String requestType;
    private String requestDetails;
    private LocalDateTime requestedCheckin;
    private LocalDateTime requestedCheckout;
    private Integer targetRoomTypeId; //can be null
    private String status;
    private LocalDateTime processedAt;
    private String responseNotes;
    private LocalDateTime submittedAt;

    //Constructor
    public GuestRequest() {
    }

    public GuestRequest(int requestId, int bookingId, int guestId, String requestType, String requestDetails, LocalDateTime requestedCheckin, LocalDateTime requestedCheckout, Integer targetRoomTypeId, String status, LocalDateTime processedAt, String responseNotes, LocalDateTime submittedAt) {
        this.requestId = requestId;
        this.bookingId = bookingId;
        this.guestId = guestId;
        this.requestType = requestType;
        this.requestDetails = requestDetails;
        this.requestedCheckin = requestedCheckin;
        this.requestedCheckout = requestedCheckout;
        this.targetRoomTypeId = targetRoomTypeId;
        this.status = status;
        this.processedAt = processedAt;
        this.responseNotes = responseNotes;
        this.submittedAt = submittedAt;
    }

    //Getter & Setter
    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestDetails() {
        return requestDetails;
    }

    public void setRequestDetails(String requestDetails) {
        this.requestDetails = requestDetails;
    }

    public LocalDateTime getRequestedCheckin() {
        return requestedCheckin;
    }

    public void setRequestedCheckin(LocalDateTime requestedCheckin) {
        this.requestedCheckin = requestedCheckin;
    }

    public LocalDateTime getRequestedCheckout() {
        return requestedCheckout;
    }

    public void setRequestedCheckout(LocalDateTime requestedCheckout) {
        this.requestedCheckout = requestedCheckout;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getResponseNotes() {
        return responseNotes;
    }

    public void setResponseNotes(String responseNotes) {
        this.responseNotes = responseNotes;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public Integer getTargetRoomTypeId() {
        return targetRoomTypeId;
    }

    public void setTargetRoomTypeId(Integer targetRoomTypeId) {
        this.targetRoomTypeId = targetRoomTypeId;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

}
