package model;

import java.math.BigDecimal;

/**
 * Last update 17:05 27/06/2026
 *
 * @author Minh Thu
 */
public class HotelService {

    private int hotelServiceId;
    private String serviceName;
    private int hotelId;
    private String description;
    private BigDecimal unitPrice;
    private String imageUrl;
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

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    
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
