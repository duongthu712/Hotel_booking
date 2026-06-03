package model;

public class Room {

    private int roomNumber;
    private int floor;
    private String status;
    private int roomTypeId;

    //Constructor
    public Room() {
    }

    public Room(int roomNumber, int floor, String status, int roomTypeId) {
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.status = status;
        this.roomTypeId = roomTypeId;
    }

    //Getter & Setter
    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(int roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

}
