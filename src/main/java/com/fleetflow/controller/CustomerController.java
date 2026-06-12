package com.fleetflow.controller;

import com.fleetflow.dto.CustomerDTO;
import com.fleetflow.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CustomerController — handles HTTP requests for /api/customers
 *
 * GET    /api/customers          → get all customers
 * GET    /api/customers/{id}     → get one customer
 * GET    /api/customers/search   → search by name
 * POST   /api/customers          → create customer
 * PUT    /api/customers/{id}     → update customer
 * DELETE /api/customers/{id}     → delete customer
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<CustomerDTO>> getAll() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<CustomerDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<CustomerDTO>> search(
            @RequestParam String name) {
        return ResponseEntity.ok(customerService.searchByName(name));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<CustomerDTO> create(
            @Valid @RequestBody CustomerDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.createCustomer(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<CustomerDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerDTO dto) {
        return ResponseEntity.ok(customerService.updateCustomer(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
