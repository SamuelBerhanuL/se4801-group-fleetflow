package com.fleetflow.repository;

import com.fleetflow.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * WarehouseRepository — database operations for Warehouse.
 * JpaRepository gives us save(), findById(), findAll(),
 * deleteById() for free without writing any SQL.
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    // Find all warehouses in a specific city
    // e.g. findByCity("Addis Ababa") → returns all Addis warehouses
    List<Warehouse> findByCity(String city);
}
