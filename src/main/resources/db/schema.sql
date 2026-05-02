CREATE DATABASE IF NOT EXISTS insuretech_pro;
USE insuretech_pro;

-- Reset demo tables so every presentation starts with clean, non-duplicate data.
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS claims;
DROP TABLE IF EXISTS vehicle_policy_details;
DROP TABLE IF EXISTS health_policy_details;
DROP TABLE IF EXISTS life_policy_details;
DROP TABLE IF EXISTS policies;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS agents;
DROP TABLE IF EXISTS users;

-- Users are used by the JavaFX login screen.
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    reference_id BIGINT,
    role VARCHAR(30) NOT NULL DEFAULT 'ADMIN',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (role IN ('ADMIN', 'AGENT', 'CUSTOMER')),
    CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

-- Agents manage customers and sell policies.
CREATE TABLE IF NOT EXISTS agents (
    agent_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    agent_code VARCHAR(30) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    hire_date DATE NOT NULL,
    commission_rate DECIMAL(5, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (commission_rate >= 0),
    CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

-- Customers buy one or more policies.
CREATE TABLE IF NOT EXISTS customers (
    customer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_code VARCHAR(30) NOT NULL UNIQUE,
    agent_id BIGINT NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    address_line VARCHAR(255),
    city VARCHAR(80),
    state VARCHAR(80),
    postal_code VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_customers_agent_id (agent_id),
    CONSTRAINT fk_customers_agent
        FOREIGN KEY (agent_id) REFERENCES agents(agent_id),
    CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

-- Policies store common data for all policy types.
CREATE TABLE IF NOT EXISTS policies (
    policy_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    policy_number VARCHAR(40) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    agent_id BIGINT NOT NULL,
    policy_type VARCHAR(20) NOT NULL,
    policy_name VARCHAR(100) NOT NULL,
    premium_amount DECIMAL(12, 2) NOT NULL,
    coverage_amount DECIMAL(12, 2) NOT NULL,
    payment_frequency VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_policies_customer_id (customer_id),
    INDEX idx_policies_agent_id (agent_id),
    CONSTRAINT fk_policies_customer
        FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    CONSTRAINT fk_policies_agent
        FOREIGN KEY (agent_id) REFERENCES agents(agent_id),
    CHECK (policy_type IN ('LIFE', 'HEALTH', 'VEHICLE')),
    CHECK (payment_frequency IN ('MONTHLY', 'QUARTERLY', 'YEARLY')),
    CHECK (status IN ('ACTIVE', 'EXPIRED', 'CANCELLED')),
    CHECK (premium_amount >= 0),
    CHECK (coverage_amount > 0),
    CHECK (end_date > start_date)
);

-- Life policy details are stored separately because they apply only to LIFE policies.
CREATE TABLE IF NOT EXISTS life_policy_details (
    policy_id BIGINT PRIMARY KEY,
    nominee_name VARCHAR(100) NOT NULL,
    nominee_relation VARCHAR(50) NOT NULL,
    nominee_age INT NOT NULL,
    medical_history VARCHAR(255),
    risk_category VARCHAR(20) NOT NULL,
    CONSTRAINT fk_life_policy
        FOREIGN KEY (policy_id) REFERENCES policies(policy_id)
        ON DELETE CASCADE,
    CHECK (nominee_age > 0),
    CHECK (risk_category IN ('LOW', 'MEDIUM', 'HIGH'))
);

-- Health policy details are stored separately because they apply only to HEALTH policies.
CREATE TABLE IF NOT EXISTS health_policy_details (
    policy_id BIGINT PRIMARY KEY,
    covered_members INT NOT NULL,
    pre_existing_diseases VARCHAR(255),
    network_hospital_plan VARCHAR(20) NOT NULL,
    room_rent_limit DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_health_policy
        FOREIGN KEY (policy_id) REFERENCES policies(policy_id)
        ON DELETE CASCADE,
    CHECK (covered_members > 0),
    CHECK (room_rent_limit >= 0),
    CHECK (network_hospital_plan IN ('BASIC', 'STANDARD', 'PREMIUM'))
);

-- Vehicle policy details are stored separately because they apply only to VEHICLE policies.
CREATE TABLE IF NOT EXISTS vehicle_policy_details (
    policy_id BIGINT PRIMARY KEY,
    vehicle_registration_number VARCHAR(30) NOT NULL UNIQUE,
    vehicle_type VARCHAR(20) NOT NULL,
    manufacturer VARCHAR(80) NOT NULL,
    model VARCHAR(80) NOT NULL,
    manufacture_year INT NOT NULL,
    engine_number VARCHAR(80) NOT NULL UNIQUE,
    chassis_number VARCHAR(80) NOT NULL UNIQUE,
    CONSTRAINT fk_vehicle_policy
        FOREIGN KEY (policy_id) REFERENCES policies(policy_id)
        ON DELETE CASCADE,
    CHECK (vehicle_type IN ('CAR', 'BIKE', 'TRUCK')),
    CHECK (manufacture_year >= 1900)
);

-- Claims are requests raised by customers against policies.
CREATE TABLE IF NOT EXISTS claims (
    claim_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    claim_number VARCHAR(40) NOT NULL UNIQUE,
    policy_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    claim_date DATE NOT NULL,
    claim_amount DECIMAL(12, 2) NOT NULL,
    approved_amount DECIMAL(12, 2),
    claim_reason VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'REQUESTED',
    remarks VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_claims_policy_id (policy_id),
    INDEX idx_claims_customer_id (customer_id),
    CONSTRAINT fk_claims_policy
        FOREIGN KEY (policy_id) REFERENCES policies(policy_id),
    CONSTRAINT fk_claims_customer
        FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    CHECK (claim_amount > 0),
    CHECK (approved_amount IS NULL OR approved_amount >= 0),
    CHECK (status IN ('REQUESTED', 'PROCESSING', 'APPROVED', 'REJECTED', 'SETTLED', 'CANCELLED'))
);

-- Payments store premium payment transactions.
CREATE TABLE IF NOT EXISTS payments (
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_reference VARCHAR(40) NOT NULL UNIQUE,
    policy_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    payment_date DATE NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_payments_policy_id (policy_id),
    INDEX idx_payments_customer_id (customer_id),
    CONSTRAINT fk_payments_policy
        FOREIGN KEY (policy_id) REFERENCES policies(policy_id),
    CONSTRAINT fk_payments_customer
        FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    CHECK (amount > 0),
    CHECK (payment_method IN ('CASH', 'CARD', 'UPI', 'BANK_TRANSFER')),
    CHECK (status IN ('PAID', 'FAILED', 'PENDING'))
);

-- Demo users for role-based login.
INSERT IGNORE INTO agents (agent_id, agent_code, full_name, email, phone, hire_date, commission_rate, status)
VALUES
    (1, 'AGT-DEMO', 'Neha Kapoor', 'agent.demo@insuretech.com', '9000011111', '2022-06-01', 5.50, 'ACTIVE'),
    (2, 'AGT-DELHI', 'Rohan Sharma', 'rohan.agent@insuretech.com', '9000022222', '2021-09-15', 6.25, 'ACTIVE'),
    (3, 'AGT-MUMBAI', 'Sneha Iyer', 'sneha.agent@insuretech.com', '9000033333', '2023-01-20', 5.75, 'ACTIVE');

INSERT IGNORE INTO customers (customer_id, customer_code, agent_id, full_name, date_of_birth, gender, email, phone,
                              address_line, city, state, postal_code, status)
VALUES
    (1, 'CUS-DEMO', 1, 'Aarav Mehta', '1998-05-10', 'MALE', 'customer.demo@example.com', '9876543210',
        'MG Road', 'Pune', 'Maharashtra', '411001', 'ACTIVE'),
    (2, 'CUS-PRIYA', 1, 'Priya Nair', '1994-11-24', 'FEMALE', 'priya.customer@example.com', '9876543211',
        'Baner Road', 'Pune', 'Maharashtra', '411045', 'ACTIVE'),
    (3, 'CUS-IMRAN', 2, 'Imran Khan', '1990-03-18', 'MALE', 'imran.customer@example.com', '9876543212',
        'Connaught Place', 'Delhi', 'Delhi', '110001', 'ACTIVE'),
    (4, 'CUS-MEERA', 2, 'Meera Joshi', '1988-08-09', 'FEMALE', 'meera.customer@example.com', '9876543213',
        'Karol Bagh', 'Delhi', 'Delhi', '110005', 'ACTIVE'),
    (5, 'CUS-VIKRAM', 3, 'Vikram Rao', '1985-01-30', 'MALE', 'vikram.customer@example.com', '9876543214',
        'Andheri West', 'Mumbai', 'Maharashtra', '400058', 'ACTIVE'),
    (6, 'CUS-ANANYA', 3, 'Ananya Sen', '1996-07-14', 'FEMALE', 'ananya.customer@example.com', '9876543215',
        'Powai', 'Mumbai', 'Maharashtra', '400076', 'ACTIVE');

INSERT IGNORE INTO policies (policy_id, policy_number, customer_id, agent_id, policy_type, policy_name,
                             premium_amount, coverage_amount, payment_frequency, start_date, end_date, status)
VALUES
    (1, 'POL-LIFE-1001', 1, 1, 'LIFE', 'Secure Life Plus', 25000.00, 1000000.00, 'YEARLY',
        '2026-01-01', '2046-01-01', 'ACTIVE'),
    (2, 'POL-HEALTH-1002', 2, 1, 'HEALTH', 'Family Health Shield', 18000.00, 500000.00, 'YEARLY',
        '2026-02-01', '2027-02-01', 'ACTIVE'),
    (3, 'POL-VEHICLE-1003', 3, 2, 'VEHICLE', 'Comprehensive Motor Cover', 42000.00, 700000.00, 'YEARLY',
        '2026-03-01', '2027-03-01', 'ACTIVE'),
    (4, 'POL-LIFE-1004', 4, 2, 'LIFE', 'Future Secure Term Plan', 32000.00, 1500000.00, 'YEARLY',
        '2026-01-15', '2041-01-15', 'ACTIVE'),
    (5, 'POL-HEALTH-1005', 5, 3, 'HEALTH', 'Senior Care Health', 24000.00, 800000.00, 'YEARLY',
        '2026-04-01', '2027-04-01', 'ACTIVE'),
    (6, 'POL-VEHICLE-1006', 6, 3, 'VEHICLE', 'Two Wheeler Protect', 6500.00, 120000.00, 'YEARLY',
        '2026-04-10', '2027-04-10', 'ACTIVE'),
    (7, 'POL-LIFE-1007', 2, 1, 'LIFE', 'Smart Life Cover', 28000.00, 1200000.00, 'YEARLY',
        '2026-05-01', '2046-05-01', 'ACTIVE'),
    (8, 'POL-HEALTH-1008', 1, 1, 'HEALTH', 'Personal Health Assist', 14000.00, 350000.00, 'MONTHLY',
        '2026-05-01', '2027-05-01', 'ACTIVE');

INSERT IGNORE INTO life_policy_details (policy_id, nominee_name, nominee_relation, nominee_age, medical_history, risk_category)
VALUES
    (1, 'Kavya Mehta', 'Spouse', 29, 'None', 'LOW'),
    (4, 'Arjun Joshi', 'Spouse', 39, 'Blood pressure', 'MEDIUM'),
    (7, 'Rahul Nair', 'Brother', 33, 'None', 'LOW');

INSERT IGNORE INTO health_policy_details (policy_id, covered_members, pre_existing_diseases, network_hospital_plan, room_rent_limit)
VALUES
    (2, 4, 'None', 'PREMIUM', 8000.00),
    (5, 2, 'Diabetes', 'STANDARD', 6000.00),
    (8, 1, 'None', 'BASIC', 3500.00);

INSERT IGNORE INTO vehicle_policy_details (policy_id, vehicle_registration_number, vehicle_type, manufacturer, model,
                                           manufacture_year, engine_number, chassis_number)
VALUES
    (3, 'DL01AB1234', 'CAR', 'Hyundai', 'Creta', 2023, 'ENG-DL01AB1234', 'CHS-DL01AB1234'),
    (6, 'MH02CD5678', 'BIKE', 'Honda', 'Activa', 2022, 'ENG-MH02CD5678', 'CHS-MH02CD5678');

INSERT IGNORE INTO claims (claim_id, claim_number, policy_id, customer_id, claim_date, claim_amount,
                           approved_amount, claim_reason, status, remarks)
VALUES
    (1, 'CLM-1001', 2, 2, '2026-04-12', 65000.00, 0.00, 'Hospitalization claim', 'PROCESSING',
        'Documents are under review'),
    (2, 'CLM-1002', 3, 3, '2026-04-18', 120000.00, 95000.00, 'Accident repair claim', 'APPROVED',
        'Surveyor approved partial amount'),
    (3, 'CLM-1003', 5, 5, '2026-05-01', 38000.00, 38000.00, 'Medical treatment claim', 'SETTLED',
        'Claim settled successfully'),
    (4, 'CLM-1004', 6, 6, '2026-05-02', 9500.00, 0.00, 'Vehicle damage claim', 'REQUESTED',
        'New claim submitted'),
    (5, 'CLM-1005', 8, 1, '2026-05-02', 22000.00, 0.00, 'Health checkup reimbursement', 'REJECTED',
        'Documents incomplete'),
    (6, 'CLM-1006', 4, 4, '2026-04-25', 75000.00, 0.00, 'Critical illness claim', 'PROCESSING',
        'Medical documents verified');

INSERT IGNORE INTO payments (payment_id, payment_reference, policy_id, customer_id, payment_date, amount,
                             payment_method, status)
VALUES
    (1, 'PAY-1001', 1, 1, '2026-04-05', 25000.00, 'UPI', 'PAID'),
    (2, 'PAY-1002', 2, 2, '2026-04-10', 18000.00, 'CARD', 'PAID'),
    (3, 'PAY-1003', 3, 3, '2026-04-15', 42000.00, 'BANK_TRANSFER', 'PENDING'),
    (4, 'PAY-1004', 4, 4, '2026-05-01', 32000.00, 'BANK_TRANSFER', 'PAID'),
    (5, 'PAY-1005', 5, 5, '2026-05-01', 24000.00, 'CARD', 'PAID'),
    (6, 'PAY-1006', 6, 6, '2026-05-02', 6500.00, 'UPI', 'PAID'),
    (7, 'PAY-1007', 7, 2, '2026-05-02', 28000.00, 'UPI', 'PENDING'),
    (8, 'PAY-1008', 8, 1, '2026-05-02', 14000.00, 'CASH', 'PAID');

INSERT INTO users (username, password, full_name, reference_id, role, status)
VALUES
    ('admin', 'admin123', 'John Doe', NULL, 'ADMIN', 'ACTIVE'),
    ('agent', 'agent123', 'Neha Kapoor', 1, 'AGENT', 'ACTIVE'),
    ('agent2', 'agent2123', 'Rohan Sharma', 2, 'AGENT', 'ACTIVE'),
    ('agent3', 'agent3123', 'Sneha Iyer', 3, 'AGENT', 'ACTIVE'),
    ('customer', 'customer123', 'Aarav Mehta', 1, 'CUSTOMER', 'ACTIVE'),
    ('priya', 'priya123', 'Priya Nair', 2, 'CUSTOMER', 'ACTIVE'),
    ('imran', 'imran123', 'Imran Khan', 3, 'CUSTOMER', 'ACTIVE'),
    ('meera', 'meera123', 'Meera Joshi', 4, 'CUSTOMER', 'ACTIVE'),
    ('vikram', 'vikram123', 'Vikram Rao', 5, 'CUSTOMER', 'ACTIVE'),
    ('ananya', 'ananya123', 'Ananya Sen', 6, 'CUSTOMER', 'ACTIVE');
