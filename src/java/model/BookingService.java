/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @author admin
 */
public class BookingService {
    private int bookingServiceId;
    private int bookingId;
    private int roomTypeServiceId;
    private BigDecimal unitPrice;
    private int quantityUsed;
    private BigDecimal totalPrice;
    private LocalDateTime addedAt;

    public BookingService() {
    }

    public BookingService(int bookingServiceId, int bookingId, int roomTypeServiceId, BigDecimal unitPrice, int quantityUsed, BigDecimal totalPrice, LocalDateTime addedAt) {
        this.bookingServiceId = bookingServiceId;
        this.bookingId = bookingId;
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
