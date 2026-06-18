package com.fleetflow.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * CreateShipmentRequest — the shape of JSON sent to POST /api/shipments
 *
 * Example request body:
 * {
 *   "driverId": 1,
 *   "vehicleId": 1,
 *   "routeId": 1,
 *   "customerId": 1,
 *   "originWarehouseId": 1,
 *   "weight": 250.5,
 *   "description": "Electronics - handle with care"
 * }
 *
 * The dispatcher fills in all the IDs.
 * ShipmentService fetches the actual entities by those IDs.
 */
@Data
public class CreateShipmentRequest {

    @NotNull(message = "Driver ID is required")
    private Long driverId;

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotNull(message = "Route ID is required")
    private Long routeId;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Origin warehouse ID is required")
    private Long originWarehouseId;

    @Min(value = 0, message = "Weight cannot be negative")
    private java.math.BigDecimal weight;

    private String description;
}

