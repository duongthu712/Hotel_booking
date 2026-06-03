package model;

import java.math.BigDecimal;

public class Service {

    private int serviceId;
    private String serviceName;
    private String description;
    private BigDecimal unitPrice;
    private boolean active;
    private ServiceType type;

    //Constructor
    public Service() {
    }

    public Service(int serviceId, String serviceName, String description, BigDecimal unitPrice, boolean active, ServiceType type) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.description = description;
        this.unitPrice = unitPrice;
        this.active = active;
        this.type = type;
    }

    //Getter & Setter
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
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ServiceType getType() {
        return type;
    }

    public void setType(ServiceType type) {
        this.type = type;
    }

}
