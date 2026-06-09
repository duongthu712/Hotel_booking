/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Minh Thu
 */
public class RoomTypeAmenity {
    private int roomTypeAmenityId;
    private int roomTypeId;
    private int amenityId;
    private int quantity;

    public RoomTypeAmenity() {
    }

    public RoomTypeAmenity(int roomTypeAmenityId, int roomTypeId, int amenityId, int quantity) {
        this.roomTypeAmenityId = roomTypeAmenityId;
        this.roomTypeId = roomTypeId;
        this.amenityId = amenityId;
        this.quantity = quantity;
    }

    public int getRoomTypeAmenityId() {
        return roomTypeAmenityId;
    }

    public void setRoomTypeAmenityId(int roomTypeAmenityId) {
        this.roomTypeAmenityId = roomTypeAmenityId;
    }

    public int getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(int roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public int getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(int amenityId) {
        this.amenityId = amenityId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
