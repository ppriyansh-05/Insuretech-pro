package com.insuretechpro.model;

/**
 * PolicyDistribution stores policy type counts for the pie chart.
 */
public class PolicyDistribution {
    private String policyType;
    private long totalCount;

    public PolicyDistribution(String policyType, long totalCount) {
        // Example: LIFE = 10 policies, HEALTH = 5 policies.
        this.policyType = policyType;
        this.totalCount = totalCount;
    }

    public String getPolicyType() {
        return policyType;
    }

    public long getTotalCount() {
        return totalCount;
    }
}
