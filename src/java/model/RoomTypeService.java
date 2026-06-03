package model;

public class RoomTypeService {

    private int roomTypeServiceId;
    private int roomTypeId;
    private int serviceId;
    private int quantity;

    //Constructor
    public RoomTypeService() {
    }

    public RoomTypeService(int roomTypeServiceId, int roomTypeId, int serviceId, int quantity) {
        this.roomTypeServiceId = roomTypeServiceId;
        this.roomTypeId = roomTypeId;
        this.serviceId = serviceId;
        this.quantity = quantity;
    }

    //Getter & Setter
    public int getRoomTypeServiceId() {
        return roomTypeServiceId;
    }

    public void setRoomTypeServiceId(int roomTypeServiceId) {
        this.roomTypeServiceId = roomTypeServiceId;
    }

    public int getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(int roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
