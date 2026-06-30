package com.fleetflow.dto;

import com.fleetflow.entity.Driver;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DriverDTO — shape of driver data sent to/from the API.
 *
 * When CREATING a driver the request sends: userId, fullName,
 * licenseNumber, phone.
 *
 * When READING a driver the response includes: id, userId,
 * userEmail, fullName, licenseNumber, phone, available.
 */
@Data
public class DriverDTO {

    private Long id;

    // The user account this driver is linked to
    @NotNull(message = "User ID is required")
    private Long userId;

    // Included in responses so caller knows the driver's email
    private String userEmail;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @NotBlank(message = "Phone is required")
    private String phone;

    private Boolean available;

    // Converts Driver entity → DriverDTO
    public static DriverDTO fromEntity(Driver driver) {
        DriverDTO dto = new DriverDTO();
        dto.setId(driver.getId());
        dto.setUserId(driver.getUser().getId());
        dto.setUserEmail(driver.getUser().getEmail());
        dto.setFullName(driver.getFullName());
        dto.setLicenseNumber(driver.getLicenseNumber());
        dto.setPhone(driver.getPhone());
        dto.setAvailable(driver.getAvailable());
        return dto;
    }
}
