package com.insuretechpro.exception;

/**
 * This exception is thrown when Java cannot connect to the database.
 * It keeps database errors separate from policy and claim errors.
 */
public class DatabaseConnectionException extends Exception {
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
