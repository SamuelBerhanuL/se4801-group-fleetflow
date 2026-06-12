package com.fleetflow.controller;

import com.fleetflow.dto.WarehouseDTO;
import com.fleetflow.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * WarehouseController — receives HTTP requests for /api/warehouses
 *
 * GET    /api/warehouses       → get all warehouses (ADMIN, DISPATCHER)
 * GET    /api/warehouses/{id}  → get one warehouse  (ADMIN, DISPATCHER)
 * POST   /api/warehouses       → create warehouse   (ADMIN only)
 * PUT    /api/warehouses/{id}  → update warehouse   (ADMIN only)
 * DELETE /api/warehouses/{id}  → delete warehouse   (ADMIN only)
 *
 * @PreAuthorize checks the role BEFORE the method runs.
 * If wrong role → 403 Forbidden (GlobalExceptionHandler handles it)
 */
@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<WarehouseDTO>> getAll() {
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<WarehouseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getWarehouseById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WarehouseDTO> create(
            @Valid @RequestBody WarehouseDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(warehouseService.createWarehouse(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WarehouseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody WarehouseDTO dto) {
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }
}
