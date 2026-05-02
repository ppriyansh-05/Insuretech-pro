package com.insuretechpro.main;

import com.insuretechpro.exception.RepositoryException;
import com.insuretechpro.model.Agent;
import com.insuretechpro.model.Claim;
import com.insuretechpro.model.Customer;
import com.insuretechpro.model.HealthPolicy;
import com.insuretechpro.model.LifePolicy;
import com.insuretechpro.model.Payment;
import com.insuretechpro.model.Policy;
import com.insuretechpro.model.VehiclePolicy;
import com.insuretechpro.repository.AgentRepository;
import com.insuretechpro.repository.ClaimRepository;
import com.insuretechpro.repository.CustomerRepository;
import com.insuretechpro.repository.PaymentRepository;
import com.insuretechpro.repository.PolicyRepository;
import com.insuretechpro.util.InputUtil;

/**
 * DatabaseMenuMain is the JDBC-backed console menu.
 * It connects user input, model classes, repositories, and MySQL.
 */
public class DatabaseMenuMain {
    private final AgentRepository agentRepository = new AgentRepository();
    private final CustomerRepository customerRepository = new CustomerRepository();
    private final PolicyRepository policyRepository = new PolicyRepository();
    private final PaymentRepository paymentRepository = new PaymentRepository();
    private final ClaimRepository claimRepository = new ClaimRepository();

    public static void main(String[] args) {
        // Program starts from here and opens the menu.
        new DatabaseMenuMain().start();
    }

    private void start() {
        boolean running = true;

        while (running) {
            showMenu();
            int choice = InputUtil.readInt("Enter choice: ");

            try {
                switch (choice) {
                    case 1 -> addAgent();
                    case 2 -> addCustomer();
                    case 3 -> addLifePolicy();
                    case 4 -> addHealthPolicy();
                    case 5 -> addVehiclePolicy();
                    case 6 -> recordPayment();
                    case 7 -> submitClaim();
                    case 8 -> approveClaim();
                    case 9 -> findPolicy();
                    case 0 -> running = false;
                    default -> System.out.println("Invalid choice.");
                }
            } catch (RepositoryException exception) {
                // RepositoryException handles database-related errors.
                System.out.println("Error: " + exception.getMessage());
            }
        }

        System.out.println("Thank you for using InsureTech Pro.");
    }

    private void showMenu() {
        // Menu gives clear options to the user.
        System.out.println();
        System.out.println("===== InsureTech Pro Menu =====");
        System.out.println("1. Add Agent");
        System.out.println("2. Add Customer");
        System.out.println("3. Add Life Policy");
        System.out.println("4. Add Health Policy");
        System.out.println("5. Add Vehicle Policy");
        System.out.println("6. Record Payment");
        System.out.println("7. Submit Claim");
        System.out.println("8. Approve Claim");
        System.out.println("9. Find Policy By ID");
        System.out.println("0. Exit");
    }

    private void addAgent() throws RepositoryException {
        Agent agent = new Agent(
                0,
                InputUtil.readString("Agent code: "),
                InputUtil.readString("Full name: "),
                InputUtil.readString("Email: "),
                InputUtil.readString("Phone: "),
                InputUtil.readDate("Hire date (yyyy-MM-dd): "),
                InputUtil.readDouble("Commission rate: "),
                "ACTIVE"
        );

        // Repository saves the object into the agents table.
        agentRepository.saveAgent(agent);
        System.out.println("Agent saved with ID: " + agent.getAgentId());
    }

    private void addCustomer() throws RepositoryException {
        Customer customer = new Customer(
                0,
                InputUtil.readString("Customer code: "),
                InputUtil.readLong("Agent ID: "),
                InputUtil.readString("Full name: "),
                InputUtil.readDate("Date of birth (yyyy-MM-dd): "),
                InputUtil.readString("Gender (MALE/FEMALE/OTHER): ").toUpperCase(),
                InputUtil.readString("Email: "),
                InputUtil.readString("Phone: "),
                InputUtil.readString("Address line: "),
                InputUtil.readString("City: "),
                InputUtil.readString("State: "),
                InputUtil.readString("Postal code: "),
                "ACTIVE"
        );

        // Customer must reference an existing agent ID.
        customerRepository.saveCustomer(customer);
        System.out.println("Customer saved with ID: " + customer.getCustomerId());
    }

    private void addLifePolicy() throws RepositoryException {
        LifePolicy policy = new LifePolicy(
                0,
                InputUtil.readString("Policy number: "),
                InputUtil.readLong("Customer ID: "),
                InputUtil.readLong("Agent ID: "),
                InputUtil.readString("Policy name: "),
                0,
                InputUtil.readDouble("Coverage amount: "),
                InputUtil.readString("Payment frequency (MONTHLY/QUARTERLY/YEARLY): ").toUpperCase(),
                InputUtil.readDate("Start date (yyyy-MM-dd): "),
                InputUtil.readDate("End date (yyyy-MM-dd): "),
                "ACTIVE",
                InputUtil.readString("Nominee name: "),
                InputUtil.readString("Nominee relation: "),
                InputUtil.readInt("Nominee age: "),
                InputUtil.readString("Medical history: "),
                InputUtil.readString("Risk category (LOW/MEDIUM/HIGH): ").toUpperCase()
        );

        savePolicy(policy);
    }

