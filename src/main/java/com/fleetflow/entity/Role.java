package com.fleetflow.entity;

/**
 * Role — defines what each user is allowed to do in the system.
 *
 * Used in User.java as the 'role' field.
 * Stored in the database as a String: "ADMIN", "DISPATCHER", "DRIVER"
 *
 * Permissions summary:
 *
 * ADMIN
 *   - Create/manage users, drivers, vehicles, warehouses, routes
 *   - View all shipments and reports
 *   - Full access to everything
 *
 * DISPATCHER
 *   - Assign deliveries (create shipments)
 *   - Assign drivers and vehicles to shipments
 *   - Update shipment statuses
 *   - View all shipments
 *
 * DRIVER
 *   - View their own assigned shipments only
 *   - Update status of their own shipments
 *   - Cannot see other drivers' shipments
 */
public enum Role {
    ADMIN,
    DISPATCHER,
    DRIVER
}
