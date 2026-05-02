package com.insuretechpro.repository;

import com.insuretechpro.database.DatabaseConnection;
import com.insuretechpro.exception.DatabaseConnectionException;
import com.insuretechpro.exception.RepositoryException;
import com.insuretechpro.model.Agent;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * AgentRepository contains JDBC code for the agents table.
 * Service classes should call this repository instead of writing SQL directly.
 */
public class AgentRepository {
    public long saveAgent(Agent agent) throws RepositoryException {
        String sql = """
                INSERT INTO agents
                (agent_code, full_name, email, phone, hire_date, commission_rate, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // PreparedStatement safely sends Java values to SQL placeholders.
            statement.setString(1, agent.getAgentCode());
            statement.setString(2, agent.getFullName());
            statement.setString(3, agent.getEmail());
            statement.setString(4, agent.getPhone());
            statement.setDate(5, Date.valueOf(agent.getHireDate()));
            statement.setDouble(6, agent.getCommissionRate());
            statement.setString(7, agent.getStatus());
            statement.executeUpdate();

            // Generated key is the auto-increment agent_id created by MySQL.
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    long generatedId = keys.getLong(1);
                    agent.setAgentId(generatedId);
                    return generatedId;
                }
            }

            throw new RepositoryException("Agent saved but generated ID was not returned.", null);
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not save agent.", exception);
        }
    }

    public Agent findAgentById(long agentId) throws RepositoryException {
        String sql = "SELECT * FROM agents WHERE agent_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // This placeholder prevents SQL injection.
            statement.setLong(1, agentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToAgent(resultSet);
                }
            }

            throw new RepositoryException("Agent not found with ID: " + agentId, null);
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not find agent.", exception);
        }
    }

    public List<Agent> findAllAgents() throws RepositoryException {
        String sql = "SELECT * FROM agents ORDER BY agent_id DESC";
        List<Agent> agents = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            // Each row from ResultSet becomes one Agent object.
            while (resultSet.next()) {
                agents.add(mapResultSetToAgent(resultSet));
            }

            return agents;
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not load agents.", exception);
        }
    }

    public void updateAgentStatus(long agentId, String status) throws RepositoryException {
        String sql = "UPDATE agents SET status = ? WHERE agent_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setLong(2, agentId);
            statement.executeUpdate();
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not update agent status.", exception);
        }
    }

    public void deleteAgent(long agentId) throws RepositoryException {
        String sql = "DELETE FROM agents WHERE agent_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, agentId);
            statement.executeUpdate();
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not delete agent. Remove or reassign linked customers and policies first.", exception);
        }
    }

    private Agent mapResultSetToAgent(ResultSet resultSet) throws SQLException {
        // This method converts one database row into one Agent object.
        return new Agent(
                resultSet.getLong("agent_id"),
                resultSet.getString("agent_code"),
                resultSet.getString("full_name"),
                resultSet.getString("email"),
                resultSet.getString("phone"),
                resultSet.getDate("hire_date").toLocalDate(),
                resultSet.getDouble("commission_rate"),
                resultSet.getString("status")
        );
    }
}
