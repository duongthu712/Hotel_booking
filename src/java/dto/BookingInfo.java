package dto;

import java.util.Date;

public class BookingInfo {

    private String bookingCode;
    private String fullName;
    private String phone;
    private Date checkinDate;

    public BookingInfo(String bookingCode, String fullName, String phone, Date checkinDate) {
        this.bookingCode = bookingCode;
        this.fullName = fullName;
        this.phone = phone;
        this.checkinDate = checkinDate;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCheckinDate(Date checkinDate) {
        this.checkinDate = checkinDate;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public Date getCheckinDate() {
        return checkinDate;
    }
}
