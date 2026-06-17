package com.fleetflow.controller;

import com.fleetflow.dto.VehicleDTO;
import com.fleetflow.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * VehicleController — handles HTTP requests for /api/vehicles
 *
 * GET    /api/vehicles         → all vehicles        (ADMIN, DISPATCHER)
 * GET    /api/vehicles/{id}    → one vehicle         (ADMIN, DISPATCHER)
 * GET    /api/vehicles/active  → only active ones    (ADMIN, DISPATCHER)
 * POST   /api/vehicles         → create vehicle      (ADMIN only)
 * PUT    /api/vehicles/{id}    → update vehicle      (ADMIN only)
 * DELETE /api/vehicles/{id}    → delete vehicle      (ADMIN only)
 */
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<VehicleDTO>> getAll() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<VehicleDTO>> getActive() {
        return ResponseEntity.ok(vehicleService.getActiveVehicles());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<VehicleDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleDTO> create(
            @Valid @RequestBody VehicleDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vehicleService.createVehicle(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody VehicleDTO dto) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
