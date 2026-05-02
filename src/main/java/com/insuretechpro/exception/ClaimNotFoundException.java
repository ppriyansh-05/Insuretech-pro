package com.insuretechpro.exception;

/**
 * This exception is thrown when a claim ID is not found.
 * It helps the service layer report claim-related errors clearly.
 */
public class ClaimNotFoundException extends Exception {
    public ClaimNotFoundException(String message) {
        super(message);
    }
}
