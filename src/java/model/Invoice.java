package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Invoice {

    private int invoiceId;
    private int bookingId;
    private BigDecimal roomCharges;
    private BigDecimal serviceCharges;
    private BigDecimal depositDeducted;
    private BigDecimal totalAmount;
    private BigDecimal remainingAmount;
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime paidAt;

    //Constructor
    public Invoice() {
    }

    public Invoice(int invoiceId, int bookingId, BigDecimal roomCharges, BigDecimal serviceCharges, BigDecimal depositDeducted, BigDecimal totalAmount, BigDecimal remainingAmount, String paymentStatus, String paymentMethod, LocalDateTime paidAt) {
        this.invoiceId = invoiceId;
        this.bookingId = bookingId;
        this.roomCharges = roomCharges;
        this.serviceCharges = serviceCharges;
        this.depositDeducted = depositDeducted;
        this.totalAmount = totalAmount;
        this.remainingAmount = remainingAmount;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.paidAt = paidAt;
    }

    //Getter & Setter
    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public BigDecimal getRoomCharges() {
        return roomCharges;
    }

    public void setRoomCharges(BigDecimal roomCharges) {
        this.roomCharges = roomCharges;
    }

    public BigDecimal getServiceCharges() {
        return serviceCharges;
    }

    public void setServiceCharges(BigDecimal serviceCharges) {
        this.serviceCharges = serviceCharges;
    }

    public BigDecimal getDepositDeducted() {
        return depositDeducted;
    }

    public void setDepositDeducted(BigDecimal depositDeducted) {
        this.depositDeducted = depositDeducted;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

}
