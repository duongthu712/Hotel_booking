package model;

import java.util.List;
import java.math.BigDecimal;
import java.util.ArrayList;

public class RoomType {

    private int roomTypeId;
    private String typeName;
    private String description;
    private int capacity;
    private String bedType;
    private int bedCount;
    private BigDecimal areaSqm;
    private BigDecimal basePrice;
    private boolean active;
    private int availableRooms;

    private List<String> imageUrl;
    private List<String> imageCaption;

    private List<RoomTypeService> roomTypeServices;
    private List<RoomAmenity> roomAmenities;
    private List<RoomTypeAmenity> roomTypeAmenitys;

    public RoomType() {
        imageUrl = new ArrayList<>();
        imageCaption = new ArrayList<>();
        roomTypeServices = new ArrayList<>();
        roomAmenities = new ArrayList<>();
    }

    public RoomType(int roomTypeId, String typeName, String description, int capacity, String bedType, int bedCount, BigDecimal areaSqm, BigDecimal basePrice, boolean active, List<String> imageUrl, List<String> imageCaption, List<RoomTypeService> roomTypeServices, List<RoomTypeAmenity> roomTypeAmenitys) {
        this.roomTypeId = roomTypeId;
        this.typeName = typeName;
        this.description = description;
        this.capacity = capacity;
        this.bedType = bedType;
        this.bedCount = bedCount;
        this.areaSqm = areaSqm;
        this.basePrice = basePrice;
        this.active = active;
        this.imageUrl = imageUrl;
        this.imageCaption = imageCaption;
        this.roomTypeServices = roomTypeServices;
        this.roomTypeAmenitys = roomTypeAmenitys;
    }

    public RoomType(int roomTypeId, String typeName, String description,
            int capacity, String bedType, int bedCount, BigDecimal areaSqm,
            BigDecimal basePrice, boolean active) {
        this();
        this.roomTypeId = roomTypeId;
        this.typeName = typeName;
        this.description = description;
        this.capacity = capacity;
        this.bedType = bedType;
        this.bedCount = bedCount;
        this.areaSqm = areaSqm;
        this.basePrice = basePrice;
        this.active = active;
    }

    public void addImage(String url, String caption) {
        if (imageUrl == null) {
            imageUrl = new ArrayList<>();
        }

        if (imageCaption == null) {
            imageCaption = new ArrayList<>();
        }

        imageUrl.add(url);
        imageCaption.add(caption);
    }

    public void clearImages() {
        if (imageUrl != null) {
            imageUrl.clear();
        }

        if (imageCaption != null) {
            imageCaption.clear();
        }
    }

    public List<RoomTypeAmenity> getRoomTypeAmenitys() {
        return roomTypeAmenitys;
    }

    public void setRoomTypeAmenitys(List<RoomTypeAmenity> roomTypeAmenitys) {
        this.roomTypeAmenitys = roomTypeAmenitys;
    }

    public int getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(int roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getBedType() {
        return bedType;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    public int getBedCount() {
        return bedCount;
    }

    public void setBedCount(int bedCount) {
        this.bedCount = bedCount;
    }

    public BigDecimal getAreaSqm() {
        return areaSqm;
    }

    public void setAreaSqm(BigDecimal areaSqm) {
        this.areaSqm = areaSqm;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getImageCaption() {
        return imageCaption;
    }

    public void setImageCaption(List<String> imageCaption) {
        this.imageCaption = imageCaption;
    }

    public List<RoomTypeService> getRoomTypeServices() {
        return roomTypeServices;
    }

    public void setRoomTypeServices(List<RoomTypeService> roomTypeServices) {
        this.roomTypeServices = roomTypeServices;
    }

    public List<RoomAmenity> getRoomAmenities() {
        return roomAmenities;
    }

    public void setRoomAmenities(List<RoomAmenity> roomAmenities) {
        this.roomAmenities = roomAmenities;
    }

    public int getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(int availableRooms) {
        this.availableRooms = availableRooms;
    }
    
}
