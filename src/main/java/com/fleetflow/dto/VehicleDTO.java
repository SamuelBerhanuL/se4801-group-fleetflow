package com.fleetflow.dto;

import com.fleetflow.entity.Vehicle;
import com.fleetflow.entity.VehicleStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * VehicleDTO — shape of vehicle data sent to/from the API.
 */
@Data
public class VehicleDTO {

    private Long id;

    @NotBlank(message = "Plate number is required")
    private String plateNumber;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Capacity is required")
    private java.math.BigDecimal capacityKg;

    private VehicleStatus status;

    public static VehicleDTO fromEntity(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setId(vehicle.getId());
        dto.setPlateNumber(vehicle.getPlateNumber());
        dto.setModel(vehicle.getModel());
        dto.setCapacityKg(vehicle.getCapacityKg());
        dto.setStatus(vehicle.getStatus());
        return dto;
    }
}
