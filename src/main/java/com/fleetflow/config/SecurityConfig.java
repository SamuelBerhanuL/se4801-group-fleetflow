package com.fleetflow.config;

import com.fleetflow.security.JwtAuthFilter;
import com.fleetflow.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig — the main security configuration for the app.
 *
 * THIS IS WHERE WE DEFINE:
 * 1. Which endpoints are PUBLIC (no token needed)
 * 2. Which endpoints are PROTECTED (need a valid token)
 * 3. Which endpoints need a specific ROLE
 * 4. How passwords are hashed (BCrypt)
 * 5. Where our JWT filter plugs in
 *
 * PUBLIC endpoints (anyone can call):
 *   POST /api/auth/register  → create an account
 *   POST /api/auth/login     → get a token
 *   GET  /swagger-ui.html    → API documentation
 *   GET  /api-docs           → OpenAPI spec
 *
 * PROTECTED endpoints (need Authorization: Bearer <token>):
 *   Everything else
 *
 * @Configuration   = this class provides Spring beans (@Bean methods)
 * @EnableWebSecurity = activates Spring Security
 * @EnableMethodSecurity = enables @PreAuthorize on controller methods
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    // ── Password Encoder ──────────────────────────────────────────────────
    // BCrypt is a one-way hashing algorithm for passwords.
    // "password123" → "$2a$10$abc123..." (can never be reversed)
    // When logging in, BCrypt re-hashes the input and COMPARES.
    // strength=10 means it does 2^10 = 1024 hashing rounds (secure)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    // ── Authentication Provider ───────────────────────────────────────────
    // Tells Spring Security HOW to authenticate:
    // Use our UserDetailsService to load users
    // Use BCrypt to check passwords
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ── Authentication Manager ────────────────────────────────────────────
    // Used by AuthService to trigger the login process
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ── Security Filter Chain ─────────────────────────────────────────────
    // The main security rules for all HTTP requests
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — not needed for REST APIs with JWT
            // CSRF is only needed for browser form-based apps
            .csrf(AbstractHttpConfigurer::disable)

            // Define which URLs need authentication
            .authorizeHttpRequests(auth -> auth

                // PUBLIC — no token needed for these
                .requestMatchers(
                    "/api/auth/**",       // login and register
                    "/swagger-ui/**",     // Swagger UI page
                    "/swagger-ui.html",   // Swagger UI redirect
                    "/api-docs/**",       // OpenAPI JSON spec
                    "/v3/api-docs/**"     // OpenAPI v3 spec
                ).permitAll()

                // EVERYTHING ELSE requires a valid JWT token
                .anyRequest().authenticated()
            )

            // Use STATELESS sessions — no server-side sessions.
            // Every request must carry its own JWT token.
            // This is required for proper REST API design.
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Register our authentication provider
            .authenticationProvider(authenticationProvider())

            // Add our JWT filter BEFORE Spring's default login filter
            // So JWT is checked first on every request
            .addFilterBefore(jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
