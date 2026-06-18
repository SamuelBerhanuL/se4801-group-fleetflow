package com.fleetflow.dto;

import com.fleetflow.entity.Shipment;
import com.fleetflow.entity.ShipmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ShipmentDTO — shape of shipment data returned in API responses.
 * Contains all fields including related entity names for easy reading.
 */
@Data
public class ShipmentDTO {

    private Long id;
    private String trackingCode;
    private ShipmentStatus status;
    private java.math.BigDecimal weight;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Related entity details (IDs + names for easy display)
    private Long driverId;
    private String driverName;
    private Long vehicleId;
    private String vehiclePlate;
    private Long routeId;
    private String routeName;
    private Long customerId;
    private String customerName;
    private Long originWarehouseId;
    private String originWarehouseName;

    public static ShipmentDTO fromEntity(Shipment s) {
        ShipmentDTO dto = new ShipmentDTO();
        dto.setId(s.getId());
        dto.setTrackingCode(s.getTrackingCode());
        dto.setStatus(s.getStatus());
        dto.setWeight(s.getWeight());
        dto.setDescription(s.getDescription());
        dto.setCreatedAt(s.getCreatedAt());
        dto.setUpdatedAt(s.getUpdatedAt());
        dto.setDriverId(s.getDriver().getId());
        dto.setDriverName(s.getDriver().getFullName());
        dto.setVehicleId(s.getVehicle().getId());
        dto.setVehiclePlate(s.getVehicle().getPlateNumber());
        dto.setRouteId(s.getRoute().getId());
        dto.setRouteName(s.getRoute().getName());
        dto.setCustomerId(s.getCustomer().getId());
        dto.setCustomerName(s.getCustomer().getFullName());
        dto.setOriginWarehouseId(s.getOriginWarehouse().getId());
        dto.setOriginWarehouseName(s.getOriginWarehouse().getName());
        return dto;
    }
}

