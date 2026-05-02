package com.insuretechpro.model;

import java.time.LocalDate;

/**
 * HealthPolicy is a child class of Policy.
 * It stores health insurance details such as covered members and hospital plan.
 */
public class HealthPolicy extends Policy {
    // These fields are specific to health insurance only.
    private int coveredMembers;
    private String preExistingDiseases;
    private String networkHospitalPlan;
    private double roomRentLimit;

    public HealthPolicy(long policyId, String policyNumber, long customerId, long agentId,
                        String policyName, double premiumAmount, double coverageAmount,
                        String paymentFrequency, LocalDate startDate, LocalDate endDate, String status,
                        int coveredMembers, String preExistingDiseases,
                        String networkHospitalPlan, double roomRentLimit) {
        // super() reuses the constructor of the abstract parent class.
        super(policyId, policyNumber, customerId, agentId, policyName, premiumAmount,
                coverageAmount, paymentFrequency, startDate, endDate, status);
        this.coveredMembers = coveredMembers;
        this.preExistingDiseases = preExistingDiseases;
        this.networkHospitalPlan = networkHospitalPlan;
        this.roomRentLimit = roomRentLimit;
    }

    public int getCoveredMembers() {
        return coveredMembers;
    }

    public void setCoveredMembers(int coveredMembers) {
        this.coveredMembers = coveredMembers;
    }

    public String getPreExistingDiseases() {
        return preExistingDiseases;
    }

    public void setPreExistingDiseases(String preExistingDiseases) {
        this.preExistingDiseases = preExistingDiseases;
    }

    public String getNetworkHospitalPlan() {
        return networkHospitalPlan;
    }

    public void setNetworkHospitalPlan(String networkHospitalPlan) {
        this.networkHospitalPlan = networkHospitalPlan;
    }

    public double getRoomRentLimit() {
        return roomRentLimit;
    }

    public void setRoomRentLimit(double roomRentLimit) {
        this.roomRentLimit = roomRentLimit;
    }

    @Override
    public double calculatePremium() {
        // Health premium depends on coverage, members, and hospital plan.
        double basePremium = getCoverageAmount() * 0.015;
        double memberCharge = coveredMembers * 500;

        if ("PREMIUM".equalsIgnoreCase(networkHospitalPlan)) {
            basePremium = basePremium + 2000;
        }

        return basePremium + memberCharge;
    }

    @Override
    public void displayPolicyDetails() {
        // This method displays common policy details and health-specific details.
        System.out.println("Health Policy Details");
        System.out.println("Policy Number: " + getPolicyNumber());
        System.out.println("Policy Name: " + getPolicyName());
        System.out.println("Payment Frequency: " + getPaymentFrequency());
        System.out.println("Coverage Amount: " + getCoverageAmount());
        System.out.printf("Calculated Premium: %.2f%n", calculatePremium());
        System.out.println("Covered Members: " + coveredMembers);
        System.out.println("Hospital Plan: " + networkHospitalPlan);
        System.out.printf("Room Rent Limit: %.2f%n", roomRentLimit);
    }
}
