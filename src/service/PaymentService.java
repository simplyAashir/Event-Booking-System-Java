package service;

import dao.PaymentDAO;
import model.Payment;
import java.util.List;

public class PaymentService {
    private PaymentDAO paymentDAO;
    
    public PaymentService() {
        this.paymentDAO = new PaymentDAO();
    }
    
    // Create new payment
    public Payment createPayment(String bookingId, double amount, String paymentMethod) {
        String transactionId = paymentDAO.generatePaymentId();
        Payment payment = new Payment(transactionId, bookingId, amount, paymentMethod);
        
        if (paymentDAO.createPayment(payment)) {
            return payment;
        }
        return null;
    }
    
    // Get payments by customer
    public List<Payment> getCustomerPayments(String customerId) {
        return paymentDAO.getPaymentsByCustomer(customerId);
    }
    
    // Get payments by booking
    public List<Payment> getBookingPayments(String bookingId) {
        return paymentDAO.getPaymentsByBooking(bookingId);
    }
    
    // Update payment status
    public boolean updatePaymentStatus(String transactionId, String status) {
        return paymentDAO.updatePaymentStatus(transactionId, status);
    }
    
    // Mark payment as completed
    public boolean completePayment(String transactionId, String reference) {
        Payment payment = paymentDAO.getPaymentById(transactionId);
        if (payment != null) {
            payment.setReference(reference);
            payment.setStatus("COMPLETED");
            return paymentDAO.updatePaymentStatus(transactionId, "COMPLETED");
        }
        return false;
    }
    
    // Get payment summary for customer
    public String getPaymentSummary(String customerId) {
        List<Payment> payments = getCustomerPayments(customerId);
        double totalPaid = payments.stream()
            .filter(p -> "COMPLETED".equals(p.getStatus()))
            .mapToDouble(Payment::getAmount)
            .sum();
        
        double pendingAmount = payments.stream()
            .filter(p -> "PENDING".equals(p.getStatus()))
            .mapToDouble(Payment::getAmount)
            .sum();
        
        return String.format("Total Paid: $%.2f | Pending: $%.2f", totalPaid, pendingAmount);
    }
}