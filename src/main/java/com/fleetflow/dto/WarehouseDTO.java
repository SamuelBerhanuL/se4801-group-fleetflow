package com.fleetflow.dto;

import com.fleetflow.entity.Warehouse;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * WarehouseDTO — the shape of warehouse data sent to/from the API.
 *
 * WHY NOT RETURN THE ENTITY DIRECTLY?
 * Entities are tied to the database. If we return them directly,
 * any change to the database breaks the API response.
 * DTOs give us full control over what we expose.
 *
 * fromEntity() converts a Warehouse entity into this DTO.
 * Used in the service layer before returning data to the controller.
 */
@Data
public class WarehouseDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Contact phone is required")
    private String contactPhone;

    // Converts entity → DTO
    // Called like: WarehouseDTO.fromEntity(warehouse)
    public static WarehouseDTO fromEntity(Warehouse warehouse) {
        WarehouseDTO dto = new WarehouseDTO();
        dto.setId(warehouse.getId());
        dto.setName(warehouse.getName());
        dto.setCity(warehouse.getCity());
        dto.setAddress(warehouse.getAddress());
        dto.setContactPhone(warehouse.getContactPhone());
        return dto;
    }
}
