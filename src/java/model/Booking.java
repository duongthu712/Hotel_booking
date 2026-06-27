package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime; // Thêm import này

/**
 * Last update 15:40 27/06/2026
 *
 * @author LinhLTHE200306
 */
public class Booking {

    private int bookingId;
    private String bookingCode;
    private int guestId;
    private Integer staffId;
    private int roomTypeId;
    private int numRooms;
    private BigDecimal bookedPricePerNight;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private int numGuests;
    private int numChildren;
    private LocalTime expectedCheckInTime;
    private LocalDateTime autoCancelDeadline;
    private String status;
    private String paymentStatus;
    private BigDecimal depositAmount;
    private String source;
    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private LocalDateTime actualCheckinTime;
    private LocalDateTime actualCheckoutTime;
    private LocalDateTime createAt;

    // Constructor
    public Booking() {
    }

    // Constructor đầy đủ (đã cập nhật)
    public Booking(int bookingId, String bookingCode, int guestId, Integer staffId, int roomTypeId, int numRooms,
            BigDecimal bookedPricePerNight, LocalDate checkinDate, LocalDate checkoutDate, int numGuests,
            int numChildren, LocalTime expectedCheckInTime, LocalDateTime autoCancelDeadline,
            String status, String paymentStatus, BigDecimal depositAmount, String source,
            LocalDateTime confirmedAt, LocalDateTime cancelledAt, String cancellationReason,
            LocalDateTime actualCheckinTime, LocalDateTime actualCheckoutTime, LocalDateTime createAt) {
        this.bookingId = bookingId;
        this.bookingCode = bookingCode;
        this.guestId = guestId;
        this.staffId = staffId;
        this.roomTypeId = roomTypeId;
        this.numRooms = numRooms;
        this.bookedPricePerNight = bookedPricePerNight;
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
        this.numGuests = numGuests;
        this.numChildren = numChildren;
        this.expectedCheckInTime = expectedCheckInTime;
        this.autoCancelDeadline = autoCancelDeadline;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.depositAmount = depositAmount;
        this.source = source;
        this.confirmedAt = confirmedAt;
        this.cancelledAt = cancelledAt;
        this.cancellationReason = cancellationReason;
        this.actualCheckinTime = actualCheckinTime;
        this.actualCheckoutTime = actualCheckoutTime;
        this.createAt = createAt;
    }

    // --- CÁC GETTER & SETTER MỚI ---
    public int getNumChildren() {
        return numChildren;
    }

    public void setNumChildren(int numChildren) {
        this.numChildren = numChildren;
    }

    public LocalTime getExpectedCheckInTime() {
        return expectedCheckInTime;
    }

    public void setExpectedCheckInTime(LocalTime expectedCheckInTime) {
        this.expectedCheckInTime = expectedCheckInTime;
    }

    public LocalDateTime getAutoCancelDeadline() {
        return autoCancelDeadline;
    }

    public void setAutoCancelDeadline(LocalDateTime autoCancelDeadline) {
        this.autoCancelDeadline = autoCancelDeadline;
    }

    // --- CÁC GETTER & SETTER CŨ (giữ nguyên) ---
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

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public int getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(int roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public int getNumRooms() {
        return numRooms;
    }

    public void setNumRooms(int numRooms) {
        this.numRooms = numRooms;
    }

    public BigDecimal getBookedPricePerNight() {
        return bookedPricePerNight;
    }

    public void setBookedPricePerNight(BigDecimal bookedPricePerNight) {
        this.bookedPricePerNight = bookedPricePerNight;
    }

    public LocalDate getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(LocalDate checkinDate) {
        this.checkinDate = checkinDate;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(LocalDate checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public int getNumGuests() {
        return numGuests;
    }

    public void setNumGuests(int numGuests) {
        this.numGuests = numGuests;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public LocalDateTime getActualCheckinTime() {
        return actualCheckinTime;
    }

    public void setActualCheckinTime(LocalDateTime actualCheckinTime) {
        this.actualCheckinTime = actualCheckinTime;
    }

    public LocalDateTime getActualCheckoutTime() {
        return actualCheckoutTime;
    }

    public void setActualCheckoutTime(LocalDateTime actualCheckoutTime) {
        this.actualCheckoutTime = actualCheckoutTime;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}
