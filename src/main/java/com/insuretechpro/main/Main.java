package com.insuretechpro.main;

import com.insuretechpro.exception.ClaimNotFoundException;
import com.insuretechpro.exception.InvalidClaimException;
import com.insuretechpro.exception.PolicyNotFoundException;
import com.insuretechpro.model.Agent;
import com.insuretechpro.model.Claim;
import com.insuretechpro.model.Customer;
import com.insuretechpro.model.HealthPolicy;
import com.insuretechpro.model.LifePolicy;
import com.insuretechpro.model.Payment;
import com.insuretechpro.model.Policy;
import com.insuretechpro.model.VehiclePolicy;
import com.insuretechpro.service.ClaimManager;
import com.insuretechpro.service.PolicyManager;

import java.time.LocalDate;

/**
 * Main class is the starting point of the program.
 * It demonstrates the complete OOP flow before adding database connectivity.
 */
public class Main {
    public static void main(String[] args) {
        // Manager objects contain business logic for policies and claims.
        PolicyManager policyManager = new PolicyManager();
        ClaimManager claimManager = new ClaimManager(policyManager);

        // Agent object represents the person managing the customer and policies.
        Agent agent = new Agent(
                501,
                "AGT-1001",
                "Neha Kapoor",
                "neha.kapoor@insuretech.com",
                "9000011111",
                LocalDate.of(2022, 6, 1),
                5.5,
                "ACTIVE"
        );

        // Customer object represents the policy owner.
        Customer customer = new Customer(
                101,
                "CUS-1001",
                501,
                "Aarav Mehta",
                LocalDate.of(1998, 5, 10),
                "MALE",
                "aarav.mehta@example.com",
                "9876543210",
                "MG Road",
                "Pune",
                "Maharashtra",
                "411001",
                "ACTIVE"
        );

        agent.displayAgentDetails();
        System.out.println("================================");
        customer.displayCustomerDetails();
        System.out.println("================================");

        // Parent class reference can store a LifePolicy object.
        Policy lifePolicy = new LifePolicy(
                1,
                "POL-LIFE-1001",
                customer.getCustomerId(),
                agent.getAgentId(),
                "Secure Life Plan",
                0,
                1000000,
                "YEARLY",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2046, 1, 1),
                "ACTIVE",
                "Rahul Sharma",
                "Brother",
                25,
                "No major illness",
                "LOW"
        );

        // Same parent class reference can store a HealthPolicy object.
        Policy healthPolicy = new HealthPolicy(
                2,
                "POL-HEALTH-1001",
                customer.getCustomerId(),
                agent.getAgentId(),
                "Family Health Plan",
                0,
                500000,
                "YEARLY",
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2027, 2, 1),
                "ACTIVE",
                4,
                "None",
                "PREMIUM",
                5000
        );

        // Same parent class reference can store a VehiclePolicy object.
        Policy vehiclePolicy = new VehiclePolicy(
                3,
                "POL-VEHICLE-1001",
                customer.getCustomerId(),
                agent.getAgentId(),
                "Car Protection Plan",
                0,
                800000,
                "YEARLY",
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2027, 3, 1),
                "ACTIVE",
                "MH12AB1234",
                "CAR",
                "Hyundai",
                "Creta",
                2024,
                "ENG123456",
                "CHS987654"
        );

        // Adding different child objects to one manager shows polymorphism.
        policyManager.addPolicy(lifePolicy);
        policyManager.addPolicy(healthPolicy);
        policyManager.addPolicy(vehiclePolicy);

        System.out.println("All Policies");
        System.out.println("================================");
        policyManager.displayAllPolicies();

        // Payment is connected to customer and policy using their IDs.
        Payment payment = new Payment(
                1,
                "PAY-1001",
                healthPolicy.getPolicyId(),
                customer.getCustomerId(),
                LocalDate.of(2026, 2, 5),
                healthPolicy.getPremiumAmount(),
                "UPI",
                "PENDING"
        );
        payment.markAsPaid();
        payment.displayPaymentDetails();
        System.out.println("================================");

        try {
            // Claim is connected to both customer and policy using their IDs.
            Claim claim = new Claim(
                    1,
                    "CLM-1001",
                    healthPolicy.getPolicyId(),
                    customer.getCustomerId(),
                    LocalDate.of(2026, 4, 15),
                    75000,
                    0,
                    "Hospital treatment expenses",
                    "REQUESTED",
                    "Claim submitted for review"
            );

            // ClaimManager validates the claim before adding or approving it.
            claimManager.addClaim(claim);
            claimManager.approveClaim(1, 70000, "Approved after document verification");

            System.out.println("All Claims");
            System.out.println("================================");
            claimManager.displayAllClaims();
        } catch (PolicyNotFoundException | ClaimNotFoundException | InvalidClaimException exception) {
            // Custom exceptions are handled here to avoid program crash.
            System.out.println("Error: " + exception.getMessage());
        }
    }
}
