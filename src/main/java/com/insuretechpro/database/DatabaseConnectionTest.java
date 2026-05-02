package com.insuretechpro.database;

import com.insuretechpro.exception.DatabaseConnectionException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DatabaseConnectionTest checks whether Java can connect to MySQL.
 * Run this after updating database.properties with your MySQL password.
 */
public class DatabaseConnectionTest {
    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // If connection is not null and open, JDBC connection is successful.
            if (connection != null && !connection.isClosed()) {
                System.out.println("Connected to MySQL successfully.");
                System.out.println("Database: " + connection.getCatalog());
            }
        } catch (DatabaseConnectionException | SQLException exception) {
            System.out.println("Connection test failed: " + exception.getMessage());
        }
    }
}
