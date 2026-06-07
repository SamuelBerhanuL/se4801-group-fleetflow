package com.fleetflow.exception;

/**
 * Thrown when we try to find something that doesn't exist.
 * Example: getDriverById(99) but driver 99 is not in the database.
 * Results in HTTP 404 Not Found response.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    // Convenience constructor: ResourceNotFoundException("Driver", 99)
    // produces message: "Driver with id 99 not found"
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " with id " + id + " not found");
    }
}
