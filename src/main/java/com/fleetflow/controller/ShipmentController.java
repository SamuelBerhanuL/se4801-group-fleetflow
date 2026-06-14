package com.fleetflow.controller;

import com.fleetflow.dto.CreateShipmentRequest;
import com.fleetflow.dto.ShipmentDTO;
import com.fleetflow.entity.ShipmentStatus;
import com.fleetflow.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ShipmentController — handles HTTP requests for /api/shipments
 *
 * POST   /api/shipments                    → create shipment   (DISPATCHER)
 * GET    /api/shipments                    → all, paginated    (ADMIN, DISPATCHER)
 * GET    /api/shipments/{id}               → one shipment      (ADMIN, DISPATCHER)
 * GET    /api/shipments/tracking/{code}    → by tracking code  (ADMIN, DISPATCHER)
 * GET    /api/shipments/customer/{id}      → by customer       (ADMIN, DISPATCHER)
 * GET    /api/shipments/status/{status}    → filter by status  (ADMIN, DISPATCHER)
 * PUT    /api/shipments/{id}/status        → update status     (ADMIN, DISPATCHER)
 */
@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<ShipmentDTO> create(
            @Valid @RequestBody CreateShipmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(shipmentService.createShipment(request));
    }

    // Paginated list — default 10 per page sorted by createdAt desc
    // Call like: GET /api/shipments?page=0&size=10&sort=createdAt,desc
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<Page<ShipmentDTO>> getAll(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(shipmentService.getAllShipments(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER', 'DRIVER')")
    public ResponseEntity<ShipmentDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.getShipmentById(id));
    }

    // Track by tracking code — e.g. GET /api/shipments/tracking/FF-A1B2C3D4
    @GetMapping("/tracking/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER', 'DRIVER')")
    public ResponseEntity<ShipmentDTO> getByTrackingCode(
            @PathVariable String code) {
        return ResponseEntity.ok(shipmentService.getByTrackingCode(code));
    }

    // All shipments for one customer
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<Page<ShipmentDTO>> getByCustomer(
            @PathVariable Long customerId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(
                shipmentService.getShipmentsByCustomer(customerId, pageable));
    }

    // Filter shipments by status
    // e.g. GET /api/shipments/status/PENDING
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<Page<ShipmentDTO>> getByStatus(
            @PathVariable ShipmentStatus status,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(
                shipmentService.getShipmentsByStatus(status, pageable));
    }

    // Update shipment status
    // Body: { "status": "PICKED_UP" }
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER', 'DRIVER')")
    public ResponseEntity<ShipmentDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        return ResponseEntity.ok(shipmentService.updateStatus(id, newStatus));
    }
}
