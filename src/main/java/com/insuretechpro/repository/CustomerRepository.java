package com.insuretechpro.repository;

import com.insuretechpro.database.DatabaseConnection;
import com.insuretechpro.exception.DatabaseConnectionException;
import com.insuretechpro.exception.RepositoryException;
import com.insuretechpro.model.Customer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * CustomerRepository contains JDBC code for the customers table.
 * It keeps database code separate from model and service classes.
 */
public class CustomerRepository {
    public long saveCustomer(Customer customer) throws RepositoryException {
        String sql = """
                INSERT INTO customers
                (customer_code, agent_id, full_name, date_of_birth, gender, email, phone,
                 address_line, city, state, postal_code, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Each setter fills one question mark in the SQL query.
            statement.setString(1, customer.getCustomerCode());
            statement.setLong(2, customer.getAgentId());
            statement.setString(3, customer.getFullName());
            statement.setDate(4, Date.valueOf(customer.getDateOfBirth()));
            statement.setString(5, customer.getGender());
            statement.setString(6, customer.getEmail());
            statement.setString(7, customer.getPhone());
            statement.setString(8, customer.getAddressLine());
            statement.setString(9, customer.getCity());
            statement.setString(10, customer.getState());
            statement.setString(11, customer.getPostalCode());
            statement.setString(12, customer.getStatus());
            statement.executeUpdate();

            // Generated key is the auto-increment customer_id created by MySQL.
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    long generatedId = keys.getLong(1);
                    customer.setCustomerId(generatedId);
                    return generatedId;
                }
            }

            throw new RepositoryException("Customer saved but generated ID was not returned.", null);
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not save customer.", exception);
        }
    }

    public Customer findCustomerById(long customerId) throws RepositoryException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Using a placeholder makes the search safe and clean.
            statement.setLong(1, customerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToCustomer(resultSet);
                }
            }

            throw new RepositoryException("Customer not found with ID: " + customerId, null);
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not find customer.", exception);
        }
    }

    public List<Customer> findAllCustomers() throws RepositoryException {
        String sql = "SELECT * FROM customers ORDER BY customer_id DESC";
        List<Customer> customers = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            // Each row from ResultSet becomes one Customer object.
            while (resultSet.next()) {
                customers.add(mapResultSetToCustomer(resultSet));
            }

            return customers;
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not load customers.", exception);
        }
    }

    public void updateCustomerStatus(long customerId, String status) throws RepositoryException {
        String sql = "UPDATE customers SET status = ? WHERE customer_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setLong(2, customerId);
            statement.executeUpdate();
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not update customer status.", exception);
        }
    }

    public void deleteCustomer(long customerId) throws RepositoryException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, customerId);
            statement.executeUpdate();
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not delete customer. Delete linked policies, claims, and payments first.", exception);
        }
    }

    private Customer mapResultSetToCustomer(ResultSet resultSet) throws SQLException {
        // This method converts one database row into one Customer object.
        return new Customer(
                resultSet.getLong("customer_id"),
                resultSet.getString("customer_code"),
                resultSet.getLong("agent_id"),
                resultSet.getString("full_name"),
                resultSet.getDate("date_of_birth").toLocalDate(),
                resultSet.getString("gender"),
                resultSet.getString("email"),
                resultSet.getString("phone"),
                resultSet.getString("address_line"),
                resultSet.getString("city"),
                resultSet.getString("state"),
                resultSet.getString("postal_code"),
                resultSet.getString("status")
        );
    }
}
