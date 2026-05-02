package com.insuretechpro.database;

import com.insuretechpro.exception.DatabaseConnectionException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * DatabaseInitializer runs schema.sql to create the database and tables.
 * Use this once before running JDBC insert/search operations.
 */
public class DatabaseInitializer {
    private static final String PROPERTIES_FILE = "database.properties";
    private static final String SCHEMA_FILE = "db/schema.sql";

    public static void main(String[] args) {
        try {
            initializeDatabase();
            System.out.println("Database and tables created successfully.");
        } catch (DatabaseConnectionException exception) {
            System.out.println("Database setup failed: " + exception.getMessage());
        }
    }

    public static void initializeDatabase() throws DatabaseConnectionException {
        Properties properties = loadProperties();
        String schemaSql = loadSchemaSql();

        try {
            // Admin URL connects to MySQL server before the project database exists.
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(
                    properties.getProperty("db.adminUrl"),
                    properties.getProperty("db.username"),
                    properties.getProperty("db.password"));
                 Statement statement = connection.createStatement()) {

                // schema.sql contains multiple SQL commands separated by semicolons.
                for (String sqlCommand : schemaSql.split(";")) {
                    String trimmedCommand = sqlCommand.trim();
                    if (!trimmedCommand.isEmpty()) {
                        statement.execute(trimmedCommand);
                    }
                }

                migrateClaimStatusConstraint(connection);
            }
        } catch (ClassNotFoundException exception) {
            throw new DatabaseConnectionException("MySQL JDBC driver was not found.", exception);
        } catch (SQLException exception) {
            throw new DatabaseConnectionException("Could not create database schema.", exception);
        }
    }

    private static void migrateClaimStatusConstraint(Connection connection) throws SQLException {
        // Existing databases may still have the old claims status CHECK constraint.
        String findChecks = """
                SELECT tc.CONSTRAINT_NAME
                FROM information_schema.TABLE_CONSTRAINTS tc
                JOIN information_schema.CHECK_CONSTRAINTS cc
                    ON tc.CONSTRAINT_SCHEMA = cc.CONSTRAINT_SCHEMA
                    AND tc.CONSTRAINT_NAME = cc.CONSTRAINT_NAME
                WHERE tc.CONSTRAINT_SCHEMA = DATABASE()
                    AND tc.TABLE_NAME = 'claims'
                    AND tc.CONSTRAINT_TYPE = 'CHECK'
                    AND cc.CHECK_CLAUSE LIKE '%status%'
                """;

        try (PreparedStatement statement = connection.prepareStatement(findChecks);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String constraintName = resultSet.getString("CONSTRAINT_NAME");
                try (Statement dropStatement = connection.createStatement()) {
                    dropStatement.execute("ALTER TABLE claims DROP CHECK " + constraintName);
                }
            }
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    ALTER TABLE claims
                    ADD CHECK (status IN ('REQUESTED', 'PROCESSING', 'APPROVED', 'REJECTED', 'SETTLED', 'CANCELLED'))
                    """);
        }
    }

    private static Properties loadProperties() throws DatabaseConnectionException {
        Properties properties = new Properties();

        // Reads database username, password, and URL from resources.
        try (InputStream inputStream = DatabaseInitializer.class
                .getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {

            if (inputStream == null) {
                throw new DatabaseConnectionException("database.properties file was not found.", null);
            }

            properties.load(inputStream);
            return properties;
        } catch (IOException exception) {
            throw new DatabaseConnectionException("Could not read database.properties.", exception);
        }
    }

    private static String loadSchemaSql() throws DatabaseConnectionException {
        // Reads the full schema.sql file from src/main/resources/db.
        try (InputStream inputStream = DatabaseInitializer.class
                .getClassLoader()
                .getResourceAsStream(SCHEMA_FILE)) {

            if (inputStream == null) {
                throw new DatabaseConnectionException("schema.sql file was not found.", null);
            }

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new DatabaseConnectionException("Could not read schema.sql.", exception);
        }
    }
}
