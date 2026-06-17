package com.fleetflow.repository;

import com.fleetflow.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AuditLogRepository — database operations for AuditLog.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // Get all log entries for one shipment ordered by time
    // Used to show the full history of a shipment
    List<AuditLog> findByShipmentIdOrderByChangedAtAsc(Long shipmentId);
}

