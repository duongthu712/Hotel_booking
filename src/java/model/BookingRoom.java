package model;

import java.time.LocalDateTime;

/**
 * Last update 17:00 27/06/2026
 *
 * @author LinhLTHE200306
 */
public class BookingRoom {

    private int bookingRoomId;
    private int bookingId;
    private int roomId;
    private LocalDateTime assignedAt;
    private String checkoutStatus;
    private LocalDateTime checkoutAt;

    public BookingRoom() {
    }

    public BookingRoom(int bookingRoomId, int bookingId, int roomId, LocalDateTime assignedAt, String checkoutStatus, LocalDateTime checkoutAt) {
        this.bookingRoomId = bookingRoomId;
        this.bookingId = bookingId;
        this.roomId = roomId;
        this.assignedAt = assignedAt;
        this.checkoutStatus = checkoutStatus;
        this.checkoutAt = checkoutAt;
    }

    public int getBookingRoomId() {
        return bookingRoomId;
    }

    public void setBookingRoomId(int bookingRoomId) {
        this.bookingRoomId = bookingRoomId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getRoomId() {
        return roomId;
    } 

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    } 

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public String getCheckoutStatus() {
        return checkoutStatus;
    }

    public void setCheckoutStatus(String checkoutStatus) {
        this.checkoutStatus = checkoutStatus;
    }

    public LocalDateTime getCheckoutAt() {
        return checkoutAt;
    }

    public void setCheckoutAt(LocalDateTime checkoutAt) {
        this.checkoutAt = checkoutAt;
    }
}