    private void addHealthPolicy() throws RepositoryException {
        HealthPolicy policy = new HealthPolicy(
                0,
                InputUtil.readString("Policy number: "),
                InputUtil.readLong("Customer ID: "),
                InputUtil.readLong("Agent ID: "),
                InputUtil.readString("Policy name: "),
                0,
                InputUtil.readDouble("Coverage amount: "),
                InputUtil.readString("Payment frequency (MONTHLY/QUARTERLY/YEARLY): ").toUpperCase(),
                InputUtil.readDate("Start date (yyyy-MM-dd): "),
                InputUtil.readDate("End date (yyyy-MM-dd): "),
                "ACTIVE",
                InputUtil.readInt("Covered members: "),
                InputUtil.readString("Pre-existing diseases: "),
                InputUtil.readString("Hospital plan (BASIC/STANDARD/PREMIUM): ").toUpperCase(),
                InputUtil.readDouble("Room rent limit: ")
        );

        savePolicy(policy);
    }

    private void addVehiclePolicy() throws RepositoryException {
        VehiclePolicy policy = new VehiclePolicy(
                0,
                InputUtil.readString("Policy number: "),
                InputUtil.readLong("Customer ID: "),
                InputUtil.readLong("Agent ID: "),
                InputUtil.readString("Policy name: "),
                0,
                InputUtil.readDouble("Coverage amount: "),
                InputUtil.readString("Payment frequency (MONTHLY/QUARTERLY/YEARLY): ").toUpperCase(),
                InputUtil.readDate("Start date (yyyy-MM-dd): "),
                InputUtil.readDate("End date (yyyy-MM-dd): "),
                "ACTIVE",
                InputUtil.readString("Vehicle registration number: "),
                InputUtil.readString("Vehicle type (CAR/BIKE/TRUCK): ").toUpperCase(),
                InputUtil.readString("Manufacturer: "),
                InputUtil.readString("Model: "),
                InputUtil.readInt("Manufacture year: "),
                InputUtil.readString("Engine number: "),
                InputUtil.readString("Chassis number: ")
        );

        savePolicy(policy);
    }

    private void savePolicy(Policy policy) throws RepositoryException {
        // Premium is calculated using polymorphism before saving.
        policy.setPremiumAmount(policy.calculatePremium());
        policyRepository.savePolicy(policy);
        System.out.println("Policy saved with ID: " + policy.getPolicyId());
        System.out.printf("Calculated premium: %.2f%n", policy.getPremiumAmount());
    }

    private void recordPayment() throws RepositoryException {
        Payment payment = new Payment(
                0,
                InputUtil.readString("Payment reference: "),
                InputUtil.readLong("Policy ID: "),
                InputUtil.readLong("Customer ID: "),
                InputUtil.readDate("Payment date (yyyy-MM-dd): "),
                InputUtil.readDouble("Amount: "),
                InputUtil.readString("Payment method (CASH/CARD/UPI/BANK_TRANSFER): ").toUpperCase(),
                InputUtil.readString("Status (PAID/FAILED/PENDING): ").toUpperCase()
        );

        paymentRepository.savePayment(payment);
        System.out.println("Payment saved with ID: " + payment.getPaymentId());
    }

    private void submitClaim() throws RepositoryException {
        Claim claim = new Claim(
                0,
                InputUtil.readString("Claim number: "),
                InputUtil.readLong("Policy ID: "),
                InputUtil.readLong("Customer ID: "),
                InputUtil.readDate("Claim date (yyyy-MM-dd): "),
                InputUtil.readDouble("Claim amount: "),
                0,
                InputUtil.readString("Claim reason: "),
                "REQUESTED",
                "Claim submitted"
        );

        claimRepository.saveClaim(claim);
        System.out.println("Claim saved with ID: " + claim.getClaimId());
    }

    private void approveClaim() throws RepositoryException {
        long claimId = InputUtil.readLong("Claim ID: ");
        double approvedAmount = InputUtil.readDouble("Approved amount: ");
        String remarks = InputUtil.readString("Remarks: ");

        // First read existing claim, then update its status.
        Claim claim = claimRepository.findClaimById(claimId);
        claim.approveClaim(approvedAmount, remarks);
        claimRepository.updateClaimStatus(claim);
        System.out.println("Claim approved successfully.");
    }

    private void findPolicy() throws RepositoryException {
        long policyId = InputUtil.readLong("Policy ID: ");

        // Repository returns correct child object based on policy_type.
        Policy policy = policyRepository.findPolicyById(policyId);
        policy.displayPolicyDetails();
    }
}
