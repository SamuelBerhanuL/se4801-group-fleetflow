package com.fleetflow;

import com.fleetflow.dto.WarehouseDTO;
import com.fleetflow.entity.Warehouse;
import com.fleetflow.exception.ResourceNotFoundException;
import com.fleetflow.repository.WarehouseRepository;
import com.fleetflow.service.WarehouseService;
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
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private WarehouseService warehouseService;

    private Warehouse mockWarehouse;
    private WarehouseDTO warehouseDTO;

    @BeforeEach
    void setUp() {
        mockWarehouse = Warehouse.builder()
                .id(1L)
                .name("Bole Warehouse")
                .city("Addis Ababa")
                .address("Bole Road, Addis Ababa")
                .contactPhone("0911234567")
                .build();

        warehouseDTO = new WarehouseDTO();
        warehouseDTO.setName("Bole Warehouse");
        warehouseDTO.setCity("Addis Ababa");
        warehouseDTO.setAddress("Bole Road, Addis Ababa");
        warehouseDTO.setContactPhone("0911234567");
    }

    @Test
    void createWarehouse_success() {
        when(warehouseRepository.save(any(Warehouse.class)))
                .thenReturn(mockWarehouse);

        WarehouseDTO result = warehouseService.createWarehouse(warehouseDTO);

        assertNotNull(result);
        assertEquals("Bole Warehouse", result.getName());
        assertEquals("Addis Ababa", result.getCity());
        verify(warehouseRepository, times(1)).save(any(Warehouse.class));
    }

    @Test
    void getWarehouseById_success() {
        when(warehouseRepository.findById(1L))
                .thenReturn(Optional.of(mockWarehouse));

        WarehouseDTO result = warehouseService.getWarehouseById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Bole Warehouse", result.getName());
    }

    @Test
    void getWarehouseById_throwsException_whenNotFound() {
        when(warehouseRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> warehouseService.getWarehouseById(99L));
    }

    @Test
    void getAllWarehouses_returnsAll() {
        when(warehouseRepository.findAll())
                .thenReturn(List.of(mockWarehouse));

        List<WarehouseDTO> result = warehouseService.getAllWarehouses();

        assertEquals(1, result.size());
        assertEquals("Bole Warehouse", result.get(0).getName());
    }

    @Test
    void updateWarehouse_success() {
        when(warehouseRepository.findById(1L))
                .thenReturn(Optional.of(mockWarehouse));
        when(warehouseRepository.save(any(Warehouse.class)))
                .thenReturn(mockWarehouse);

        warehouseDTO.setName("Updated Warehouse");
        WarehouseDTO result = warehouseService.updateWarehouse(1L, warehouseDTO);

        assertNotNull(result);
        verify(warehouseRepository, times(1)).save(any(Warehouse.class));
    }

    @Test
    void updateWarehouse_throwsException_whenNotFound() {
        when(warehouseRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> warehouseService.updateWarehouse(99L, warehouseDTO));
    }

    @Test
    void deleteWarehouse_success() {
        when(warehouseRepository.existsById(1L)).thenReturn(true);
        doNothing().when(warehouseRepository).deleteById(1L);

        assertDoesNotThrow(() -> warehouseService.deleteWarehouse(1L));
        verify(warehouseRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteWarehouse_throwsException_whenNotFound() {
        when(warehouseRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> warehouseService.deleteWarehouse(99L));
    }
}
