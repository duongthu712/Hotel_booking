package dto;

import java.util.Date;

// Nội dung hiển thị chi tiết khi search
public class BookingInfo {

    private String bookingCode;
    private String fullName;
    private String phone;
    private Date checkinDate;
    private String expectedCheckInTime; // Giờ khách hẹn đến (Dạng HH:mm:ss)
    private String autoCancelDeadline;  // Hạn chót tự động hủy phòng nếu khách ko đến
    private String callNote;            // Ghi chú nhanh nội dung cuộc gọi

    public BookingInfo(String bookingCode, String fullName, String phone, Date checkinDate) {
        this.bookingCode = bookingCode;
        this.fullName = fullName;
        this.phone = phone;
        this.checkinDate = checkinDate;
    }

    public BookingInfo(String bookingCode, String fullName, String phone, Date checkinDate, 
                       String expectedCheckInTime, String autoCancelDeadline, String callNote) {
        this.bookingCode = bookingCode;
        this.fullName = fullName;
        this.phone = phone;
        this.checkinDate = checkinDate;
        this.expectedCheckInTime = expectedCheckInTime;
        this.autoCancelDeadline = autoCancelDeadline;
        this.callNote = callNote;
    }

    public String getExpectedCheckInTime() {
        return expectedCheckInTime;
    }

    public void setExpectedCheckInTime(String expectedCheckInTime) {
        this.expectedCheckInTime = expectedCheckInTime;
    }

    public String getAutoCancelDeadline() {
        return autoCancelDeadline;
    }

    public void setAutoCancelDeadline(String autoCancelDeadline) {
        this.autoCancelDeadline = autoCancelDeadline;
    }

    public String getCallNote() {
        return callNote;
    }

    public void setCallNote(String callNote) {
        this.callNote = callNote;
    }

    // --- Các Getter & Setter cũ giữ nguyên ---

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