package com.fleetflow.repository;

import com.fleetflow.entity.Vehicle;
import com.fleetflow.entity.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * VehicleRepository — database operations for Vehicle.
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // Get all vehicles with a specific status
    // e.g. findByStatus(ACTIVE) → all vehicles ready to use
    List<Vehicle> findByStatus(VehicleStatus status);

    // Check if a plate number is already registered
    boolean existsByPlateNumber(String plateNumber);
}
