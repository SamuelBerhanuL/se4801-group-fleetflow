package com.fleetflow.service;

import com.fleetflow.dto.CreateShipmentRequest;
import com.fleetflow.dto.ShipmentDTO;
import com.fleetflow.entity.*;
import com.fleetflow.exception.BadRequestException;
import com.fleetflow.exception.ResourceNotFoundException;
import com.fleetflow.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * ShipmentService — the most important service in FleetFlow.
 * Handles creating shipments, updating status, and searching.
 *
 * Key rules enforced here:
 * - Tracking code is auto-generated (dispatcher never types it)
 * - Status can only move FORWARD (no going backwards)
 * - Every status change is logged in AuditLog automatically
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final RouteRepository routeRepository;
    private final CustomerRepository customerRepository;
    private final WarehouseRepository warehouseRepository;
    private final AuditLogService auditLogService;

    // ── Create Shipment ───────────────────────────────────────────────────
    @Transactional
    public ShipmentDTO createShipment(CreateShipmentRequest request) {

        // Fetch all related entities by their IDs
        // Throws ResourceNotFoundException if any ID doesn't exist
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Driver", request.getDriverId()));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle", request.getVehicleId()));

        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Route", request.getRouteId()));

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer", request.getCustomerId()));

        Warehouse warehouse = warehouseRepository
                .findById(request.getOriginWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Warehouse", request.getOriginWarehouseId()));

        // Generate unique tracking code: "FF-" + first 8 chars of UUID
        // Example: "FF-A1B2C3D4"
        String trackingCode = "FF-" + UUID.randomUUID()
                .toString().replace("-", "")
                .substring(0, 8).toUpperCase();

        Shipment shipment = Shipment.builder()
                .driver(driver)
                .vehicle(vehicle)
                .route(route)
                .customer(customer)
                .originWarehouse(warehouse)
                .trackingCode(trackingCode)
                .status(ShipmentStatus.PENDING)
                .weight(request.getWeight())
                .description(request.getDescription())
                .build();

        Shipment saved = shipmentRepository.save(shipment);

        // Log the creation in audit log
        String actor = getCurrentUserEmail();
        auditLogService.log(saved, null, ShipmentStatus.PENDING, actor);

        log.info("Shipment created: {}", trackingCode);
        return ShipmentDTO.fromEntity(saved);
    }

    // ── Get All Shipments (paginated) ─────────────────────────────────────
    // Returns Page<ShipmentDTO> — 10 per page by default
    // Caller sends: GET /api/shipments?page=0&size=10&sort=createdAt,desc
    @Transactional(readOnly = true)
    public Page<ShipmentDTO> getAllShipments(Pageable pageable) {
        return shipmentRepository.findAll(pageable)
                .map(ShipmentDTO::fromEntity);
    }

    // ── Get Shipment By Id ────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public ShipmentDTO getShipmentById(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", id));
        return ShipmentDTO.fromEntity(shipment);
    }

    // ── Get By Tracking Code ──────────────────────────────────────────────
    // Public-facing — customers use this to track their order
    @Transactional(readOnly = true)
    public ShipmentDTO getByTrackingCode(String code) {
        Shipment shipment = shipmentRepository.findByTrackingCode(code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Shipment with tracking code: " + code));
        return ShipmentDTO.fromEntity(shipment);
    }

    // ── Get Shipments By Customer ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public Page<ShipmentDTO> getShipmentsByCustomer(Long customerId,
                                                     Pageable pageable) {
        return shipmentRepository.findByCustomerId(customerId, pageable)
                .map(ShipmentDTO::fromEntity);
    }

    // ── Get Shipments By Status ───────────────────────────────────────────
    @Transactional(readOnly = true)
    public Page<ShipmentDTO> getShipmentsByStatus(ShipmentStatus status,
                                                   Pageable pageable) {
        return shipmentRepository.findByStatus(status, pageable)
                .map(ShipmentDTO::fromEntity);
    }

    // ── Update Shipment Status ────────────────────────────────────────────
    // Enforces forward-only status transitions
    // Automatically creates an AuditLog entry
    @Transactional
    public ShipmentDTO updateStatus(Long id, String newStatusStr) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", id));

        ShipmentStatus newStatus;
        try {
            newStatus = ShipmentStatus.valueOf(newStatusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + newStatusStr +
                    ". Valid values: PENDING, PICKED_UP, IN_TRANSIT, DELIVERED, CANCELLED");
        }

        // Validate status transition — no going backwards
        validateStatusTransition(shipment.getStatus(), newStatus);

        ShipmentStatus oldStatus = shipment.getStatus();
        shipment.setStatus(newStatus);
        Shipment saved = shipmentRepository.save(shipment);

        // Automatically log this status change
        String actor = getCurrentUserEmail();
        auditLogService.log(saved, oldStatus, newStatus, actor);

        log.info("Shipment {} status: {} → {}", 
                 shipment.getTrackingCode(), oldStatus, newStatus);
        return ShipmentDTO.fromEntity(saved);
    }

    // ── Validate Status Transition ────────────────────────────────────────
    // Rules:
    // DELIVERED → nothing (final state, cannot change)
    // CANCELLED → nothing (final state, cannot change)
    // Otherwise must move forward in the flow
    private void validateStatusTransition(ShipmentStatus current,
                                          ShipmentStatus next) {
        if (current == ShipmentStatus.DELIVERED) {
            throw new BadRequestException(
                    "Cannot change status of a DELIVERED shipment");
        }
        if (current == ShipmentStatus.CANCELLED) {
            throw new BadRequestException(
                    "Cannot change status of a CANCELLED shipment");
        }

        // Define the valid forward order
        int currentOrder = getStatusOrder(current);
        int nextOrder = getStatusOrder(next);

        // CANCELLED is always allowed from any non-final state
        if (next == ShipmentStatus.CANCELLED) return;

        // Otherwise must move forward
        if (nextOrder <= currentOrder) {
            throw new BadRequestException(
                    "Invalid status transition: " + current + " → " + next +
                    ". Status must move forward.");
        }
    }

    private int getStatusOrder(ShipmentStatus status) {
        return switch (status) {
            case PENDING   -> 1;
            case PICKED_UP -> 2;
            case IN_TRANSIT-> 3;
            case DELIVERED -> 4;
            case CANCELLED -> 5;
        };
    }

    // ── Get Current Logged-In User Email ──────────────────────────────────
    // Used for audit log — who made this change?
    private String getCurrentUserEmail() {
        try {
            return SecurityContextHolder.getContext()
                    .getAuthentication().getName();
        } catch (Exception e) {
            return "system";
        }
    }
}
