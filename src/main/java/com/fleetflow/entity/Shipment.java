package com.fleetflow.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Shipment — the CORE entity of the whole FleetFlow system.
 * Represents one delivery job from warehouse to customer.
 *
 * It links together 5 other entities:
 *   driver          → who is delivering it
 *   vehicle         → what truck is carrying it
 *   route           → which path it follows
 *   customer        → who ordered it
 *   originWarehouse → where it starts from
 *
 * Maps to the 'shipments' table in V1__init.sql
 *
 * @ManyToOne = many shipments can use the same driver/vehicle etc.
 * @JoinColumn = the foreign key column name in the shipments table
 * fetch=LAZY = only load related entity when we actually need it
 *              (better performance — don't load everything at once)
 */
@Entity
@Table(name = "shipments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_warehouse_id", nullable = false)
    private Warehouse originWarehouse;

    // Auto-generated unique tracking code
    // e.g. "FF-A1B2C3D4" — customers use this to track their order
    @Column(name = "tracking_code", nullable = false, unique = true)
    private String trackingCode;

    // Current status of the shipment
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ShipmentStatus status = ShipmentStatus.PENDING;

    // Weight of the goods in kilograms
    @Column(name = "weight")
    private java.math.BigDecimal weight;

    // Description of what is being delivered
    @Column(name = "description")
    private String description;

    // Auto-set when created
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Auto-updated every time the row is saved
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
