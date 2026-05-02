package com.insuretechpro.repository;

import com.insuretechpro.database.DatabaseConnection;
import com.insuretechpro.exception.DatabaseConnectionException;
import com.insuretechpro.exception.RepositoryException;
import com.insuretechpro.model.Payment;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * PaymentRepository contains JDBC code for the payments table.
 * It stores premium payment transactions in MySQL.
 */
public class PaymentRepository {
    public long savePayment(Payment payment) throws RepositoryException {
        String sql = """
                INSERT INTO payments
                (payment_reference, policy_id, customer_id, payment_date, amount, payment_method, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Each setter fills one SQL placeholder in order.
            statement.setString(1, payment.getPaymentReference());
            statement.setLong(2, payment.getPolicyId());
            statement.setLong(3, payment.getCustomerId());
            statement.setDate(4, Date.valueOf(payment.getPaymentDate()));
            statement.setDouble(5, payment.getAmount());
            statement.setString(6, payment.getPaymentMethod());
            statement.setString(7, payment.getStatus());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    long generatedId = keys.getLong(1);
                    payment.setPaymentId(generatedId);
                    return generatedId;
                }
            }

            throw new RepositoryException("Payment saved but generated ID was not returned.", null);
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not save payment.", exception);
        }
    }

    public Payment findPaymentById(long paymentId) throws RepositoryException {
        String sql = "SELECT * FROM payments WHERE payment_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, paymentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToPayment(resultSet);
                }
            }

            throw new RepositoryException("Payment not found with ID: " + paymentId, null);
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not find payment.", exception);
        }
    }

    public List<Payment> findAllPayments() throws RepositoryException {
        String sql = "SELECT * FROM payments ORDER BY payment_id DESC";
        List<Payment> payments = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            // Each row from ResultSet becomes one Payment object.
            while (resultSet.next()) {
                payments.add(mapResultSetToPayment(resultSet));
            }

            return payments;
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not load payments.", exception);
        }
    }

    public void updatePaymentStatus(Payment payment) throws RepositoryException {
        String sql = "UPDATE payments SET status = ? WHERE payment_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // This updates only the payment status after success/failure.
            statement.setString(1, payment.getStatus());
            statement.setLong(2, payment.getPaymentId());
            statement.executeUpdate();
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not update payment status.", exception);
        }
    }

    public void deletePayment(long paymentId) throws RepositoryException {
        String sql = "DELETE FROM payments WHERE payment_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, paymentId);
            statement.executeUpdate();
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not delete payment.", exception);
        }
    }

    private Payment mapResultSetToPayment(ResultSet resultSet) throws SQLException {
        // This method converts one payments table row into a Payment object.
        return new Payment(
                resultSet.getLong("payment_id"),
                resultSet.getString("payment_reference"),
                resultSet.getLong("policy_id"),
                resultSet.getLong("customer_id"),
                resultSet.getDate("payment_date").toLocalDate(),
                resultSet.getDouble("amount"),
                resultSet.getString("payment_method"),
                resultSet.getString("status")
        );
    }
}
