package com.insuretechpro.repository;

import com.insuretechpro.database.DatabaseConnection;
import com.insuretechpro.exception.DatabaseConnectionException;
import com.insuretechpro.exception.RepositoryException;
import com.insuretechpro.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * UserRepository contains JDBC code for login users.
 * The GUI calls this class to check username and password.
 */
public class UserRepository {
    public User authenticate(String username, String password) throws RepositoryException {
        String sql = """
                SELECT user_id, username, full_name, reference_id, role, status
                FROM users
                WHERE username = ? AND password = ? AND status = 'ACTIVE'
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // PreparedStatement avoids SQL injection during login.
            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getLong("user_id"),
                            resultSet.getString("username"),
                            resultSet.getString("full_name"),
                            getNullableLong(resultSet, "reference_id"),
                            resultSet.getString("role"),
                            resultSet.getString("status")
                    );
                }
            }

            throw new RepositoryException("Invalid username or password.", null);
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not check login details.", exception);
        }
    }

    public long saveUser(String username, String password, String fullName, Long referenceId, String role)
            throws RepositoryException {
        String sql = """
                INSERT INTO users (username, password, full_name, reference_id, role, status)
                VALUES (?, ?, ?, ?, ?, 'ACTIVE')
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, fullName);
            setNullableLong(statement, 4, referenceId);
            statement.setString(5, role);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }

            throw new RepositoryException("User saved but generated ID was not returned.", null);
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not save user login.", exception);
        }
    }

    public List<User> findAllUsers() throws RepositoryException {
        String sql = """
                SELECT user_id, username, full_name, reference_id, role, status
                FROM users
                ORDER BY role, user_id
                """;
        List<User> users = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }

            return users;
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not load users.", exception);
        }
    }

    public void updateUserStatus(long userId, String status) throws RepositoryException {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setLong(2, userId);
            statement.executeUpdate();
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not update user status.", exception);
        }
    }

    public void deleteUser(long userId) throws RepositoryException {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);
            statement.executeUpdate();
        } catch (DatabaseConnectionException | SQLException exception) {
            throw new RepositoryException("Could not delete user.", exception);
        }
    }

    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getLong("user_id"),
                resultSet.getString("username"),
                resultSet.getString("full_name"),
                getNullableLong(resultSet, "reference_id"),
                resultSet.getString("role"),
                resultSet.getString("status")
        );
    }

    private void setNullableLong(PreparedStatement statement, int index, Long value) throws SQLException {
        if (value == null) {
            statement.setNull(index, java.sql.Types.BIGINT);
        } else {
            statement.setLong(index, value);
        }
    }

    private Long getNullableLong(ResultSet resultSet, String columnName) throws SQLException {
        // reference_id can be empty for admin users.
        long value = resultSet.getLong(columnName);
        return resultSet.wasNull() ? null : value;
    }
}
