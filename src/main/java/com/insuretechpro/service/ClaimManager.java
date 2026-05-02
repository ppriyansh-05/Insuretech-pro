package com.insuretechpro.service;

import com.insuretechpro.exception.ClaimNotFoundException;
import com.insuretechpro.exception.InvalidClaimException;
import com.insuretechpro.exception.PolicyNotFoundException;
import com.insuretechpro.model.Claim;
import com.insuretechpro.model.Policy;

import java.util.ArrayList;
import java.util.List;

/**
 * ClaimManager contains business logic related to insurance claims.
 * It checks claim rules before storing or approving a claim.
 */
public class ClaimManager {
    // ArrayList acts like temporary claim storage before database integration.
    private final List<Claim> claims = new ArrayList<>();
    private final PolicyManager policyManager;

    public ClaimManager(PolicyManager policyManager) {
        // ClaimManager needs PolicyManager to validate policy details.
        this.policyManager = policyManager;
    }

    public void addClaim(Claim claim) throws PolicyNotFoundException, InvalidClaimException {
        // A claim must be connected to an existing policy.
        Policy policy = policyManager.findPolicyById(claim.getPolicyId());

        if (claim.getClaimAmount() <= 0) {
            throw new InvalidClaimException("Claim amount must be greater than zero.");
        }

        if (claim.getClaimAmount() > policy.getCoverageAmount()) {
            throw new InvalidClaimException("Claim amount cannot exceed policy coverage.");
        }

        claims.add(claim);
    }

    public Claim findClaimById(long claimId) throws ClaimNotFoundException {
        // Search each claim and return the matching claim ID.
        for (Claim claim : claims) {
            if (claim.getClaimId() == claimId) {
                return claim;
            }
        }

        throw new ClaimNotFoundException("Claim not found with ID: " + claimId);
    }

    public void approveClaim(long claimId, double approvedAmount, String remarks)
            throws ClaimNotFoundException, InvalidClaimException {
        Claim claim = findClaimById(claimId);

        if (approvedAmount <= 0 || approvedAmount > claim.getClaimAmount()) {
            throw new InvalidClaimException("Approved amount must be valid.");
        }

        claim.approveClaim(approvedAmount, remarks);
    }

    public void rejectClaim(long claimId, String remarks) throws ClaimNotFoundException {
        // Rejecting a claim updates its status and remarks.
        Claim claim = findClaimById(claimId);
        claim.rejectClaim(remarks);
    }

    public void displayAllClaims() {
        // Display every claim stored in the manager.
        for (Claim claim : claims) {
            claim.displayClaimDetails();
            System.out.println("--------------------------------");
        }
    }

    public List<Claim> getAllClaims() {
        // Returning a copy protects the original list from outside changes.
        return new ArrayList<>(claims);
    }
}
