package com.fleetflow.controller;

import com.fleetflow.dto.DriverDTO;
import com.fleetflow.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * DriverController — handles HTTP requests for /api/drivers
 *
 * GET    /api/drivers            → all drivers       (ADMIN, DISPATCHER)
 * GET    /api/drivers/{id}       → one driver        (ADMIN, DISPATCHER)
 * GET    /api/drivers/available  → available drivers (ADMIN, DISPATCHER)
 * POST   /api/drivers            → create driver     (ADMIN only)
 * PUT    /api/drivers/{id}       → update driver     (ADMIN only)
 * PATCH  /api/drivers/{id}/toggle→ toggle available  (ADMIN, DISPATCHER)
 * DELETE /api/drivers/{id}       → delete driver     (ADMIN only)
 */
@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<DriverDTO>> getAll() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<DriverDTO>> getAvailable() {
        return ResponseEntity.ok(driverService.getAvailableDrivers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<DriverDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriverDTO> create(
            @Valid @RequestBody DriverDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(driverService.createDriver(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriverDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody DriverDTO dto) {
        return ResponseEntity.ok(driverService.updateDriver(id, dto));
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<DriverDTO> toggleAvailability(
            @PathVariable Long id) {
        return ResponseEntity.ok(driverService.toggleAvailability(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }
}
