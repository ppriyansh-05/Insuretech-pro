package com.insuretechpro.model;

/**
 * User represents a person who can log in to the application.
 * This model is used by the JavaFX login screen.
 */
public class User {
    // Private fields keep login data encapsulated.
    private long userId;
    private String username;
    private String fullName;
    private Long referenceId;
    private String role;
    private String status;

    public User(long userId, String username, String fullName, Long referenceId, String role, String status) {
        // Constructor creates a logged-in user object.
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.referenceId = referenceId;
        this.role = role;
        this.status = status;
    }

    public long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }
}
