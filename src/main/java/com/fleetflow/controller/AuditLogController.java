package com.fleetflow.controller;

import com.fleetflow.dto.AuditLogDTO;
import com.fleetflow.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AuditLogController — handles HTTP requests for audit logs.
 *
 * GET /api/shipments/{id}/logs → full history of one shipment
 */
@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    // Get full status change history for a shipment
    // e.g. GET /api/shipments/5/logs
    // Returns: list of who changed what status and when
    @GetMapping("/{shipmentId}/logs")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<AuditLogDTO>> getLogs(
            @PathVariable Long shipmentId) {
        return ResponseEntity.ok(
                auditLogService.getLogsForShipment(shipmentId));
    }
}
