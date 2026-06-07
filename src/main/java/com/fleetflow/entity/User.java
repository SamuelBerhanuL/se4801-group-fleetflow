package com.fleetflow.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * User — represents anyone who can LOG IN to the system.
 *
 * Every person (admin, dispatcher, driver) has a User account.
 * The 'role' field determines what they are allowed to do.
 *
 * This class maps to the 'users' table that Flyway created
 * in V1__init.sql. Every field here = one column in that table.
 *
 * Annotations explained:
 * @Entity        = tells Spring "this class is a database table"
 * @Table         = specifies the exact table name in the database
 * @Id            = this field is the primary key
 * @GeneratedValue = database auto-generates the id (1, 2, 3...)
 * @Column        = maps field to a specific column name
 * @CreationTimestamp = auto-fills with current time when saved
 *
 * Lombok annotations (saves us writing boilerplate):
 * @Getter    = generates getgetId(), getEmail(), getRole() etc.
 * @Setter    = generates setId(), setEmail() etc.
 * @Builder   = lets us build objects like: User.builder().email("x").build()
 * @NoArgsConstructor = generates empty constructor (required by JPA)
 * @AllArgsConstructor = generates constructor with all fields
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    // Primary key — auto-incremented by PostgreSQL (1, 2, 3...)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Login email — must be unique across all users
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // Password stored as BCrypt hash — NEVER plain text
    // Example hash: "$2a$10$abc123..." (60 characters)
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // Role determines permissions:
    // ADMIN      → can do everything
    // DISPATCHER → can assign deliveries
    // DRIVER     → can only update their own shipments
    // @Enumerated stores the enum as a String ("ADMIN") not a number
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    // Soft delete flag — instead of deleting users we set active=false
    // This preserves history (audit logs still reference the user)
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    // Auto-filled with the current timestamp when the row is inserted
    // We never set this manually — Hibernate does it automatically
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
