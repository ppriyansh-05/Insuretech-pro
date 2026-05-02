package com.insuretechpro.repository;

import com.insuretechpro.database.DatabaseConnection;
import com.insuretechpro.exception.DatabaseConnectionException;
import com.insuretechpro.exception.RepositoryException;
import com.insuretechpro.model.DashboardStats;
import com.insuretechpro.model.PolicyDistribution;
import com.insuretechpro.model.RecentClaimView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DashboardRepository contains SQL queries used by the JavaFX dashboard.
 * It keeps dashboard calculations out of the UI code.
 */
public class DashboardRepository {
    public DashboardStats getDashboardStats() throws RepositoryException {
        long activePolicies = count("SELECT COUNT(*) FROM policies WHERE status = 'ACTIVE'");
        long pendingClaims = count("SELECT COUNT(*) FROM claims WHERE status IN ('REQUESTED', 'PROCESSING')");
        double monthlyRevenue = sumMonthlyRevenue();

        return new DashboardStats(activePolicies, pendingClaims, monthlyRevenue);
    }

    public List<RecentClaimView> getRecentClaims(int limit) throws RepositoryException {
        String sql = """
                SELECT c.claim_id, cu.full_name, p.policy_type, c.status, c.claim_date
                FROM claims c
                JOIN customers cu ON c.customer_id = cu.customer_id
                JOIN policies p ON c.policy_id = p.policy_id
                ORDER BY c.claim_date DESC, c.claim_id DESC
                LIMIT ?
                """;

        List<RecentClaimView> claims = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    claims.add(new RecentClaimView(
                            resultSet.getLong("claim_id"),
                            resultSet.getString("full_name"),
                            resultSet.getString("policy_type"),
                            resultSet.getString("status"),
                            resultSet.getDate("claim_date").toLocalDate()
                    ));
                }
            }

            return claims;
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not load recent claims.", exception);
        }
    }

    public List<PolicyDistribution> getPolicyDistribution() throws RepositoryException {
        String sql = """
                SELECT policy_type, COUNT(*) AS total_count
                FROM policies
                GROUP BY policy_type
                """;

        List<PolicyDistribution> distribution = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                distribution.add(new PolicyDistribution(
                        resultSet.getString("policy_type"),
                        resultSet.getLong("total_count")
                ));
            }

            return distribution;
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not load policy distribution.", exception);
        }
    }

    private long count(String sql) throws RepositoryException {
        // Generic helper for COUNT(*) dashboard queries.
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getLong(1);
            }

            return 0;
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not calculate dashboard count.", exception);
        }
    }

    private double sumMonthlyRevenue() throws RepositoryException {
        String sql = """
                SELECT COALESCE(SUM(amount), 0) AS monthly_revenue
                FROM payments
                WHERE status = 'PAID'
                AND YEAR(payment_date) = YEAR(CURRENT_DATE())
                AND MONTH(payment_date) = MONTH(CURRENT_DATE())
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getDouble("monthly_revenue");
            }

            return 0;
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not calculate monthly revenue.", exception);
        }
    }
}
