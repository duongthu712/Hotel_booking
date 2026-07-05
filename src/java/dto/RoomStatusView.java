package dto;

public class RoomStatusView {

    private int roomId;
    private int roomNumber;
    private int floor;
    private String status;

    private int roomTypeId;
    private String roomTypeName;

    private Integer currentBookingId;
    private String currentBookingCode;
    private String guestFullName;
    private int capacity;
    private String guestPhone;      
    private String guestIdNumber;   

    // Thêm mới định mức người lớn và trẻ em
    private int maxAdults;
    private int maxChildren;

    public RoomStatusView() {
    }

    public RoomStatusView(int roomId, int roomNumber, int floor, String status, int roomTypeId, String roomTypeName,
            Integer currentBookingId, String currentBookingCode, String guestFullName, int capacity, 
            String guestPhone, String guestIdNumber, int maxAdults, int maxChildren) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.status = status;
        this.roomTypeId = roomTypeId;
        this.roomTypeName = roomTypeName;
        this.currentBookingId = currentBookingId;
        this.currentBookingCode = currentBookingCode;
        this.guestFullName = guestFullName;
        this.capacity = capacity;
        this.guestPhone = guestPhone;
        this.guestIdNumber = guestIdNumber;
        this.maxAdults = maxAdults;
        this.maxChildren = maxChildren;
    }

    public int getMaxAdults() {
        return maxAdults;
    }

    public void setMaxAdults(int maxAdults) {
        this.maxAdults = maxAdults;
    }

    public int getMaxChildren() {
        return maxChildren;
    }

    public void setMaxChildren(int maxChildren) {
        this.maxChildren = maxChildren;
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

    public String getRoomTypeName() {
        return roomTypeName;
    }

    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    public Integer getCurrentBookingId() {
        return currentBookingId;
    }

    public void setCurrentBookingId(Integer currentBookingId) {
        this.currentBookingId = currentBookingId;
    }

    public String getCurrentBookingCode() {
        return currentBookingCode;
    }

    public void setCurrentBookingCode(String currentBookingCode) {
        this.currentBookingCode = currentBookingCode;
    }

    public String getGuestFullName() {
        return guestFullName;
    }

    public void setGuestFullName(String guestFullName) {
        this.guestFullName = guestFullName;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

    public void setGuestPhone(String guestPhone) {
        this.guestPhone = guestPhone;
    }

    public String getGuestIdNumber() {
        return guestIdNumber;
    }

    public void setGuestIdNumber(String guestIdNumber) {
        this.guestIdNumber = guestIdNumber;
    }
}