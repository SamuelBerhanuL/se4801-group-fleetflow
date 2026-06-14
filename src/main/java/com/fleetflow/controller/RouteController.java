package com.fleetflow.controller;

import com.fleetflow.dto.RouteDTO;
import com.fleetflow.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RouteController — handles HTTP requests for /api/routes
 *
 * GET    /api/routes                        → all routes        (ADMIN, DISPATCHER)
 * GET    /api/routes/{id}                   → one route         (ADMIN, DISPATCHER)
 * GET    /api/routes/warehouse/{id}         → routes by warehouse(ADMIN, DISPATCHER)
 * POST   /api/routes                        → create route      (ADMIN only)
 * PUT    /api/routes/{id}                   → update route      (ADMIN only)
 * DELETE /api/routes/{id}                   → delete route      (ADMIN only)
 */
@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<RouteDTO>> getAll() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<RouteDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.getRouteById(id));
    }

    @GetMapping("/warehouse/{warehouseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<RouteDTO>> getByWarehouse(
            @PathVariable Long warehouseId) {
        return ResponseEntity.ok(
                routeService.getRoutesByWarehouse(warehouseId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RouteDTO> create(
            @Valid @RequestBody RouteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(routeService.createRoute(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RouteDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody RouteDTO dto) {
        return ResponseEntity.ok(routeService.updateRoute(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }
}
