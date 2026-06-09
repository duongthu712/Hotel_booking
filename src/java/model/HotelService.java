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
    private String serviceName;
    private int hotelId;
    private String description;
    private BigDecimal unitPrice;
    private String imageUrl; // Trường ảnh mới lưu link ảnh dịch vụ
    private boolean isActive;

    public HotelService() {
    }

    public HotelService(int hotelServiceId, String serviceName, int hotelId, String description, BigDecimal unitPrice, String imageUrl, boolean isActive) {
        this.hotelServiceId = hotelServiceId;
        this.serviceName = serviceName;
        this.hotelId = hotelId;
        this.description = description;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
    }

    public HotelService(String serviceName, int hotelId, String description, BigDecimal unitPrice, String imageUrl, boolean isActive) {
        this.serviceName = serviceName;
        this.hotelId = hotelId;
        this.description = description;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
    }

    // Getter & Setter
    public int getHotelServiceId() {
        return hotelServiceId;
    }

    public void setHotelServiceId(int hotelServiceId) {
        this.hotelServiceId = hotelServiceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
