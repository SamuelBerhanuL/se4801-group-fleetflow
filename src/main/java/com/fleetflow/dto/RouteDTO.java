package com.fleetflow.dto;

import com.fleetflow.entity.Route;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * RouteDTO — shape of route data sent to/from the API.
 *
 * When CREATING a route the request sends warehouseId.
 * Service fetches the actual Warehouse entity by that ID.
 */
@Data
public class RouteDTO {

    private Long id;

    // ID of the warehouse this route starts from
    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    // Included in responses so caller knows warehouse name
    private String warehouseName;

    @NotBlank(message = "Route name is required")
    private String name;

    @NotBlank(message = "Origin city is required")
    private String originCity;

    @NotBlank(message = "Destination city is required")
    private String destinationCity;

    @NotNull(message = "Estimated hours is required")
    @Min(value = 1, message = "Must be at least 1 hour")
    private Integer estimatedHours;

    public static RouteDTO fromEntity(Route route) {
        RouteDTO dto = new RouteDTO();
        dto.setId(route.getId());
        dto.setWarehouseId(route.getWarehouse().getId());
        dto.setWarehouseName(route.getWarehouse().getName());
        dto.setName(route.getName());
        dto.setOriginCity(route.getOriginCity());
        dto.setDestinationCity(route.getDestinationCity());
        dto.setEstimatedHours(route.getEstimatedHours());
        return dto;
    }
}
