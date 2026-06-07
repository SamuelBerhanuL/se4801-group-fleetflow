package com.fleetflow.exception;

/**
 * Thrown when a request violates a business rule.
 * Example: trying to change shipment from DELIVERED back to PENDING.
 * Results in HTTP 400 Bad Request response.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
