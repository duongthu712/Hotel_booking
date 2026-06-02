package model;

public class HotelPolicy {
    private int policyId;
    private String policyName;
    private String description;
    private String policyType;
    private boolean active;

    //Constructor
    public HotelPolicy() {
    }

    public HotelPolicy(int policyId, String policyName, String description, 
            String policyType, boolean active) {
        this.policyId = policyId;
        this.policyName = policyName;
        this.description = description;
        this.policyType = policyType;
        this.active = active;
    }
    
    //Getter & Setter
    public int getPolicyId() {
        return policyId;
    }

    public void setPolicyId(int policyId) {
        this.policyId = policyId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }  
}
