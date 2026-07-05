/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.math.BigDecimal;

/**
 *
 * @author Minh Thu
 */
public class AvailableRoomTypeView {

private int roomTypeId;
    private String roomTypeName;
    private int maxAdults;
    private int maxChildren;
    private BigDecimal basePrice;
    private int availableRoomsCount;

    public AvailableRoomTypeView() {
    }

    public AvailableRoomTypeView(int roomTypeId, String roomTypeName, int maxAdults, int maxChildren, BigDecimal basePrice, int availableRoomsCount) {
        this.roomTypeId = roomTypeId;
        this.roomTypeName = roomTypeName;
        this.maxAdults = maxAdults;
        this.maxChildren = maxChildren;
        this.basePrice = basePrice;
        this.availableRoomsCount = availableRoomsCount;
    }

    public int getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(int roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getRoomTypeName() {
        return roomTypeName;
    }

    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    public int getMaxAdults() {
        return maxAdults;
    }

    public void setMaxAdults(int maxAdults) {
        this.maxAdults = maxAdults;
    }

    public int getMaxChildren() {
        return maxChildren;
    }

    public void setMaxChildren(int maxChildren) {
        this.maxChildren = maxChildren;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public int getAvailableRoomsCount() {
        return availableRoomsCount;
    }

    public void setAvailableRoomsCount(int availableRoomsCount) {
        this.availableRoomsCount = availableRoomsCount;
    }
}
