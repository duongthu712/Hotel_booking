/**
 * Author: ThuDNM-HE204370
 * Date created: 12/06/2026
 * Purpose: Used to display the list and details of bookings for the receptionist (used in Check-in and Assign Room pages).
 */
package dto;

import java.math.BigDecimal;
import java.sql.Date;
public class BookingCheckInView {

    private int bookingId;
    private String bookingCode;
    private int numRooms;
    private int numGuests;       // Số người lớn thực tế của đoàn khách từ đơn đặt
    private int numChildren;
    private String paymentStatus;
    private BigDecimal depositAmount;
    private int guestId;
    private String guestFullName;
    private String guestPhone;
    private String guestEmail;
    private int roomTypeId; 
    private String roomTypeName;
    private int capacity;
    private int maxAdults;    
    private int maxChildren;  
    private String status;
    private String idNumber;
    private Date dateOfBirth;
    private String nationality;
    private String actualCheckInTime;
    private String requestType;
    private String requestDetails;
    private String requestStatus;
    private String requestedCheckIn;
    private int assignedRoomsCount;    // Số phòng đã được gán thực tế trong DB
    private String assignedRoomList;   // Chuỗi danh sách số phòng dạng "101, 102"
    private String expectedCheckInTime; // Giờ khách hẹn đến (Dạng HH:mm:ss)
    private String autoCancelDeadline;  // Hạn chót tự động hủy phòng nếu khách ko đến
    private String callNote;            // Ghi chú cuộc gọi của lễ tân (Lấy từ cancellation_reason)
    private String checkinDate;         // Ngày check-in gốc

    public BookingCheckInView() {
    }

    public String getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(String checkinDate) {
        this.checkinDate = checkinDate;
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

    public int getAssignedRoomsCount() {
        return assignedRoomsCount;
    }

    public void setAssignedRoomsCount(int assignedRoomsCount) {
        this.assignedRoomsCount = assignedRoomsCount;
    }

    public String getAssignedRoomList() {
        return assignedRoomList;
    }

    public void setAssignedRoomList(String assignedRoomList) {
        this.assignedRoomList = assignedRoomList;
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

    public int getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(int roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public int getNumRooms() {
        return numRooms;
    }

    public void setNumRooms(int numRooms) {
        this.numRooms = numRooms;
    }

    public int getNumGuests() {
        return numGuests;
    }

    public void setNumGuests(int numGuests) {
        this.numGuests = numGuests;
    }

    public int getNumChildren() {
        return numChildren;
    }

    public void setNumChildren(int numChildren) {
        this.numChildren = numChildren;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public String getGuestFullName() {
        return guestFullName;
    }

    public void setGuestFullName(String guestFullName) {
        this.guestFullName = guestFullName;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

    public void setGuestPhone(String guestPhone) {
        this.guestPhone = guestPhone;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public String getRoomTypeName() {
        return roomTypeName;
    }

    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getActualCheckInTime() {
        return actualCheckInTime;
    }

    public void setActualCheckInTime(String actualCheckInTime) {
        this.actualCheckInTime = actualCheckInTime;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestDetails() {
        return requestDetails;
    }

    public void setRequestDetails(String requestDetails) {
        this.requestDetails = requestDetails;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getRequestedCheckIn() {
        return requestedCheckIn;
    }

    public void setRequestedCheckIn(String requestedCheckIn) {
        this.requestedCheckIn = requestedCheckIn;
    }
}