package com.fleetflow.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Vehicle — a truck or van used to carry shipments.
 * Maps to the 'vehicles' table created in V1__init.sql
 *
 * Status:
 *   ACTIVE      = ready to be assigned to a delivery
 *   MAINTENANCE = being repaired, cannot be assigned
 */
@Entity
@Table(name = "vehicles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique licence plate — e.g. "AA-12345"
    @Column(name = "plate_number", nullable = false, unique = true)
    private String plateNumber;

    // Make and model — e.g. "Isuzu NPR"
    @Column(name = "model", nullable = false)
    private String model;


    @Column(name = "capacity_kg", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal capacityKg;

    // ACTIVE or MAINTENANCE — stored as String in DB
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private VehicleStatus status = VehicleStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
