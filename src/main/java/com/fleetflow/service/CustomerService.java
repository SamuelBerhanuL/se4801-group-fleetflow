package com.fleetflow.service;

import com.fleetflow.dto.CustomerDTO;
import com.fleetflow.entity.Customer;
import com.fleetflow.exception.ResourceNotFoundException;
import com.fleetflow.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CustomerService — all customer business logic lives here.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    // ── Create ────────────────────────────────────────────────────────────
    @Transactional
    public CustomerDTO createCustomer(CustomerDTO dto) {
        Customer customer = Customer.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .build();

        Customer saved = customerRepository.save(customer);
        log.info("Customer created: {}", saved.getFullName());
        return CustomerDTO.fromEntity(saved);
    }

    // ── Get All ───────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ── Get By Id ─────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        return CustomerDTO.fromEntity(customer);
    }

    // ── Update ────────────────────────────────────────────────────────────
    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));

        customer.setFullName(dto.getFullName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());

        return CustomerDTO.fromEntity(customerRepository.save(customer));
    }

    // ── Delete ────────────────────────────────────────────────────────────
    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer", id);
        }
        customerRepository.deleteById(id);
        log.info("Customer deleted: id={}", id);
    }

    // ── Search by name ────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<CustomerDTO> searchByName(String name) {
        return customerRepository
                .findByFullNameContainingIgnoreCase(name)
                .stream()
                .map(CustomerDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
