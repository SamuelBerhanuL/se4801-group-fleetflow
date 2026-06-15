package com.fleetflow.service;

import com.fleetflow.dto.DriverDTO;
import com.fleetflow.entity.Driver;
import com.fleetflow.entity.User;
import com.fleetflow.exception.BadRequestException;
import com.fleetflow.exception.DuplicateResourceException;
import com.fleetflow.exception.ResourceNotFoundException;
import com.fleetflow.repository.DriverRepository;
import com.fleetflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DriverService — all driver business logic lives here.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;

    // ── Create Driver ─────────────────────────────────────────────────────
    @Transactional
    public DriverDTO createDriver(DriverDTO dto) {

        // Check the user exists
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", dto.getUserId()));

        // Check user doesn't already have a driver profile
        if (driverRepository.findByUserId(dto.getUserId()).isPresent()) {
            throw new DuplicateResourceException(
                    "A driver profile already exists for this user");
        }

        // Check licence number is unique
        if (driverRepository.existsByLicenseNumber(dto.getLicenseNumber())) {
            throw new DuplicateResourceException(
                    "License number already registered: " + dto.getLicenseNumber());
        }

        Driver driver = Driver.builder()
                .user(user)
                .fullName(dto.getFullName())
                .licenseNumber(dto.getLicenseNumber())
                .phone(dto.getPhone())
                .available(true)
                .build();

        Driver saved = driverRepository.save(driver);
        log.info("Driver created: {}", saved.getFullName());
        return DriverDTO.fromEntity(saved);
    }

    // ── Get All Drivers ───────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<DriverDTO> getAllDrivers() {
        return driverRepository.findAll()
                .stream()
                .map(DriverDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ── Get Driver By Id ──────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public DriverDTO getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", id));
        return DriverDTO.fromEntity(driver);
    }

    // ── Get Available Drivers ─────────────────────────────────────────────
    // Returns only drivers who are free (available = true)
    // Dispatchers use this when assigning a delivery
    @Transactional(readOnly = true)
    public List<DriverDTO> getAvailableDrivers() {
        return driverRepository.findByAvailableTrue()
                .stream()
                .map(DriverDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ── Update Driver ─────────────────────────────────────────────────────
    @Transactional
    public DriverDTO updateDriver(Long id, DriverDTO dto) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", id));

        driver.setFullName(dto.getFullName());
        driver.setPhone(dto.getPhone());

        // Only update licence if it changed and new one is not taken
        if (!driver.getLicenseNumber().equals(dto.getLicenseNumber())) {
            if (driverRepository.existsByLicenseNumber(dto.getLicenseNumber())) {
                throw new DuplicateResourceException(
                        "License number already registered: " + dto.getLicenseNumber());
            }
            driver.setLicenseNumber(dto.getLicenseNumber());
        }

        return DriverDTO.fromEntity(driverRepository.save(driver));
    }

    // ── Toggle Availability ───────────────────────────────────────────────
    // Flips driver between available=true and available=false
    @Transactional
    public DriverDTO toggleAvailability(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", id));

        driver.setAvailable(!driver.getAvailable());
        log.info("Driver {} availability set to: {}", 
                 driver.getFullName(), driver.getAvailable());
        return DriverDTO.fromEntity(driverRepository.save(driver));
    }

    // ── Delete Driver ─────────────────────────────────────────────────────
    @Transactional
    public void deleteDriver(Long id) {
        if (!driverRepository.existsById(id)) {
            throw new ResourceNotFoundException("Driver", id);
        }
        driverRepository.deleteById(id);
        log.info("Driver deleted: id={}", id);
    }
}
