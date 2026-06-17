package com.fleetflow;

import com.fleetflow.dto.VehicleDTO;
import com.fleetflow.entity.Vehicle;
import com.fleetflow.entity.VehicleStatus;
import com.fleetflow.exception.DuplicateResourceException;
import com.fleetflow.exception.ResourceNotFoundException;
import com.fleetflow.repository.VehicleRepository;
import com.fleetflow.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle mockVehicle;
    private VehicleDTO vehicleDTO;

    @BeforeEach
    void setUp() {
        mockVehicle = Vehicle.builder()
                .id(1L)
                .plateNumber("AA-12345")
                .model("Isuzu NPR")
                .capacityKg(BigDecimal.valueOf(3000))
                .status(VehicleStatus.ACTIVE)
                .build();

        vehicleDTO = new VehicleDTO();
        vehicleDTO.setPlateNumber("AA-12345");
        vehicleDTO.setModel("Isuzu NPR");
        vehicleDTO.setCapacityKg(BigDecimal.valueOf(3000));
    }

    @Test
    void createVehicle_success() {
        when(vehicleRepository.existsByPlateNumber(anyString()))
                .thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class)))
                .thenReturn(mockVehicle);

        VehicleDTO result = vehicleService.createVehicle(vehicleDTO);

        assertNotNull(result);
        assertEquals("AA-12345", result.getPlateNumber());
        assertEquals(VehicleStatus.ACTIVE, result.getStatus());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void createVehicle_throwsException_whenPlateAlreadyExists() {
        when(vehicleRepository.existsByPlateNumber("AA-12345"))
                .thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> vehicleService.createVehicle(vehicleDTO));

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void getVehicleById_success() {
        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(mockVehicle));

        VehicleDTO result = vehicleService.getVehicleById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("AA-12345", result.getPlateNumber());
    }

    @Test
    void getVehicleById_throwsException_whenNotFound() {
        when(vehicleRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> vehicleService.getVehicleById(99L));
    }

    @Test
    void getActiveVehicles_returnsOnlyActiveOnes() {
        when(vehicleRepository.findByStatus(VehicleStatus.ACTIVE))
                .thenReturn(List.of(mockVehicle));

        List<VehicleDTO> result = vehicleService.getActiveVehicles();

        assertEquals(1, result.size());
        assertEquals(VehicleStatus.ACTIVE, result.get(0).getStatus());
    }

    @Test
    void getActiveVehicles_returnsEmpty_whenNoneActive() {
        when(vehicleRepository.findByStatus(VehicleStatus.ACTIVE))
                .thenReturn(List.of());

        List<VehicleDTO> result = vehicleService.getActiveVehicles();

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteVehicle_success() {
        when(vehicleRepository.existsById(1L)).thenReturn(true);
        doNothing().when(vehicleRepository).deleteById(1L);

        assertDoesNotThrow(() -> vehicleService.deleteVehicle(1L));
        verify(vehicleRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteVehicle_throwsException_whenNotFound() {
        when(vehicleRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> vehicleService.deleteVehicle(99L));
    }
}
