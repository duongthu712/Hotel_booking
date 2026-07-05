package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Last update 8:30 30/06/2026
 *
 * @author LinhLTHE200306
 */
public class InvoicePayment {

    private int paymentId;
    private int invoiceId;
    private BigDecimal amount;
    private String paymentMethod;
    private LocalDateTime paidAt;
    private int collectedBy;
    private String note;

    public InvoicePayment() {
    }

    public InvoicePayment(int paymentId, int invoiceId, BigDecimal amount, String paymentMethod, LocalDateTime paidAt, int collectedBy, String note) {
        this.paymentId = paymentId;
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paidAt = paidAt;
        this.collectedBy = collectedBy;
        this.note = note;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public int getCollectedBy() {
        return collectedBy;
    }

    public void setCollectedBy(int collectedBy) {
        this.collectedBy = collectedBy;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
