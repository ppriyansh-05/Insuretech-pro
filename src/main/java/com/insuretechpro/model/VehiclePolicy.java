package com.insuretechpro.model;

import java.time.LocalDate;

/**
 * VehiclePolicy is a child class of Policy.
 * It stores vehicle-specific details required for vehicle insurance.
 */
public class VehiclePolicy extends Policy {
    // These fields are specific to vehicle insurance only.
    private String vehicleRegistrationNumber;
    private String vehicleType;
    private String manufacturer;
    private String model;
    private int manufactureYear;
    private String engineNumber;
    private String chassisNumber;

    public VehiclePolicy(long policyId, String policyNumber, long customerId, long agentId,
                         String policyName, double premiumAmount, double coverageAmount,
                         String paymentFrequency, LocalDate startDate, LocalDate endDate, String status,
                         String vehicleRegistrationNumber, String vehicleType,
                         String manufacturer, String model, int manufactureYear,
                         String engineNumber, String chassisNumber) {
        // super() stores shared policy data in the parent Policy class.
        super(policyId, policyNumber, customerId, agentId, policyName, premiumAmount,
                coverageAmount, paymentFrequency, startDate, endDate, status);
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
        this.vehicleType = vehicleType;
        this.manufacturer = manufacturer;
        this.model = model;
        this.manufactureYear = manufactureYear;
        this.engineNumber = engineNumber;
        this.chassisNumber = chassisNumber;
    }

    public String getVehicleRegistrationNumber() {
        return vehicleRegistrationNumber;
    }

    public void setVehicleRegistrationNumber(String vehicleRegistrationNumber) {
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getManufactureYear() {
        return manufactureYear;
    }

    public void setManufactureYear(int manufactureYear) {
        this.manufactureYear = manufactureYear;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    @Override
    public double calculatePremium() {
        // Vehicle premium changes based on the type of vehicle.
        double rate = 0.018;

        if ("BIKE".equalsIgnoreCase(vehicleType)) {
            rate = 0.012;
        } else if ("TRUCK".equalsIgnoreCase(vehicleType)) {
            rate = 0.025;
        }

        return getCoverageAmount() * rate;
    }

    @Override
    public void displayPolicyDetails() {
        // This method displays common policy details and vehicle-specific details.
        System.out.println("Vehicle Policy Details");
        System.out.println("Policy Number: " + getPolicyNumber());
        System.out.println("Policy Name: " + getPolicyName());
        System.out.println("Payment Frequency: " + getPaymentFrequency());
        System.out.println("Coverage Amount: " + getCoverageAmount());
        System.out.printf("Calculated Premium: %.2f%n", calculatePremium());
        System.out.println("Vehicle Number: " + vehicleRegistrationNumber);
        System.out.println("Vehicle Type: " + vehicleType);
        System.out.println("Vehicle Model: " + manufacturer + " " + model);
    }
}
