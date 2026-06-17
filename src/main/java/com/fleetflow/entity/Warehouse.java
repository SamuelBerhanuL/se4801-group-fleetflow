package com.fleetflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Warehouse — a physical location where goods are stored.
 * Shipments originate FROM a warehouse.
 * Routes are assigned TO a warehouse.
 * Maps to the 'warehouses' table created in V1__init.sql
 */
@Entity
@Table(name = "warehouses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Column(name = "city", nullable = false)
    private String city;

    @NotBlank
    @Column(name = "address", nullable = false)
    private String address;

    @NotBlank
    @Column(name = "contact_phone", nullable = false)
    private String contactPhone;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
