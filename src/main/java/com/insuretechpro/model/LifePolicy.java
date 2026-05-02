package com.insuretechpro.model;

import java.time.LocalDate;

/**
 * LifePolicy is a child class of Policy.
 * It stores extra details that are only required for life insurance.
 */
public class LifePolicy extends Policy {
    // These fields are specific to life insurance only.
    private String nomineeName;
    private String nomineeRelation;
    private int nomineeAge;
    private String medicalHistory;
    private String riskCategory;

    public LifePolicy(long policyId, String policyNumber, long customerId, long agentId,
                      String policyName, double premiumAmount, double coverageAmount,
                      String paymentFrequency, LocalDate startDate, LocalDate endDate, String status,
                      String nomineeName, String nomineeRelation, int nomineeAge,
                      String medicalHistory, String riskCategory) {
        // super() sends common policy details to the parent Policy class.
        super(policyId, policyNumber, customerId, agentId, policyName, premiumAmount,
                coverageAmount, paymentFrequency, startDate, endDate, status);
        this.nomineeName = nomineeName;
        this.nomineeRelation = nomineeRelation;
        this.nomineeAge = nomineeAge;
        this.medicalHistory = medicalHistory;
        this.riskCategory = riskCategory;
    }

    public String getNomineeName() {
        return nomineeName;
    }

    public void setNomineeName(String nomineeName) {
        this.nomineeName = nomineeName;
    }

    public String getNomineeRelation() {
        return nomineeRelation;
    }

    public void setNomineeRelation(String nomineeRelation) {
        this.nomineeRelation = nomineeRelation;
    }

    public int getNomineeAge() {
        return nomineeAge;
    }

    public void setNomineeAge(int nomineeAge) {
        this.nomineeAge = nomineeAge;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getRiskCategory() {
        return riskCategory;
    }

    public void setRiskCategory(String riskCategory) {
        this.riskCategory = riskCategory;
    }

    @Override
    public double calculatePremium() {
        // Life policy premium changes based on customer risk category.
        double rate = 0.02;

        if ("HIGH".equalsIgnoreCase(riskCategory)) {
            rate = 0.03;
        } else if ("MEDIUM".equalsIgnoreCase(riskCategory)) {
            rate = 0.025;
        }

        return getCoverageAmount() * rate;
    }

    @Override
    public void displayPolicyDetails() {
        // This method displays common policy details and life-specific details.
        System.out.println("Life Policy Details");
        System.out.println("Policy Number: " + getPolicyNumber());
        System.out.println("Policy Name: " + getPolicyName());
        System.out.println("Payment Frequency: " + getPaymentFrequency());
        System.out.println("Coverage Amount: " + getCoverageAmount());
        System.out.printf("Calculated Premium: %.2f%n", calculatePremium());
        System.out.println("Nominee Name: " + nomineeName);
        System.out.println("Nominee Relation: " + nomineeRelation);
        System.out.println("Risk Category: " + riskCategory);
    }
}
