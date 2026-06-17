package com.fleetflow.repository;

import com.fleetflow.entity.Shipment;
import com.fleetflow.entity.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ShipmentRepository — database operations for Shipment.
 *
 * Page<Shipment> means the result is PAGINATED.
 * Instead of returning ALL shipments at once (could be thousands),
 * we return a page of 10, 20 etc. at a time.
 * The caller sends: ?page=0&size=10 in the request URL.
 */
@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    // Find shipments by status with pagination
    // e.g. all PENDING shipments, 10 per page
    Page<Shipment> findByStatus(ShipmentStatus status, Pageable pageable);

    // Find all shipments for one customer with pagination
    Page<Shipment> findByCustomerId(Long customerId, Pageable pageable);

    // Find a shipment by its tracking code
    // Customers use this to track their order
    Optional<Shipment> findByTrackingCode(String trackingCode);

    // Find all shipments assigned to a specific driver
    Page<Shipment> findByDriverId(Long driverId, Pageable pageable);
}
