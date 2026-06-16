package com.fleetflow.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * AuditLog — records every status change on a shipment.
 *
 * Every time a shipment status changes, one AuditLog row is created.
 * This gives a full history of what happened to each shipment.
 *
 * Example log entry:
 *   shipment_id = 5
 *   actor       = "dispatcher@fleetflow.com"
 *   old_status  = "PENDING"
 *   new_status  = "PICKED_UP"
 *   changed_at  = "2026-06-10 09:30:00"
 *
 * Maps to the 'audit_log' table in V1__init.sql
 */
@Entity
@Table(name = "audit_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which shipment this log entry belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    // Email of the user who made the status change
    @Column(name = "actor", nullable = false)
    private String actor;

    // What the status was BEFORE the change
    @Enumerated(EnumType.STRING)
    @Column(name = "old_status")
    private ShipmentStatus oldStatus;

    // What the status changed TO
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private ShipmentStatus newStatus;

    // Auto-set to current time when the log entry is created
    @CreationTimestamp
    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;
}

