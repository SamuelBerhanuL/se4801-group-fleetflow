package com.fleetflow.repository;

import com.fleetflow.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * CustomerRepository — database operations for Customer.
 * JpaRepository gives save(), findById(), findAll(),
 * deleteById() for free.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Search customers by name (case-insensitive, partial match)
    // e.g. findByFullNameContainingIgnoreCase("john")
    // returns all customers whose name contains "john"
    List<Customer> findByFullNameContainingIgnoreCase(String name);
}

