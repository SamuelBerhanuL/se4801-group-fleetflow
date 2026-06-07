package com.fleetflow.exception;

/**
 * Thrown when trying to create something that already exists.
 * Example: registering with an email that is already in the database.
 * Results in HTTP 409 Conflict response.
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
