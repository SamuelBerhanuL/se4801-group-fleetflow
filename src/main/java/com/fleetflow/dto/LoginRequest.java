package com.fleetflow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * LoginRequest — the shape of the JSON body sent to POST /api/auth/login
 *
 * Example request body:
 * {
 *   "email": "admin@fleetflow.com",
 *   "password": "admin123"
 * }
 */
@Data
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
