package com.fleetflow.service;

import com.fleetflow.dto.RouteDTO;
import com.fleetflow.entity.Route;
import com.fleetflow.entity.Warehouse;
import com.fleetflow.exception.ResourceNotFoundException;
import com.fleetflow.repository.RouteRepository;
import com.fleetflow.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RouteService — all route business logic lives here.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RouteService {

    private final RouteRepository routeRepository;
    private final WarehouseRepository warehouseRepository;

    // ── Create ────────────────────────────────────────────────────────────
    @Transactional
    public RouteDTO createRoute(RouteDTO dto) {
        // Fetch the warehouse this route belongs to
        Warehouse warehouse = warehouseRepository
                .findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Warehouse", dto.getWarehouseId()));

        Route route = Route.builder()
                .warehouse(warehouse)
                .name(dto.getName())
                .originCity(dto.getOriginCity())
                .destinationCity(dto.getDestinationCity())
                .estimatedHours(dto.getEstimatedHours())
                .build();

        Route saved = routeRepository.save(route);
        log.info("Route created: {}", saved.getName());
        return RouteDTO.fromEntity(saved);
    }

    // ── Get All ───────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<RouteDTO> getAllRoutes() {
        return routeRepository.findAll()
                .stream()
                .map(RouteDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ── Get By Id ─────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public RouteDTO getRouteById(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", id));
        return RouteDTO.fromEntity(route);
    }

    // ── Get Routes By Warehouse ───────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<RouteDTO> getRoutesByWarehouse(Long warehouseId) {
        return routeRepository.findByWarehouseId(warehouseId)
                .stream()
                .map(RouteDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ── Update ────────────────────────────────────────────────────────────
    @Transactional
    public RouteDTO updateRoute(Long id, RouteDTO dto) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", id));

        // Update warehouse if changed
        if (!route.getWarehouse().getId().equals(dto.getWarehouseId())) {
            Warehouse warehouse = warehouseRepository
                    .findById(dto.getWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Warehouse", dto.getWarehouseId()));
            route.setWarehouse(warehouse);
        }

        route.setName(dto.getName());
        route.setOriginCity(dto.getOriginCity());
        route.setDestinationCity(dto.getDestinationCity());
        route.setEstimatedHours(dto.getEstimatedHours());

        return RouteDTO.fromEntity(routeRepository.save(route));
    }

    // ── Delete ────────────────────────────────────────────────────────────
    @Transactional
    public void deleteRoute(Long id) {
        if (!routeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Route", id);
        }
        routeRepository.deleteById(id);
        log.info("Route deleted: id={}", id);
    }
}
