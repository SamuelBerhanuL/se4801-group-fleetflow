package com.fleetflow.controller;

import com.fleetflow.dto.LoginRequest;
import com.fleetflow.dto.LoginResponse;
import com.fleetflow.dto.RegisterRequest;
import com.fleetflow.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController — handles HTTP requests for auth endpoints.
 *
 * POST /api/auth/register  → create a new account
 * POST /api/auth/login     → get a JWT token
 *
 * These 2 endpoints are PUBLIC (no token needed).
 * Defined as public in SecurityConfig.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ── Register ──────────────────────────────────────────────────────────
    // POST /api/auth/register
    // Body: { "email": "...", "password": "...", "role": "DRIVER" }
    // Returns: 201 Created + success message
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {
        String message = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    // ── Login ─────────────────────────────────────────────────────────────
    // POST /api/auth/login
    // Body: { "email": "...", "password": "..." }
    // Returns: 200 OK + { token, email, role, message }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
