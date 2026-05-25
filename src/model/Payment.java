package model;

import java.util.Date;

public class Payment {
    private String transactionId;
    private String bookingId;
    private Date paymentDate;
    private double amount;
    private String paymentMethod;
    private String reference;
    private String status;
    private String paymentDetails;
    
    public Payment(String transactionId, String bookingId, double amount, String paymentMethod) {
        this.transactionId = transactionId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = new Date();
        this.status = "PENDING";
        this.reference = "";
        this.paymentDetails = "";
    }
    
    // Getters and setters
    public String getTransactionId() { return transactionId; }
    public String getBookingId() { return bookingId; }
    public Date getPaymentDate() { return paymentDate; }
    public double getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getReference() { return reference; }
    public String getStatus() { return status; }
    public String getPaymentDetails() { return paymentDetails; }
    
    public void setReference(String reference) { this.reference = reference; }
    public void setStatus(String status) { this.status = status; }
    public void setPaymentDetails(String paymentDetails) { this.paymentDetails = paymentDetails; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }
    
    // Business methods
    public String getFormattedStatus() {
        switch (status) {
            case "PENDING": return "⏳ Pending";
            case "COMPLETED": return "✅ Completed";
            case "FAILED": return "❌ Failed";
            case "REFUNDED": return "↩️ Refunded";
            default: return status;
        }
    }
    
    public String getFormattedAmount() {
        return String.format("$%.2f", amount);
    }
    
    public String getFormattedDate() {
        return paymentDate.toString();
    }
    
    public String getPaymentMethodIcon() {
        switch (paymentMethod.toUpperCase()) {
            case "CREDIT_CARD": return "💳";
            case "DEBIT_CARD": return "💳";
            case "BANK_TRANSFER": return "🏦";
            case "CASH": return "💵";
            default: return "💰";
        }
    }
}