package com.fleetflow;

import com.fleetflow.dto.DriverDTO;
import com.fleetflow.entity.Driver;
import com.fleetflow.entity.Role;
import com.fleetflow.entity.User;
import com.fleetflow.exception.DuplicateResourceException;
import com.fleetflow.exception.ResourceNotFoundException;
import com.fleetflow.repository.DriverRepository;
import com.fleetflow.repository.UserRepository;
import com.fleetflow.service.DriverService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DriverService driverService;

    private User mockUser;
    private Driver mockDriver;
    private DriverDTO driverDTO;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .email("driver@fleetflow.com")
                .passwordHash("hashed")
                .role(Role.DRIVER)
                .active(true)
                .build();

        mockDriver = Driver.builder()
                .id(1L)
                .user(mockUser)
                .fullName("Abebe Tadesse")
                .licenseNumber("ET-DL-12345")
                .phone("0911234567")
                .available(true)
                .build();

        driverDTO = new DriverDTO();
        driverDTO.setUserId(1L);
        driverDTO.setFullName("Abebe Tadesse");
        driverDTO.setLicenseNumber("ET-DL-12345");
        driverDTO.setPhone("0911234567");
    }

    // ── createDriver tests ────────────────────────────────────────────────

    @Test
    void createDriver_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(driverRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(driverRepository.existsByLicenseNumber(anyString())).thenReturn(false);
        when(driverRepository.save(any(Driver.class))).thenReturn(mockDriver);

        DriverDTO result = driverService.createDriver(driverDTO);

        assertNotNull(result);
        assertEquals("Abebe Tadesse", result.getFullName());
        assertEquals("ET-DL-12345", result.getLicenseNumber());
        verify(driverRepository, times(1)).save(any(Driver.class));
    }

    @Test
    void createDriver_throwsException_whenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> driverService.createDriver(driverDTO));

        verify(driverRepository, never()).save(any());
    }

    @Test
    void createDriver_throwsException_whenDriverAlreadyExistsForUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(driverRepository.findByUserId(1L))
                .thenReturn(Optional.of(mockDriver));

        assertThrows(DuplicateResourceException.class,
                () -> driverService.createDriver(driverDTO));
    }

    @Test
    void createDriver_throwsException_whenLicenseAlreadyRegistered() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(driverRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(driverRepository.existsByLicenseNumber("ET-DL-12345"))
                .thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> driverService.createDriver(driverDTO));
    }

    // ── getDriverById tests ───────────────────────────────────────────────

    @Test
    void getDriverById_success() {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(mockDriver));

        DriverDTO result = driverService.getDriverById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Abebe Tadesse", result.getFullName());
    }

    @Test
    void getDriverById_throwsException_whenNotFound() {
        when(driverRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> driverService.getDriverById(99L));
    }

    // ── getAvailableDrivers tests ─────────────────────────────────────────

    @Test
    void getAvailableDrivers_returnsOnlyAvailableOnes() {
        when(driverRepository.findByAvailableTrue())
                .thenReturn(List.of(mockDriver));

        List<DriverDTO> result = driverService.getAvailableDrivers();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getAvailable());
    }

    @Test
    void getAvailableDrivers_returnsEmptyList_whenNoneAvailable() {
        when(driverRepository.findByAvailableTrue()).thenReturn(List.of());

        List<DriverDTO> result = driverService.getAvailableDrivers();

        assertTrue(result.isEmpty());
    }

    // ── toggleAvailability tests ──────────────────────────────────────────

    @Test
    void toggleAvailability_switchesFromTrueToFalse() {
        mockDriver.setAvailable(true);
        when(driverRepository.findById(1L)).thenReturn(Optional.of(mockDriver));
        when(driverRepository.save(any(Driver.class))).thenReturn(mockDriver);

        DriverDTO result = driverService.toggleAvailability(1L);

        // After toggle, available should be false
        verify(driverRepository).save(argThat(d -> !d.getAvailable()));
    }

    // ── deleteDriver tests ────────────────────────────────────────────────

    @Test
    void deleteDriver_success() {
        when(driverRepository.existsById(1L)).thenReturn(true);
        doNothing().when(driverRepository).deleteById(1L);

        assertDoesNotThrow(() -> driverService.deleteDriver(1L));
        verify(driverRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteDriver_throwsException_whenNotFound() {
        when(driverRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> driverService.deleteDriver(99L));
    }
}
