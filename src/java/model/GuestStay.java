package model;

public class GuestStay {

    private int stayId;
    private int bookingRoomId;
    private String fullName;
    private String phone;
    private String idNumber;

    //Constructor
    public GuestStay() {
    }

    public GuestStay(int stayId, int bookingRoomId, String fullName, String phone, String idNumber) {
        this.stayId = stayId;
        this.bookingRoomId = bookingRoomId;
        this.fullName = fullName;
        this.phone = phone;
        this.idNumber = idNumber;
    }

    //Getter & Setter
    public int getStayId() {
        return stayId;
    }

    public void setStayId(int stayId) {
        this.stayId = stayId;
    }

    public int getBookingRoomId() {
        return bookingRoomId;
    }

    public void setBookingRoomId(int bookingRoomId) {
        this.bookingRoomId = bookingRoomId;
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

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

}
