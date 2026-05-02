package com.insuretechpro.exception;

/**
 * This exception is thrown when a repository fails during database work.
 * Example: insert fails, search fails, or SQL has an error.
 */
public class RepositoryException extends Exception {
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
