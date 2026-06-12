package com.fleetflow.service;

import com.fleetflow.dto.WarehouseDTO;
import com.fleetflow.entity.Warehouse;
import com.fleetflow.exception.ResourceNotFoundException;
import com.fleetflow.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * WarehouseService — all warehouse business logic lives here.
 * The controller calls this. This calls the repository.
 * Never put business logic in the controller.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    // ── Create ────────────────────────────────────────────────────────────
    @Transactional
    public WarehouseDTO createWarehouse(WarehouseDTO dto) {
        Warehouse warehouse = Warehouse.builder()
                .name(dto.getName())
                .city(dto.getCity())
                .address(dto.getAddress())
                .contactPhone(dto.getContactPhone())
                .build();

        Warehouse saved = warehouseRepository.save(warehouse);
        log.info("Warehouse created: {}", saved.getName());
        return WarehouseDTO.fromEntity(saved);
    }

    // ── Get All ───────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<WarehouseDTO> getAllWarehouses() {
        return warehouseRepository.findAll()
                .stream()
                .map(WarehouseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ── Get By Id ─────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public WarehouseDTO getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", id));
        return WarehouseDTO.fromEntity(warehouse);
    }

    // ── Update ────────────────────────────────────────────────────────────
    @Transactional
    public WarehouseDTO updateWarehouse(Long id, WarehouseDTO dto) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", id));

        warehouse.setName(dto.getName());
        warehouse.setCity(dto.getCity());
        warehouse.setAddress(dto.getAddress());
        warehouse.setContactPhone(dto.getContactPhone());

        return WarehouseDTO.fromEntity(warehouseRepository.save(warehouse));
    }

    // ── Delete ────────────────────────────────────────────────────────────
    @Transactional
    public void deleteWarehouse(Long id) {
        if (!warehouseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Warehouse", id);
        }
        warehouseRepository.deleteById(id);
        log.info("Warehouse deleted: id={}", id);
    }
}
