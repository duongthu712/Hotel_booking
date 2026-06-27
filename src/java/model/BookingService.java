package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Last update 17:05 27/06/2026
 *
 * @author admin
 */
public class BookingService {

    private int bookingServiceId;
    private int bookingId;
    private int roomId; // CẬP NHẬT: Thay đổi từ roomNumber sang roomId
    private int roomTypeServiceId;
    private BigDecimal unitPrice;
    private int quantityUsed;
    private BigDecimal totalPrice;
    private LocalDateTime addedAt;

    public BookingService() {
    }

    public BookingService(int bookingServiceId, int bookingId, int roomId, int roomTypeServiceId, BigDecimal unitPrice, int quantityUsed, BigDecimal totalPrice, LocalDateTime addedAt) {
        this.bookingServiceId = bookingServiceId;
        this.bookingId = bookingId;
        this.roomId = roomId; // CẬP NHẬT
        this.roomTypeServiceId = roomTypeServiceId;
        this.unitPrice = unitPrice;
        this.quantityUsed = quantityUsed;
        this.totalPrice = totalPrice;
        this.addedAt = addedAt;
    }

    public int getBookingServiceId() {
        return bookingServiceId;
    }

    public void setBookingServiceId(int bookingServiceId) {
        this.bookingServiceId = bookingServiceId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getRoomId() {
        return roomId;
    } // CẬP NHẬT

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    } // CẬP NHẬT

    public int getRoomTypeServiceId() {
        return roomTypeServiceId;
    }

    public void setRoomTypeServiceId(int roomTypeServiceId) {
        this.roomTypeServiceId = roomTypeServiceId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantityUsed() {
        return quantityUsed;
    }

    public void setQuantityUsed(int quantityUsed) {
        this.quantityUsed = quantityUsed;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}
