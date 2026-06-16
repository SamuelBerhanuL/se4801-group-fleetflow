package com.fleetflow.repository;

import com.fleetflow.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * DriverRepository — database operations for Driver.
 */
@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    // Get all drivers who are free to take a delivery
    List<Driver> findByAvailableTrue();

    // Find driver by their linked user id
    // Used to check if a user already has a driver profile
    Optional<Driver> findByUserId(Long userId);

    // Check if a licence number is already registered
    boolean existsByLicenseNumber(String licenseNumber);
}
