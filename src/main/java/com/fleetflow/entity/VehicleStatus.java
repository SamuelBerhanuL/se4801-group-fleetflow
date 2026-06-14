package com.fleetflow.entity;

/**
 * VehicleStatus — the two possible states of a vehicle.
 *
 * ACTIVE      = vehicle is ready to be assigned to a delivery
 * MAINTENANCE = vehicle is being repaired, cannot be assigned
 */
public enum VehicleStatus {
    ACTIVE,
    MAINTENANCE
}
