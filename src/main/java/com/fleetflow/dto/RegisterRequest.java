package com.fleetflow.dto;

import com.fleetflow.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * RegisterRequest — the shape of the JSON body sent to POST /api/auth/register
 *
 * Example request body:
 * {
 *   "email": "driver@fleetflow.com",
 *   "password": "securepass123",
 *   "role": "DRIVER"
 * }
 *
 * The @Valid annotation on the controller parameter triggers
 * these validation rules automatically before the method runs.
 * If any rule fails, GlobalExceptionHandler catches it and
 * returns a 400 with a list of which fields are invalid.
 *
 * @Data (Lombok) = generates getters, setters, toString, equals, hashCode
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Role is required")
    private Role role;  // Must be: ADMIN, DISPATCHER, or DRIVER
}
