package com.fleetflow.entity;

/**
 * ShipmentStatus — the 5 possible states of a shipment.
 *
 * Valid flow:
 * PENDING → PICKED_UP → IN_TRANSIT → DELIVERED
 * Any status → CANCELLED
 *
 * You CANNOT go backwards.
 * e.g. DELIVERED → PENDING is not allowed.
 * ShipmentService enforces this rule.
 */
public enum ShipmentStatus {
    PENDING,
    PICKED_UP,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED
}
