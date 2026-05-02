package com.insuretechpro.model;

import java.time.LocalDate;

/**
 * Customer represents the person who buys insurance policies.
 * This class will later map to the customers table in MySQL.
 */
public class Customer {
    // Private fields protect customer data using encapsulation.
    private long customerId;
    private String customerCode;
    private long agentId;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String email;
    private String phone;
    private String addressLine;
    private String city;
    private String state;
    private String postalCode;
    private String status;

    public Customer(long customerId, String customerCode, long agentId, String fullName,
                    LocalDate dateOfBirth, String gender, String email, String phone,
                    String addressLine, String city, String state, String postalCode,
                    String status) {
        // Constructor stores all basic customer details in one object.
        this.customerId = customerId;
        this.customerCode = customerCode;
        this.agentId = agentId;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.addressLine = addressLine;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.status = status;
    }

    // Getters and setters allow controlled access to private fields.
    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void displayCustomerDetails() {
        // This method prints customer information in a readable format.
        System.out.println("Customer Details");
        System.out.println("Customer Code: " + customerCode);
        System.out.println("Full Name: " + fullName);
        System.out.println("Email: " + email);
        System.out.println("Phone: " + phone);
        System.out.println("City: " + city);
        System.out.println("Status: " + status);
    }
}
