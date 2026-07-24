package model;

public class RoomTypeService {

    private int roomTypeServiceId;
    private int roomTypeId;
    private int serviceId;
    private int quantity;
    private int isFree; 
    
    private RoomService roomService; 

    private RoomService service;

    // Constructor
    public RoomTypeService() {
    }

    public RoomTypeService(int roomTypeServiceId, int roomTypeId, int serviceId, int quantity, int isFree, RoomService roomService) {
        this.roomTypeServiceId = roomTypeServiceId;
        this.roomTypeId = roomTypeId;
        this.serviceId = serviceId;
        this.quantity = quantity;
        this.isFree = isFree;
        this.roomService = roomService;
    }

    public RoomTypeService(int roomTypeId, int serviceId, int quantity, int isFree) {
        this.roomTypeId = roomTypeId;
        this.serviceId = serviceId;
        this.quantity = quantity;
        this.isFree = isFree;
    }

    // Getter & Setter mới
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

    public int getIsFree() {
        return isFree;
    }

    public void setIsFree(int isFree) {
        this.isFree = isFree;
    }

    public RoomService getRoomService() {
        return roomService;
    }

    public void setRoomService(RoomService roomService) {
        this.roomService = roomService;
    }
}