package com.fleetflow.dto;

import com.fleetflow.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * LoginResponse — what we send BACK after a successful login.
 *
 * Example response:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1...",
 *   "email": "admin@fleetflow.com",
 *   "role": "ADMIN",
 *   "message": "Login successful"
 * }
 *
 * The 'token' is the JWT token.
 * The user copies this token and puts it in every future request
 * under the Authorization header like:
 *   Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String email;
    private Role role;
    private String message;
}
