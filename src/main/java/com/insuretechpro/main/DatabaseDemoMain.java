package com.insuretechpro.main;

import com.insuretechpro.exception.RepositoryException;
import com.insuretechpro.model.Agent;
import com.insuretechpro.model.Claim;
import com.insuretechpro.model.Customer;
import com.insuretechpro.model.HealthPolicy;
import com.insuretechpro.model.Payment;
import com.insuretechpro.model.Policy;
import com.insuretechpro.repository.AgentRepository;
import com.insuretechpro.repository.ClaimRepository;
import com.insuretechpro.repository.CustomerRepository;
import com.insuretechpro.repository.PaymentRepository;
import com.insuretechpro.repository.PolicyRepository;

import java.time.LocalDate;

/**
 * DatabaseDemoMain proves that JDBC repositories can insert and read real MySQL data.
 * Run this after DatabaseInitializer creates the database tables.
 */
public class DatabaseDemoMain {
    public static void main(String[] args) {
        AgentRepository agentRepository = new AgentRepository();
        CustomerRepository customerRepository = new CustomerRepository();
        PolicyRepository policyRepository = new PolicyRepository();
        PaymentRepository paymentRepository = new PaymentRepository();
        ClaimRepository claimRepository = new ClaimRepository();

        // Unique suffix prevents duplicate key errors when this demo is run again.
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());

        try {
            Agent agent = new Agent(
                    0,
                    "AGT-" + uniqueSuffix,
                    "Neha Kapoor",
                    "agent" + uniqueSuffix + "@insuretech.com",
                    "9" + uniqueSuffix.substring(uniqueSuffix.length() - 9),
                    LocalDate.of(2022, 6, 1),
                    5.5,
                    "ACTIVE"
            );

            // Save agent first because customer has a foreign key to agent.
            agentRepository.saveAgent(agent);

            Customer customer = new Customer(
                    0,
                    "CUS-" + uniqueSuffix,
                    agent.getAgentId(),
                    "Aarav Mehta",
                    LocalDate.of(1998, 5, 10),
                    "MALE",
                    "customer" + uniqueSuffix + "@example.com",
                    "8" + uniqueSuffix.substring(uniqueSuffix.length() - 9),
                    "MG Road",
                    "Pune",
                    "Maharashtra",
                    "411001",
                    "ACTIVE"
            );

            // Save customer second because policy has a foreign key to customer.
            customerRepository.saveCustomer(customer);

            Policy policy = new HealthPolicy(
                    0,
                    "POL-HEALTH-" + uniqueSuffix,
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

            // Premium is calculated before saving because premium_amount is stored in MySQL.
            policy.setPremiumAmount(policy.calculatePremium());
            policyRepository.savePolicy(policy);

            Payment payment = new Payment(
                    0,
                    "PAY-" + uniqueSuffix,
                    policy.getPolicyId(),
                    customer.getCustomerId(),
                    LocalDate.of(2026, 2, 5),
                    policy.getPremiumAmount(),
                    "UPI",
                    "PAID"
            );
            paymentRepository.savePayment(payment);

            Claim claim = new Claim(
                    0,
                    "CLM-" + uniqueSuffix,
                    policy.getPolicyId(),
                    customer.getCustomerId(),
                    LocalDate.of(2026, 4, 15),
                    75000,
                    0,
                    "Hospital treatment expenses",
                    "REQUESTED",
                    "Claim submitted for review"
            );
            claimRepository.saveClaim(claim);

            // Approve claim in Java, then update the same claim in MySQL.
            claim.approveClaim(70000, "Approved after document verification");
            claimRepository.updateClaimStatus(claim);

            System.out.println("Database demo completed successfully.");
            System.out.println("Saved Agent ID: " + agent.getAgentId());
            System.out.println("Saved Customer ID: " + customer.getCustomerId());
            System.out.println("Saved Policy ID: " + policy.getPolicyId());
            System.out.println("Saved Payment ID: " + payment.getPaymentId());
            System.out.println("Saved Claim ID: " + claim.getClaimId());

            // Reading back proves ResultSet to object mapping works.
            Policy savedPolicy = policyRepository.findPolicyById(policy.getPolicyId());
            savedPolicy.displayPolicyDetails();
        } catch (RepositoryException exception) {
            System.out.println("Database demo failed: " + exception.getMessage());
        }
    }
}
