package com.fleetflow.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthFilter — runs on EVERY incoming HTTP request.
 *
 * WHAT IT DOES:
 * Checks if the request has a valid JWT token in the
 * Authorization header. If yes, it tells Spring Security
 * who the user is so they can access protected endpoints.
 *
 * HOW IT WORKS (step by step):
 *
 * Request comes in →
 *   1. Read the Authorization header
 *      Example: "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIi..."
 *
 *   2. Extract the token part (remove "Bearer ")
 *
 *   3. Validate the token using JwtUtil
 *      - Is it signed with our secret key? ✓
 *      - Is it expired? ✓
 *
 *   4. Extract the email from the token
 *
 *   5. Load the full user from database by email
 *
 *   6. Tell Spring Security "this request is authenticated as this user"
 *
 *   7. Continue to the controller
 *
 * If any step fails (no token, expired, invalid):
 *   → Spring Security blocks the request with 401 Unauthorized
 *
 * OncePerRequestFilter = guaranteed to run exactly once per request
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Step 1 & 2: Get JWT token from request header
            String jwt = getJwtFromRequest(request);

            // Step 3: Validate the token
            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {

                // Step 4: Get email from token
                String email = jwtUtil.getEmailFromToken(jwt);

                // Step 5: Load user details from database
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                // Step 6: Create authentication object and set it
                // in Spring Security's context for this request
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                // Tell Spring Security: this request is authenticated
                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        // Step 7: Continue to the next filter / controller
        filterChain.doFilter(request, response);
    }

    // ── Helper: extract token from "Bearer <token>" header ────────────────
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // Check header exists and starts with "Bearer "
        if (StringUtils.hasText(bearerToken)
                && bearerToken.startsWith("Bearer ")) {
            // Return just the token part (remove "Bearer " prefix)
            return bearerToken.substring(7);
        }
        return null;
    }
}
