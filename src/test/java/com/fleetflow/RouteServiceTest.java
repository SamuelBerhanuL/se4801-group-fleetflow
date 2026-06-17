package com.fleetflow;

import com.fleetflow.dto.RouteDTO;
import com.fleetflow.entity.Route;
import com.fleetflow.entity.Warehouse;
import com.fleetflow.exception.ResourceNotFoundException;
import com.fleetflow.repository.RouteRepository;
import com.fleetflow.repository.WarehouseRepository;
import com.fleetflow.service.RouteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private RouteService routeService;

    private Warehouse mockWarehouse;
    private Route mockRoute;
    private RouteDTO routeDTO;

    @BeforeEach
    void setUp() {
        mockWarehouse = Warehouse.builder()
                .id(1L)
                .name("Bole Warehouse")
                .city("Addis Ababa")
                .address("Bole Road")
                .contactPhone("0911000000")
                .build();

        mockRoute = Route.builder()
                .id(1L)
                .warehouse(mockWarehouse)
                .name("Addis to Hawassa")
                .originCity("Addis Ababa")
                .destinationCity("Hawassa")
                .estimatedHours(4)
                .build();

        routeDTO = new RouteDTO();
        routeDTO.setWarehouseId(1L);
        routeDTO.setName("Addis to Hawassa");
        routeDTO.setOriginCity("Addis Ababa");
        routeDTO.setDestinationCity("Hawassa");
        routeDTO.setEstimatedHours(4);
    }

    @Test
    void createRoute_success() {
        when(warehouseRepository.findById(1L))
                .thenReturn(Optional.of(mockWarehouse));
        when(routeRepository.save(any(Route.class)))
                .thenReturn(mockRoute);

        RouteDTO result = routeService.createRoute(routeDTO);

        assertNotNull(result);
        assertEquals("Addis to Hawassa", result.getName());
        assertEquals("Hawassa", result.getDestinationCity());
        verify(routeRepository, times(1)).save(any(Route.class));
    }

    @Test
    void createRoute_throwsException_whenWarehouseNotFound() {
        when(warehouseRepository.findById(99L))
                .thenReturn(Optional.empty());
        routeDTO.setWarehouseId(99L);

        assertThrows(ResourceNotFoundException.class,
                () -> routeService.createRoute(routeDTO));

        verify(routeRepository, never()).save(any());
    }

    @Test
    void getRouteById_success() {
        when(routeRepository.findById(1L))
                .thenReturn(Optional.of(mockRoute));

        RouteDTO result = routeService.getRouteById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Addis to Hawassa", result.getName());
    }

    @Test
    void getRouteById_throwsException_whenNotFound() {
        when(routeRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> routeService.getRouteById(99L));
    }

    @Test
    void getAllRoutes_returnsAll() {
        when(routeRepository.findAll())
                .thenReturn(List.of(mockRoute));

        List<RouteDTO> result = routeService.getAllRoutes();

        assertEquals(1, result.size());
        assertEquals("Addis to Hawassa", result.get(0).getName());
    }

    @Test
    void getRoutesByWarehouse_returnsCorrectRoutes() {
        when(routeRepository.findByWarehouseId(1L))
                .thenReturn(List.of(mockRoute));

        List<RouteDTO> result = routeService.getRoutesByWarehouse(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getWarehouseId());
    }

    @Test
    void deleteRoute_success() {
        when(routeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(routeRepository).deleteById(1L);

        assertDoesNotThrow(() -> routeService.deleteRoute(1L));
        verify(routeRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteRoute_throwsException_whenNotFound() {
        when(routeRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> routeService.deleteRoute(99L));
    }
}
