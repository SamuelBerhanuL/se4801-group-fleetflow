package com.fleetflow.service;

import com.fleetflow.dto.LoginRequest;
import com.fleetflow.dto.LoginResponse;
import com.fleetflow.dto.RegisterRequest;
import com.fleetflow.entity.User;
import com.fleetflow.exception.DuplicateResourceException;
import com.fleetflow.repository.UserRepository;
import com.fleetflow.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthService — handles register and login business logic.
 *
 * register(): saves a new user with a hashed password
 * login():    verifies credentials and returns a JWT token
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    // ── Register ──────────────────────────────────────────────────────────
    @Transactional
    public String register(RegisterRequest request) {

        // Check if email is already taken
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                "Email already registered: " + request.getEmail());
        }

        // Build and save the new user
        // Password is hashed with BCrypt — NEVER stored as plain text
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .build();

        userRepository.save(user);
        log.info("New user registered: {} with role {}", 
                 request.getEmail(), request.getRole());

        return "User registered successfully";
    }

    // ── Login ─────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        // AuthenticationManager checks email + password for us
        // If wrong → throws BadCredentialsException → 401 response
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // If we reach here, credentials are correct
        // Load user to get their role for the response
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        // Generate JWT token for this user
        String token = jwtUtil.generateToken(user.getEmail());

        log.info("User logged in: {}", user.getEmail());

        return new LoginResponse(
                token,
                user.getEmail(),
                user.getRole(),
                "Login successful"
        );
    }
}
