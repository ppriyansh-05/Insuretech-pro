package com.insuretechpro.model;

import java.time.LocalDate;

/**
 * Policy is an abstract class because a general policy is only a concept.
 * In real life, customers buy specific policies like life, health, or vehicle.
 */
public abstract class Policy {
    // Private fields show encapsulation: data is hidden inside the class.
    private long policyId;
    private String policyNumber;
    private long customerId;
    private long agentId;
    private String policyName;
    private double premiumAmount;
    private double coverageAmount;
    private String paymentFrequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    public Policy(long policyId, String policyNumber, long customerId, long agentId,
                  String policyName, double premiumAmount, double coverageAmount,
                  String paymentFrequency, LocalDate startDate, LocalDate endDate,
                  String status) {
        // Constructor initializes the common details of every policy.
        this.policyId = policyId;
        this.policyNumber = policyNumber;
        this.customerId = customerId;
        this.agentId = agentId;
        this.policyName = policyName;
        this.premiumAmount = premiumAmount;
        this.coverageAmount = coverageAmount;
        this.paymentFrequency = paymentFrequency;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Getters and setters give controlled access to private fields.
    public long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(long policyId) {
        this.policyId = policyId;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public double getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(double premiumAmount) {
        this.premiumAmount = premiumAmount;
    }

    public double getCoverageAmount() {
        return coverageAmount;
    }

    public void setCoverageAmount(double coverageAmount) {
        this.coverageAmount = coverageAmount;
    }

    public String getPaymentFrequency() {
        return paymentFrequency;
    }

    public void setPaymentFrequency(String paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /*
     * Each child policy calculates premium in its own way.
     * This method forces every subclass to provide its own implementation.
     */
    public abstract double calculatePremium();

    /*
     * Each child policy displays common details plus its own special details.
     */
    public abstract void displayPolicyDetails();
}
