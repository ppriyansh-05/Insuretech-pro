package com.insuretechpro.model;

import java.time.LocalDate;

/**
 * RecentClaimView stores claim rows shown in the dashboard table.
 * It combines data from claims, customers, and policies.
 */
public class RecentClaimView {
    private long claimId;
    private String customerName;
    private String policyType;
    private String status;
    private LocalDate claimDate;

    public RecentClaimView(long claimId, String customerName, String policyType,
                           String status, LocalDate claimDate) {
        this.claimId = claimId;
        this.customerName = customerName;
        this.policyType = policyType;
        this.status = status;
        this.claimDate = claimDate;
    }

    public long getClaimId() {
        return claimId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPolicyType() {
        return policyType;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getClaimDate() {
        return claimDate;
    }
}
