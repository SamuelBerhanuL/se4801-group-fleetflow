package com.fleetflow.dto;

import com.fleetflow.entity.Customer;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * CustomerDTO — shape of customer data sent to/from the API.
 * fromEntity() converts a Customer entity into this DTO.
 */
@Data
public class CustomerDTO {

    private Long id;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    private String address;

    public static CustomerDTO fromEntity(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setFullName(customer.getFullName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setAddress(customer.getAddress());
        return dto;
    }
}
