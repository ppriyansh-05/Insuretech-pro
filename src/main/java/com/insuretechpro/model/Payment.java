package com.insuretechpro.model;

import java.time.LocalDate;

/**
 * Payment represents premium money paid by a customer for a policy.
 * This class will later map to the payments table in MySQL.
 */
public class Payment {
    // Private fields protect payment information.
    private long paymentId;
    private String paymentReference;
    private long policyId;
    private long customerId;
    private LocalDate paymentDate;
    private double amount;
    private String paymentMethod;
    private String status;

    public Payment(long paymentId, String paymentReference, long policyId, long customerId,
                   LocalDate paymentDate, double amount, String paymentMethod, String status) {
        // Constructor stores one payment transaction.
        this.paymentId = paymentId;
        this.paymentReference = paymentReference;
        this.policyId = policyId;
        this.customerId = customerId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    // Getters and setters give controlled access to private fields.
    public long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(long paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(long policyId) {
        this.policyId = policyId;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void markAsPaid() {
        // This method updates payment status after successful payment.
        this.status = "PAID";
    }

    public void markAsFailed() {
        // This method updates payment status after failed payment.
        this.status = "FAILED";
    }

    public void displayPaymentDetails() {
        // This method prints payment details in readable format.
        System.out.println("Payment Details");
        System.out.println("Reference: " + paymentReference);
        System.out.println("Policy ID: " + policyId);
        System.out.println("Customer ID: " + customerId);
        System.out.println("Payment Date: " + paymentDate);
        System.out.printf("Amount: %.2f%n", amount);
        System.out.println("Method: " + paymentMethod);
        System.out.println("Status: " + status);
    }
}
