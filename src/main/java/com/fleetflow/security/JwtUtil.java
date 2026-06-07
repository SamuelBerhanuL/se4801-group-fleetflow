package com.fleetflow.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JwtUtil — generates and validates JWT tokens.
 *
 * HOW JWT WORKS (simple version):
 *
 * 1. User logs in with email + password
 * 2. We verify the password is correct
 * 3. We call generateToken(email) → produces a long string like:
 *    "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbi..."
 * 4. We send that string back to the user
 * 5. User stores it and sends it with every future request:
 *    Header: Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
 * 6. Our JwtAuthFilter calls validateToken() on every request
 *    to confirm the token is genuine and not expired
 *
 * The token contains:
 *   - subject (sub)  = the user's email
 *   - issued at (iat) = when it was created
 *   - expiration (exp) = when it expires (24 hours)
 *
 * It is SIGNED with our secret key so nobody can fake it.
 *
 * @Component = Spring manages this as a singleton bean
 * @Slf4j     = gives us log.error(), log.info() etc. (Lombok)
 * @Value     = reads values from application.properties
 */
@Component
@Slf4j
public class JwtUtil {

    // Read from application.properties:
    // jwt.secret=fleetflow-super-secret-key-...
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Read from application.properties:
    // jwt.expiration=86400000  (24 hours in milliseconds)
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // ── Generate a signing key from our secret string ──────────────────────
    // This key is used to sign and verify every token
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // ── Generate a new JWT token for a logged-in user ─────────────────────
    // Called by AuthService after verifying password is correct
    // Returns the token string to send back to the user
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(email)          // who the token belongs to
                .setIssuedAt(now)           // when it was created
                .setExpiration(expiryDate)  // when it expires
                .signWith(getSigningKey())  // sign it with our secret key
                .compact();                 // build the final string
    }

    // ── Extract the email from a token ────────────────────────────────────
    // Called by JwtAuthFilter to know WHICH user is making the request
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();  // the email we stored in setSubject()
    }

    // ── Validate a token ─────────────────────────────────────────────────
    // Returns true if token is valid and not expired
    // Returns false if token is fake, expired, or tampered with
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("JWT token is malformed: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT token is empty or null: {}", e.getMessage());
        }
        return false;
    }
}
