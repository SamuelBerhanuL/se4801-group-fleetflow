package com.fleetflow.service;

import com.fleetflow.dto.VehicleDTO;
import com.fleetflow.entity.Vehicle;
import com.fleetflow.entity.VehicleStatus;
import com.fleetflow.exception.DuplicateResourceException;
import com.fleetflow.exception.ResourceNotFoundException;
import com.fleetflow.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * VehicleService — all vehicle business logic lives here.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    // ── Create ────────────────────────────────────────────────────────────
    @Transactional
    public VehicleDTO createVehicle(VehicleDTO dto) {
        if (vehicleRepository.existsByPlateNumber(dto.getPlateNumber())) {
            throw new DuplicateResourceException(
                    "Plate number already registered: " + dto.getPlateNumber());
        }

        Vehicle vehicle = Vehicle.builder()
                .plateNumber(dto.getPlateNumber())
                .model(dto.getModel())
                .capacityKg(dto.getCapacityKg())
                .status(VehicleStatus.ACTIVE)
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("Vehicle created: {}", saved.getPlateNumber());
        return VehicleDTO.fromEntity(saved);
    }

    // ── Get All ───────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<VehicleDTO> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(VehicleDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ── Get By Id ─────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public VehicleDTO getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));
        return VehicleDTO.fromEntity(vehicle);
    }

    // ── Get Active Vehicles ───────────────────────────────────────────────
    // Returns only vehicles with status = ACTIVE
    // Dispatchers use this when assigning a vehicle to a shipment
    @Transactional(readOnly = true)
    public List<VehicleDTO> getActiveVehicles() {
        return vehicleRepository.findByStatus(VehicleStatus.ACTIVE)
                .stream()
                .map(VehicleDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ── Update ────────────────────────────────────────────────────────────
    @Transactional
    public VehicleDTO updateVehicle(Long id, VehicleDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));

        // Only check plate uniqueness if it changed
        if (!vehicle.getPlateNumber().equals(dto.getPlateNumber())) {
            if (vehicleRepository.existsByPlateNumber(dto.getPlateNumber())) {
                throw new DuplicateResourceException(
                        "Plate number already registered: " + dto.getPlateNumber());
            }
            vehicle.setPlateNumber(dto.getPlateNumber());
        }

        vehicle.setModel(dto.getModel());
        vehicle.setCapacityKg(dto.getCapacityKg());

        // Allow status update (ACTIVE ↔ MAINTENANCE)
        if (dto.getStatus() != null) {
            vehicle.setStatus(dto.getStatus());
        }

        return VehicleDTO.fromEntity(vehicleRepository.save(vehicle));
    }

    // ── Delete ────────────────────────────────────────────────────────────
    @Transactional
    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle", id);
        }
        vehicleRepository.deleteById(id);
        log.info("Vehicle deleted: id={}", id);
    }
}
