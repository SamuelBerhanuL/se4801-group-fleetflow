package com.fleetflow.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler — catches ALL errors thrown anywhere in the app
 * and returns a clean, consistent JSON response instead of a messy stack trace.
 *
 * Without this, if something goes wrong Spring returns a confusing
 * HTML error page or a raw Java exception. With this, every error
 * returns a nice JSON like:
 * {
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "Driver with id 5 not found",
 *   "timestamp": "2026-06-04T10:30:00"
 * }
 *
 * @RestControllerAdvice = applies to all @RestController classes
 * @Slf4j = gives us the log.error() method (from Lombok)
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ── Helper: builds a standard error response map ──────────────────────
    private Map<String, Object> buildError(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now().toString());
        return body;
    }

    // ── 404: Resource Not Found ───────────────────────────────────────────
    // Thrown when: getDriverById(99) but driver 99 doesn't exist
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildError(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    // ── 400: Bad Request ──────────────────────────────────────────────────
    // Thrown when: business rule is violated
    // e.g. trying to change shipment status from DELIVERED back to PENDING
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        log.error("Bad request: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    // ── 409: Conflict ─────────────────────────────────────────────────────
    // Thrown when: trying to register with an email that already exists
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateResourceException ex) {
        log.error("Duplicate resource: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(buildError(HttpStatus.CONFLICT, ex.getMessage()));
    }

    // ── 401: Unauthorized ─────────────────────────────────────────────────
    // Thrown when: wrong password during login
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        log.error("Authentication failed: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildError(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
    }

    // ── 403: Forbidden ────────────────────────────────────────────────────
    // Thrown when: a DRIVER tries to call an ADMIN-only endpoint
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        log.error("Access denied: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(buildError(HttpStatus.FORBIDDEN, "You do not have permission to perform this action"));
    }

    // ── 400: Validation Failed ────────────────────────────────────────────
    // Thrown when: request body fails @NotBlank, @Email etc. validation
    // Returns a map of which fields failed and why
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError err : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(err.getField(), err.getDefaultMessage());
        }
        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("error", "Validation Failed");
        body.put("errors", fieldErrors);
        body.put("timestamp", LocalDateTime.now().toString());
        log.error("Validation failed: {}", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    // ── 500: Unexpected Error ─────────────────────────────────────────────
    // Catches anything else we didn't predict
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        log.error("Unexpected error: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred. Please try again."));
    }
}
