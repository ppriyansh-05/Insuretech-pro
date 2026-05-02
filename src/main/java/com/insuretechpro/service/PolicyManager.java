package com.insuretechpro.service;

import com.insuretechpro.exception.PolicyNotFoundException;
import com.insuretechpro.model.Policy;

import java.util.ArrayList;
import java.util.List;

/**
 * PolicyManager contains business logic related to policies.
 * Later, this class can call JDBC repository methods instead of using ArrayList.
 */
public class PolicyManager {
    // ArrayList acts like temporary storage until MySQL is connected.
    private final List<Policy> policies = new ArrayList<>();

    public void addPolicy(Policy policy) {
        // Premium is calculated using the child's overridden method.
        policy.setPremiumAmount(policy.calculatePremium());
        policies.add(policy);
    }

    public Policy findPolicyById(long policyId) throws PolicyNotFoundException {
        // Search each policy and return the matching policy ID.
        for (Policy policy : policies) {
            if (policy.getPolicyId() == policyId) {
                return policy;
            }
        }

        throw new PolicyNotFoundException("Policy not found with ID: " + policyId);
    }

    public void displayAllPolicies() {
        // Polymorphism: each object calls its own displayPolicyDetails method.
        for (Policy policy : policies) {
            policy.displayPolicyDetails();
            System.out.println("--------------------------------");
        }
    }

    public List<Policy> getAllPolicies() {
        // Returning a copy protects the original list from outside changes.
        return new ArrayList<>(policies);
    }
}
