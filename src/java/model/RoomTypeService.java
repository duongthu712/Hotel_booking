package model;

public class RoomTypeService {

    private int roomTypeServiceId;
    private int roomTypeId;
    private int serviceId;
    private int quantity;
<<<<<<< HEAD
    private boolean isFree;
=======
    private boolean isFree; 
   
    private Service service; 
>>>>>>> origin/ThuDNM

    // Constructor
    public RoomTypeService() {
    }

<<<<<<< HEAD
    public RoomTypeService(int roomTypeServiceId, int roomTypeId, int serviceId, int quantity, boolean isFree) {
=======
    public RoomTypeService(int roomTypeServiceId, int roomTypeId, int serviceId, int quantity, boolean isFree, Service service) {
>>>>>>> origin/ThuDNM
        this.roomTypeServiceId = roomTypeServiceId;
        this.roomTypeId = roomTypeId;
        this.serviceId = serviceId;
        this.quantity = quantity;
        this.isFree = isFree;
<<<<<<< HEAD
    }


    //Getter & Setter
=======
        this.service = service;
    }

    // Getter & Setter
>>>>>>> origin/ThuDNM
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

    public boolean isIsFree() {
        return isFree;
    }

    public void setIsFree(boolean isFree) {
        this.isFree = isFree;
    }
<<<<<<< HEAD
}
=======

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
>>>>>>> origin/ThuDNM
