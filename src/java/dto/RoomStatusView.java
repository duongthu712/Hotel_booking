package dto;

public class RoomStatusView {
    // 1. Thông tin cơ bản của phòng (Từ bảng Rooms)
    private int roomNumber;
    private int floor;
    private String status; // 'Phòng trống', 'Phòng có khách', 'Đang dọn dẹp', 'Đang bảo trì'

    // 2. Thông tin hạng phòng (Từ bảng RoomTypes)
    private int roomTypeId;
    private String roomTypeName;

    // 3. Thông tin bổ trợ đơn đặt phòng hiện tại (Nếu phòng đang có khách ở - status = N'Phòng có khách')
    private Integer currentBookingId;   
    private String currentBookingCode;
    private String guestFullName;

    // --- CONSTRUCTORS ---
    public RoomStatusView() {
    }

    public RoomStatusView(int roomNumber, int floor, String status, int roomTypeId, String roomTypeName, 
                          Integer currentBookingId, String currentBookingCode, String guestFullName) {
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.status = status;
        this.roomTypeId = roomTypeId;
        this.roomTypeName = roomTypeName;
        this.currentBookingId = currentBookingId;
        this.currentBookingCode = currentBookingCode;
        this.guestFullName = guestFullName;
    }

    // --- GETTERS & SETTERS ---
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
}