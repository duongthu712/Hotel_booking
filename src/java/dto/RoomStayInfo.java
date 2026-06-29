package dto;

import java.util.Date;

public class RoomStayInfo {

    private String roomNumber;
    private String fullName;
    private String phone;
    private Date checkoutDate;

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCheckoutDate(Date checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public RoomStayInfo(String roomNumber, String fullName, String phone, Date checkoutDate) {
        this.roomNumber = roomNumber;
        this.fullName = fullName;
        this.phone = phone;
        this.checkoutDate = checkoutDate;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public Date getCheckoutDate() {
        return checkoutDate;
    }
}
