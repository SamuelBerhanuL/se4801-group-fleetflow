package com.fleetflow.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Driver — a person who physically delivers shipments.
 * Every Driver has exactly ONE User account (for login).
 * The user field links to the users table via user_id foreign key.
 *
 * Relationship:
 * Driver → User is @OneToOne
 * One driver has exactly one user login account.
 * If the User is deleted, the Driver is also deleted (CASCADE).
 */
@Entity
@Table(name = "drivers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Links this Driver to a User login account
    // @OneToOne = one driver has exactly one user account
    // @JoinColumn = the foreign key column name in the drivers table
    // fetch = EAGER means when we load a Driver, User loads too
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    // Unique driving licence ID
    @Column(name = "license_number", nullable = false, unique = true)
    private String licenseNumber;

    @Column(name = "phone", nullable = false)
    private String phone;

    // true = driver is free to take a delivery
    // false = driver is currently on a delivery
    @Column(name = "available", nullable = false)
    @Builder.Default
    private Boolean available = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
