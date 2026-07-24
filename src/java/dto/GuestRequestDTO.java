/**
 * Author: ThuDNM-HE204370
 * Date created: 25/06/2026
 * Purpose: Used to display the list and details of guest requests (used in the Receptionist's Request Management page).
 */
package dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GuestRequestDTO {

    // 1. Thông tin từ bảng GuestRequests
    private int requestId;
    private int bookingId;
    private int guestId;
    private String requestType;
    private String requestDetails;
    private LocalDateTime requestedCheckin;
    private LocalDateTime requestedCheckout;
    private Integer targetRoomTypeId;
    private String status;
    private LocalDateTime processedAt;
    private String responseNotes;
    private LocalDateTime submittedAt;

    // 2. Thông tin mở rộng để hiển thị (từ JOIN các bảng khác)
    private String bookingCode;
    private String guestName;
    private String guestPhone;
    private String currentRoomTypeName;
    private String targetRoomTypeName;

    // 3. Thông tin bổ sung cho logic tính toán
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numRooms;
    private BigDecimal currentPrice;
    private BigDecimal targetPrice;
    private int totalNights;
    private String formattedTime;
    private String formattedDate;
    private Integer roomTypeId;

    public Integer getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Integer roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    // Constructor
    public GuestRequestDTO() {
    }

    // Getters & Setters
    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
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

    public Integer getTargetRoomTypeId() {
        return targetRoomTypeId;
    }

    public void setTargetRoomTypeId(Integer targetRoomTypeId) {
        this.targetRoomTypeId = targetRoomTypeId;
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

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

    public void setGuestPhone(String guestPhone) {
        this.guestPhone = guestPhone;
    }

    public String getCurrentRoomTypeName() {
        return currentRoomTypeName;
    }

    public void setCurrentRoomTypeName(String currentRoomTypeName) {
        this.currentRoomTypeName = currentRoomTypeName;
    }

    public String getTargetRoomTypeName() {
        return targetRoomTypeName;
    }

    public void setTargetRoomTypeName(String targetRoomTypeName) {
        this.targetRoomTypeName = targetRoomTypeName;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public int getNumRooms() {
        return numRooms;
    }

    public void setNumRooms(int numRooms) {
        this.numRooms = numRooms;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(BigDecimal targetPrice) {
        this.targetPrice = targetPrice;
    }

    public int getTotalNights() {
        return totalNights;
    }

    public void setTotalNights(int totalNights) {
        this.totalNights = totalNights;
    }
}
