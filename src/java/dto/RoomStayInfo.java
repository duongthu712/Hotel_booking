/**
 * Author: ThuDNM-HE204370
 * Date created: 16/06/2026
 * Purpose: Used to display a detailed list of guests staying in a specific room (used in the Check-in/Checkout management screen).
 */
package dto;

import java.util.Date;

public class RoomStayInfo {

    private int roomId;
    private String roomNumber;
    private String fullName;
    private String phone;
    private Date checkoutDate;

    public RoomStayInfo() {
    }

    public RoomStayInfo(int roomId, String roomNumber, String fullName, String phone, Date checkoutDate) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.fullName = fullName;
        this.phone = phone;
        this.checkoutDate = checkoutDate;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(Date checkoutDate) {
        this.checkoutDate = checkoutDate;
    }
}