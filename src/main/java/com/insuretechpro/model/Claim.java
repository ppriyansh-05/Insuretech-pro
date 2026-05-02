package com.insuretechpro.model;

import java.time.LocalDate;

/**
 * Claim represents a request made by a customer to get insurance money.
 * This class will later map to the claims table in MySQL.
 */
public class Claim {
    // Private fields protect claim data using encapsulation.
    private long claimId;
    private String claimNumber;
    private long policyId;
    private long customerId;
    private LocalDate claimDate;
    private double claimAmount;
    private double approvedAmount;
    private String claimReason;
    private String status;
    private String remarks;

    public Claim(long claimId, String claimNumber, long policyId, long customerId,
                 LocalDate claimDate, double claimAmount, double approvedAmount,
                 String claimReason, String status, String remarks) {
        // Constructor stores all claim details in one object.
        this.claimId = claimId;
        this.claimNumber = claimNumber;
        this.policyId = policyId;
        this.customerId = customerId;
        this.claimDate = claimDate;
        this.claimAmount = claimAmount;
        this.approvedAmount = approvedAmount;
        this.claimReason = claimReason;
        this.status = status;
        this.remarks = remarks;
    }

    // Getters and setters give controlled access to private fields.
    public long getClaimId() {
        return claimId;
    }

    public void setClaimId(long claimId) {
        this.claimId = claimId;
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
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

    public LocalDate getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(LocalDate claimDate) {
        this.claimDate = claimDate;
    }

    public double getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(double claimAmount) {
        this.claimAmount = claimAmount;
    }

    public double getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(double approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public String getClaimReason() {
        return claimReason;
    }

    public void setClaimReason(String claimReason) {
        this.claimReason = claimReason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void approveClaim(double approvedAmount, String remarks) {
        // This method changes claim status when the claim is approved.
        this.approvedAmount = approvedAmount;
        this.status = "APPROVED";
        this.remarks = remarks;
    }

    public void processClaim(String remarks) {
        // Agent uses this method when claim documents are under review.
        this.status = "PROCESSING";
        this.remarks = remarks;
    }

    public void rejectClaim(String remarks) {
        // This method changes claim status when the claim is rejected.
        this.approvedAmount = 0;
        this.status = "REJECTED";
        this.remarks = remarks;
    }

    public void cancelClaim(String remarks) {
        // Customer or admin uses this method to cancel a claim request.
        this.approvedAmount = 0;
        this.status = "CANCELLED";
        this.remarks = remarks;
    }

    public void settleClaim(String remarks) {
        // Admin uses this after approved claim amount is paid to the customer.
        this.status = "SETTLED";
        this.remarks = remarks;
    }

    public void displayClaimDetails() {
        // This method prints claim information in a readable format.
        System.out.println("Claim Details");
        System.out.println("Claim Number: " + claimNumber);
        System.out.println("Policy ID: " + policyId);
        System.out.println("Customer ID: " + customerId);
        System.out.println("Claim Date: " + claimDate);
        System.out.printf("Claim Amount: %.2f%n", claimAmount);
        System.out.printf("Approved Amount: %.2f%n", approvedAmount);
        System.out.println("Reason: " + claimReason);
        System.out.println("Status: " + status);
        System.out.println("Remarks: " + remarks);
    }
}
