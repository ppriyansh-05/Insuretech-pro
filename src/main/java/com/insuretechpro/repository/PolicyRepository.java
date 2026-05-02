package com.insuretechpro.repository;

import com.insuretechpro.database.DatabaseConnection;
import com.insuretechpro.exception.DatabaseConnectionException;
import com.insuretechpro.exception.RepositoryException;
import com.insuretechpro.model.HealthPolicy;
import com.insuretechpro.model.LifePolicy;
import com.insuretechpro.model.Policy;
import com.insuretechpro.model.VehiclePolicy;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * PolicyRepository stores and reads policies using the joined table approach.
 * Common fields go to policies, and child-specific fields go to detail tables.
 */
public class PolicyRepository {
    public long savePolicy(Policy policy) throws RepositoryException {
        String sql = """
                INSERT INTO policies
                (policy_number, customer_id, agent_id, policy_type, policy_name, premium_amount,
                 coverage_amount, payment_frequency, start_date, end_date, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Transaction keeps parent and child table inserts together.
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                setCommonPolicyValues(statement, policy);
                statement.executeUpdate();

                long policyId = readGeneratedId(statement);
                policy.setPolicyId(policyId);

                savePolicyDetails(connection, policy);
                connection.commit();
                return policyId;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not save policy.", exception);
        }
    }

    public Policy findPolicyById(long policyId) throws RepositoryException {
        String sql = "SELECT * FROM policies WHERE policy_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, policyId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new RepositoryException("Policy not found with ID: " + policyId, null);
                }

                String policyType = resultSet.getString("policy_type");

                // Child table is selected using policy_type from the parent table.
                if ("LIFE".equalsIgnoreCase(policyType)) {
                    return findLifePolicy(connection, resultSet);
                } else if ("HEALTH".equalsIgnoreCase(policyType)) {
                    return findHealthPolicy(connection, resultSet);
                } else if ("VEHICLE".equalsIgnoreCase(policyType)) {
                    return findVehiclePolicy(connection, resultSet);
                }

                throw new RepositoryException("Unknown policy type: " + policyType, null);
            }
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not find policy.", exception);
        }
    }

    public List<Policy> findAllPolicies() throws RepositoryException {
        String sql = "SELECT policy_id FROM policies ORDER BY policy_id DESC";
        List<Policy> policies = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            // Reuse findPolicyById so child policy details are loaded correctly.
            while (resultSet.next()) {
                policies.add(findPolicyById(resultSet.getLong("policy_id")));
            }

            return policies;
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not load policies.", exception);
        }
    }

    public void updatePolicyStatus(long policyId, String status) throws RepositoryException {
        String sql = "UPDATE policies SET status = ? WHERE policy_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setLong(2, policyId);
            statement.executeUpdate();
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not update policy status.", exception);
        }
    }

    public void deletePolicyWithLinkedRecords(long policyId) throws RepositoryException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                deleteByPolicyId(connection, "payments", policyId);
                deleteByPolicyId(connection, "claims", policyId);
                deleteByPolicyId(connection, "life_policy_details", policyId);
                deleteByPolicyId(connection, "health_policy_details", policyId);
                deleteByPolicyId(connection, "vehicle_policy_details", policyId);
                deleteByPolicyId(connection, "policies", policyId);
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not delete policy and linked records.", exception);
        }
    }

    private void deleteByPolicyId(Connection connection, String tableName, long policyId) throws SQLException {
        String columnName = "policies".equals(tableName) ? "policy_id" : "policy_id";
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM " + tableName + " WHERE " + columnName + " = ?")) {
            statement.setLong(1, policyId);
            statement.executeUpdate();
        }
    }

    private void setCommonPolicyValues(PreparedStatement statement, Policy policy)
            throws SQLException, RepositoryException {
        // These fields are common for all child policy types.
        statement.setString(1, policy.getPolicyNumber());
        statement.setLong(2, policy.getCustomerId());
        statement.setLong(3, policy.getAgentId());
        statement.setString(4, getPolicyType(policy));
        statement.setString(5, policy.getPolicyName());
        statement.setDouble(6, policy.getPremiumAmount());
        statement.setDouble(7, policy.getCoverageAmount());
        statement.setString(8, policy.getPaymentFrequency());
        statement.setDate(9, Date.valueOf(policy.getStartDate()));
        statement.setDate(10, Date.valueOf(policy.getEndDate()));
        statement.setString(11, policy.getStatus());
    }

    private String getPolicyType(Policy policy) throws RepositoryException {
        // instanceof checks which child class object is being saved.
        if (policy instanceof LifePolicy) {
            return "LIFE";
        } else if (policy instanceof HealthPolicy) {
            return "HEALTH";
        } else if (policy instanceof VehiclePolicy) {
            return "VEHICLE";
        }

        throw new RepositoryException("Unsupported policy type.", null);
    }

    private long readGeneratedId(PreparedStatement statement) throws SQLException, RepositoryException {
        // Reads the auto-increment policy_id generated by MySQL.
        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (keys.next()) {
                return keys.getLong(1);
            }
        }

        throw new RepositoryException("Policy saved but generated ID was not returned.", null);
    }

    private void savePolicyDetails(Connection connection, Policy policy) throws SQLException, RepositoryException {
        // Calls the correct child-table insert based on actual object type.
        if (policy instanceof LifePolicy lifePolicy) {
            saveLifePolicyDetails(connection, lifePolicy);
        } else if (policy instanceof HealthPolicy healthPolicy) {
            saveHealthPolicyDetails(connection, healthPolicy);
        } else if (policy instanceof VehiclePolicy vehiclePolicy) {
            saveVehiclePolicyDetails(connection, vehiclePolicy);
        } else {
            throw new RepositoryException("Unsupported policy details type.", null);
        }
    }

    private void saveLifePolicyDetails(Connection connection, LifePolicy policy) throws SQLException {
        String sql = """
                INSERT INTO life_policy_details
                (policy_id, nominee_name, nominee_relation, nominee_age, medical_history, risk_category)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, policy.getPolicyId());
            statement.setString(2, policy.getNomineeName());
            statement.setString(3, policy.getNomineeRelation());
            statement.setInt(4, policy.getNomineeAge());
            statement.setString(5, policy.getMedicalHistory());
            statement.setString(6, policy.getRiskCategory());
            statement.executeUpdate();
        }
    }

    private void saveHealthPolicyDetails(Connection connection, HealthPolicy policy) throws SQLException {
        String sql = """
                INSERT INTO health_policy_details
                (policy_id, covered_members, pre_existing_diseases, network_hospital_plan, room_rent_limit)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, policy.getPolicyId());
            statement.setInt(2, policy.getCoveredMembers());
            statement.setString(3, policy.getPreExistingDiseases());
            statement.setString(4, policy.getNetworkHospitalPlan());
            statement.setDouble(5, policy.getRoomRentLimit());
            statement.executeUpdate();
        }
    }

    private void saveVehiclePolicyDetails(Connection connection, VehiclePolicy policy) throws SQLException {
        String sql = """
                INSERT INTO vehicle_policy_details
                (policy_id, vehicle_registration_number, vehicle_type, manufacturer, model,
                 manufacture_year, engine_number, chassis_number)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, policy.getPolicyId());
            statement.setString(2, policy.getVehicleRegistrationNumber());
            statement.setString(3, policy.getVehicleType());
            statement.setString(4, policy.getManufacturer());
            statement.setString(5, policy.getModel());
            statement.setInt(6, policy.getManufactureYear());
            statement.setString(7, policy.getEngineNumber());
            statement.setString(8, policy.getChassisNumber());
            statement.executeUpdate();
        }
    }

    private LifePolicy findLifePolicy(Connection connection, ResultSet parent) throws SQLException, RepositoryException {
        String sql = "SELECT * FROM life_policy_details WHERE policy_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, parent.getLong("policy_id"));

            try (ResultSet child = statement.executeQuery()) {
                if (child.next()) {
                    return new LifePolicy(
                            parent.getLong("policy_id"),
                            parent.getString("policy_number"),
                            parent.getLong("customer_id"),
                            parent.getLong("agent_id"),
                            parent.getString("policy_name"),
                            parent.getDouble("premium_amount"),
                            parent.getDouble("coverage_amount"),
                            parent.getString("payment_frequency"),
                            parent.getDate("start_date").toLocalDate(),
                            parent.getDate("end_date").toLocalDate(),
                            parent.getString("status"),
                            child.getString("nominee_name"),
                            child.getString("nominee_relation"),
                            child.getInt("nominee_age"),
                            child.getString("medical_history"),
                            child.getString("risk_category")
                    );
                }
            }
        }

        throw new RepositoryException("Life policy details not found.", null);
    }

    private HealthPolicy findHealthPolicy(Connection connection, ResultSet parent) throws SQLException, RepositoryException {
        String sql = "SELECT * FROM health_policy_details WHERE policy_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, parent.getLong("policy_id"));

            try (ResultSet child = statement.executeQuery()) {
                if (child.next()) {
                    return new HealthPolicy(
                            parent.getLong("policy_id"),
                            parent.getString("policy_number"),
                            parent.getLong("customer_id"),
                            parent.getLong("agent_id"),
                            parent.getString("policy_name"),
                            parent.getDouble("premium_amount"),
                            parent.getDouble("coverage_amount"),
                            parent.getString("payment_frequency"),
                            parent.getDate("start_date").toLocalDate(),
                            parent.getDate("end_date").toLocalDate(),
                            parent.getString("status"),
                            child.getInt("covered_members"),
                            child.getString("pre_existing_diseases"),
                            child.getString("network_hospital_plan"),
                            child.getDouble("room_rent_limit")
                    );
                }
            }
        }

        throw new RepositoryException("Health policy details not found.", null);
    }

    private VehiclePolicy findVehiclePolicy(Connection connection, ResultSet parent) throws SQLException, RepositoryException {
        String sql = "SELECT * FROM vehicle_policy_details WHERE policy_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, parent.getLong("policy_id"));

            try (ResultSet child = statement.executeQuery()) {
                if (child.next()) {
                    return new VehiclePolicy(
                            parent.getLong("policy_id"),
                            parent.getString("policy_number"),
                            parent.getLong("customer_id"),
                            parent.getLong("agent_id"),
                            parent.getString("policy_name"),
                            parent.getDouble("premium_amount"),
                            parent.getDouble("coverage_amount"),
                            parent.getString("payment_frequency"),
                            parent.getDate("start_date").toLocalDate(),
                            parent.getDate("end_date").toLocalDate(),
                            parent.getString("status"),
                            child.getString("vehicle_registration_number"),
                            child.getString("vehicle_type"),
                            child.getString("manufacturer"),
                            child.getString("model"),
                            child.getInt("manufacture_year"),
                            child.getString("engine_number"),
                            child.getString("chassis_number")
                    );
                }
            }
        }

        throw new RepositoryException("Vehicle policy details not found.", null);
    }
}
