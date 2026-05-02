package com.insuretechpro.model;

import java.time.LocalDate;

/**
 * Agent represents the employee who sells or manages insurance policies.
 * This class will later map to the agents table in MySQL.
 */
public class Agent {
    // Private fields keep agent data protected inside the class.
    private long agentId;
    private String agentCode;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate hireDate;
    private double commissionRate;
    private String status;

    public Agent(long agentId, String agentCode, String fullName, String email,
                 String phone, LocalDate hireDate, double commissionRate, String status) {
        // Constructor creates one complete agent object.
        this.agentId = agentId;
        this.agentCode = agentCode;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.hireDate = hireDate;
        this.commissionRate = commissionRate;
        this.status = status;
    }

    // Getters and setters support encapsulation.
    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    public String getAgentCode() {
        return agentCode;
    }

    public void setAgentCode(String agentCode) {
        this.agentCode = agentCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public double getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(double commissionRate) {
        this.commissionRate = commissionRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void displayAgentDetails() {
        // This method prints agent details for console output.
        System.out.println("Agent Details");
        System.out.println("Agent Code: " + agentCode);
        System.out.println("Full Name: " + fullName);
        System.out.println("Email: " + email);
        System.out.println("Phone: " + phone);
        System.out.printf("Commission Rate: %.2f%%%n", commissionRate);
        System.out.println("Status: " + status);
    }
}
