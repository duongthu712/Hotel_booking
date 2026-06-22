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
public class RoomAmenityDamage {
    private int damageId;
    private int bookingId;
    private int amenityId;
    private int quantityDamaged;
    private BigDecimal totalPrice;
    private LocalDateTime addedAt;

    public RoomAmenityDamage() {
    }

    public RoomAmenityDamage(int damageId, int bookingId, int amenityId, int quantityDamaged, BigDecimal totalPrice, LocalDateTime addedAt) {
        this.damageId = damageId;
        this.bookingId = bookingId;
        this.amenityId = amenityId;
        this.quantityDamaged = quantityDamaged;
        this.totalPrice = totalPrice;
        this.addedAt = addedAt;
    }

    public int getDamageId() {
        return damageId;
    }

    public void setDamageId(int damageId) {
        this.damageId = damageId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(int amenityId) {
        this.amenityId = amenityId;
    }

    public int getQuantityDamaged() {
        return quantityDamaged;
    }

    public void setQuantityDamaged(int quantityDamaged) {
        this.quantityDamaged = quantityDamaged;
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
