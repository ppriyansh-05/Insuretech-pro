package com.insuretechpro.exception;

/**
 * This exception is thrown when a policy ID is not found.
 * It makes error handling clear and meaningful.
 */
public class PolicyNotFoundException extends Exception {
    public PolicyNotFoundException(String message) {
        super(message);
    }
}
