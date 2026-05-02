package com.insuretechpro.exception;

/**
 * This exception is thrown when a claim breaks business rules.
 * Example: claim amount is greater than policy coverage.
 */
public class InvalidClaimException extends Exception {
    public InvalidClaimException(String message) {
        super(message);
    }
}
