package model;

/**
 * Last update 16:50 27/06/2026
 *
 * @author LinhLTHE200306
 */
public class Room {

    private int roomId;
    private int roomNumber;
    private int floor;
    private String status;
    private int roomTypeId;

    public Room() {
    }

    public Room(int roomId, int roomNumber, int floor, String status, int roomTypeId) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.status = status;
        this.roomTypeId = roomTypeId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

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
