package com.insuretechpro.model;

/**
 * DashboardStats stores summary numbers shown on the dashboard.
 * It is a simple DTO, which means Data Transfer Object.
 */
public class DashboardStats {
    private long activePolicies;
    private long pendingClaims;
    private double monthlyRevenue;

    public DashboardStats(long activePolicies, long pendingClaims, double monthlyRevenue) {
        // These values come from SQL aggregate queries.
        this.activePolicies = activePolicies;
        this.pendingClaims = pendingClaims;
        this.monthlyRevenue = monthlyRevenue;
    }

    public long getActivePolicies() {
        return activePolicies;
    }

    public long getPendingClaims() {
        return pendingClaims;
    }

    public double getMonthlyRevenue() {
        return monthlyRevenue;
    }
}
