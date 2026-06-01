/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;

/**
 *
 * @author Minh Thu
 */
public class HotelService {
    private int hotelServiceId;
    private int hotelId;
    private String serviceName;
    private String description;
    private BigDecimal unitPrice;
    private boolean isActive;

    public HotelService() {
    }

    public HotelService(int hotelServiceId, int hotelId, String serviceName, String description, BigDecimal unitPrice, boolean isActive) {
        this.hotelServiceId = hotelServiceId;
        this.hotelId = hotelId;
        this.serviceName = serviceName;
        this.description = description;
        this.unitPrice = unitPrice;
        this.isActive = isActive;
    }

    public int getHotelServiceId() { return hotelServiceId; }
    public void setHotelServiceId(int hotelServiceId) { this.hotelServiceId = hotelServiceId; }

    public int getHotelId() { return hotelId; }
    public void setHotelId(int hotelId) { this.hotelId = hotelId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public boolean isIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
}
