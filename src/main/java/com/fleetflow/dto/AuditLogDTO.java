package com.fleetflow.dto;

import com.fleetflow.entity.AuditLog;
import com.fleetflow.entity.ShipmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AuditLogDTO — shape of audit log data returned in API responses.
 * Shows the full history of status changes for a shipment.
 */
@Data
public class AuditLogDTO {

    private Long id;
    private Long shipmentId;
    private String trackingCode;
    private String actor;           // email of who made the change
    private ShipmentStatus oldStatus;
    private ShipmentStatus newStatus;
    private LocalDateTime changedAt;

    public static AuditLogDTO fromEntity(AuditLog log) {
        AuditLogDTO dto = new AuditLogDTO();
        dto.setId(log.getId());
        dto.setShipmentId(log.getShipment().getId());
        dto.setTrackingCode(log.getShipment().getTrackingCode());
        dto.setActor(log.getActor());
        dto.setOldStatus(log.getOldStatus());
        dto.setNewStatus(log.getNewStatus());
        dto.setChangedAt(log.getChangedAt());
        return dto;
    }
}
