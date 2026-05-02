package com.insuretechpro.database;

import com.insuretechpro.exception.DatabaseConnectionException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DatabaseConnection creates JDBC connections for the project.
 * Other classes will use this class instead of writing connection code again.
 */
public class DatabaseConnection {
    private static final String PROPERTIES_FILE = "database.properties";

    private DatabaseConnection() {
        // Private constructor prevents creating objects of this utility class.
    }

    public static Connection getConnection() throws DatabaseConnectionException {
        Properties properties = loadProperties();

        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

        try {
            // Loading the driver explicitly makes the JDBC flow easy to explain.
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException exception) {
            throw new DatabaseConnectionException("MySQL JDBC driver was not found.", exception);
        } catch (SQLException exception) {
            throw new DatabaseConnectionException("Database connection failed.", exception);
        }
    }

    private static Properties loadProperties() throws DatabaseConnectionException {
        Properties properties = new Properties();

        // ClassLoader reads database.properties from src/main/resources.
        try (InputStream inputStream = DatabaseConnection.class
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
}
