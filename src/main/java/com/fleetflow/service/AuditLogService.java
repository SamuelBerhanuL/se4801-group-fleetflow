package com.fleetflow.service;

import com.fleetflow.dto.AuditLogDTO;
import com.fleetflow.entity.AuditLog;
import com.fleetflow.entity.Shipment;
import com.fleetflow.entity.ShipmentStatus;
import com.fleetflow.exception.ResourceNotFoundException;
import com.fleetflow.repository.AuditLogRepository;
import com.fleetflow.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AuditLogService — creates and retrieves audit log entries.
 *
 * log() is called automatically by ShipmentService every time
 * a shipment status changes. Loza never calls log() manually.
 * It is triggered internally.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ShipmentRepository shipmentRepository;

    // ── Log a status change ───────────────────────────────────────────────
    // Called by ShipmentService.updateStatus() automatically
    // actor = email of the user who made the change
    @Transactional
    public void log(Shipment shipment,
                    ShipmentStatus oldStatus,
                    ShipmentStatus newStatus,
                    String actor) {

        AuditLog entry = AuditLog.builder()
                .shipment(shipment)
                .actor(actor)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .build();

        auditLogRepository.save(entry);
        log.info("Audit log: {} changed shipment {} from {} to {}",
                actor, shipment.getTrackingCode(), oldStatus, newStatus);
    }

    // ── Get all logs for one shipment ─────────────────────────────────────
    // Returns the full history of status changes for a shipment
    // Ordered from oldest to newest
    @Transactional(readOnly = true)
    public List<AuditLogDTO> getLogsForShipment(Long shipmentId) {
        // Confirm shipment exists first
        if (!shipmentRepository.existsById(shipmentId)) {
            throw new ResourceNotFoundException("Shipment", shipmentId);
        }

        return auditLogRepository
                .findByShipmentIdOrderByChangedAtAsc(shipmentId)
                .stream()
                .map(AuditLogDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
