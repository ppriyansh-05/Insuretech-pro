package com.insuretechpro.repository;

import com.insuretechpro.database.DatabaseConnection;
import com.insuretechpro.exception.DatabaseConnectionException;
import com.insuretechpro.exception.RepositoryException;
import com.insuretechpro.model.Claim;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * ClaimRepository contains JDBC code for the claims table.
 * It saves and reads claim records from MySQL.
 */
public class ClaimRepository {
    public long saveClaim(Claim claim) throws RepositoryException {
        String sql = """
                INSERT INTO claims
                (claim_number, policy_id, customer_id, claim_date, claim_amount,
                 approved_amount, claim_reason, status, remarks)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // PreparedStatement maps Java claim fields to SQL columns.
            statement.setString(1, claim.getClaimNumber());
            statement.setLong(2, claim.getPolicyId());
            statement.setLong(3, claim.getCustomerId());
            statement.setDate(4, Date.valueOf(claim.getClaimDate()));
            statement.setDouble(5, claim.getClaimAmount());
            statement.setDouble(6, claim.getApprovedAmount());
            statement.setString(7, claim.getClaimReason());
            statement.setString(8, claim.getStatus());
            statement.setString(9, claim.getRemarks());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    long generatedId = keys.getLong(1);
                    claim.setClaimId(generatedId);
                    return generatedId;
                }
            }

            throw new RepositoryException("Claim saved but generated ID was not returned.", null);
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not save claim.", exception);
        }
    }

    public Claim findClaimById(long claimId) throws RepositoryException {
        String sql = "SELECT * FROM claims WHERE claim_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, claimId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToClaim(resultSet);
                }
            }

            throw new RepositoryException("Claim not found with ID: " + claimId, null);
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not find claim.", exception);
        }
    }

    public List<Claim> findAllClaims() throws RepositoryException {
        String sql = "SELECT * FROM claims ORDER BY claim_id DESC";
        List<Claim> claims = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            // Each row from ResultSet becomes one Claim object.
            while (resultSet.next()) {
                claims.add(mapResultSetToClaim(resultSet));
            }

            return claims;
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not load claims.", exception);
        }
    }

    public void updateClaimStatus(Claim claim) throws RepositoryException {
        String sql = """
                UPDATE claims
                SET approved_amount = ?, status = ?, remarks = ?
                WHERE claim_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // This updates claim approval/rejection result in the database.
            statement.setDouble(1, claim.getApprovedAmount());
            statement.setString(2, claim.getStatus());
            statement.setString(3, claim.getRemarks());
            statement.setLong(4, claim.getClaimId());
            statement.executeUpdate();
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not update claim status.", exception);
        }
    }

    public void deleteClaim(long claimId) throws RepositoryException {
        String sql = "DELETE FROM claims WHERE claim_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, claimId);
            statement.executeUpdate();
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not delete claim.", exception);
        }
    }

    private Claim mapResultSetToClaim(ResultSet resultSet) throws SQLException {
        // This method converts one claims table row into a Claim object.
        return new Claim(
                resultSet.getLong("claim_id"),
                resultSet.getString("claim_number"),
                resultSet.getLong("policy_id"),
                resultSet.getLong("customer_id"),
                resultSet.getDate("claim_date").toLocalDate(),
                resultSet.getDouble("claim_amount"),
                resultSet.getDouble("approved_amount"),
                resultSet.getString("claim_reason"),
                resultSet.getString("status"),
                resultSet.getString("remarks")
        );
    }
}
