package model;


import java.math.BigDecimal;

/**
 *
 * @author LinhLTHE200306
 */
public class RoomAmenity {
    private int amenityId;
    private String amenityName;
    private String description;
    private BigDecimal unitPrice;
    private boolean active;

    public RoomAmenity() {
    }

    public RoomAmenity(int amenityId, String amenityName, String description, BigDecimal unitPrice, boolean active) {
        this.amenityId = amenityId;
        this.amenityName = amenityName;
        this.description = description;
        this.unitPrice = unitPrice;
        this.active = active;
    }

    public int getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(int amenityId) {
        this.amenityId = amenityId;
    }

    public String getAmenityName() {
        return amenityName;
    }

    public void setAmenityName(String amenityName) {
        this.amenityName = amenityName;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    
}
