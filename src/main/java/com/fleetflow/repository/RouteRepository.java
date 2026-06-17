package com.fleetflow.repository;

import com.fleetflow.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RouteRepository — database operations for Route.
 */
@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    // Find all routes that originate from a specific warehouse
    List<Route> findByWarehouseId(Long warehouseId);

    // Find routes by destination city
    List<Route> findByDestinationCityIgnoreCase(String city);
}
