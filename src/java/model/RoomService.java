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
public class RoomService {

    private int serviceId;
    private String serviceName;
    private String description;
    private BigDecimal unitPrice;
    private boolean isActive;

    public RoomService() {
    }

    public RoomService(int serviceId, String serviceName, String description, BigDecimal unitPrice, boolean isActive) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.description = description;
        this.unitPrice = unitPrice;
        this.isActive = isActive;
    }

    public RoomService(String serviceName, String description, BigDecimal unitPrice, boolean isActive) {
        this.serviceName = serviceName;
        this.description = description;
        this.unitPrice = unitPrice;
        this.isActive = isActive;
    }

    // Getter & Setter
    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
