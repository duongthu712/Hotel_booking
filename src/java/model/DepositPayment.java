package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Last update 23:40 02/06/2026
 *
 * @author LinhLTHE200306
 */
public class DepositPayment {

    private int depositId;
    private int bookingId;
    private BigDecimal amount;
    private String paymentProofUrl;
    private LocalDateTime submittedAt;
    private String verificationStatus;
    private LocalDateTime verifiedAt;
    private String notes;
    private int verifiedBy;

    //Constructor
    public DepositPayment() {
    }

    public DepositPayment(int depositId, int bookingId, BigDecimal amount, String paymentProofUrl, LocalDateTime submittedAt, String verificationStatus, LocalDateTime verifiedAt, String notes, int verifiedBy) {
        this.depositId = depositId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentProofUrl = paymentProofUrl;
        this.submittedAt = submittedAt;
        this.verificationStatus = verificationStatus;
        this.verifiedAt = verifiedAt;
        this.notes = notes;
        this.verifiedBy = verifiedBy;
    }

    //Getter & Setter
    public int getDepositId() {
        return depositId;
    }

    public void setDepositId(int depositId) {
        this.depositId = depositId;
    }

    public int getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(int verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentProofUrl() {
        return paymentProofUrl;
    }

    public void setPaymentProofUrl(String paymentProofUrl) {
        this.paymentProofUrl = paymentProofUrl;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
