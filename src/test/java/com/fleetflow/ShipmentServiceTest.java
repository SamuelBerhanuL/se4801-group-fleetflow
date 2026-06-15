package com.fleetflow;

import com.fleetflow.dto.CreateShipmentRequest;
import com.fleetflow.dto.ShipmentDTO;
import com.fleetflow.entity.*;
import com.fleetflow.exception.BadRequestException;
import com.fleetflow.exception.ResourceNotFoundException;
import com.fleetflow.repository.*;
import com.fleetflow.service.AuditLogService;
import com.fleetflow.service.ShipmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

    @Mock private ShipmentRepository shipmentRepository;
    @Mock private DriverRepository driverRepository;
    @Mock private VehicleRepository vehicleRepository;
    @Mock private RouteRepository routeRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private WarehouseRepository warehouseRepository;
    @Mock private AuditLogService auditLogService;

    @InjectMocks
    private ShipmentService shipmentService;

    private Driver mockDriver;
    private Vehicle mockVehicle;
    private Route mockRoute;
    private Customer mockCustomer;
    private Warehouse mockWarehouse;
    private Shipment mockShipment;
    private CreateShipmentRequest createRequest;

    @BeforeEach
    void setUp() {
        User mockUser = User.builder()
                .id(1L).email("driver@fleetflow.com")
                .role(Role.DRIVER).active(true)
                .passwordHash("hashed").build();

        mockDriver = Driver.builder()
                .id(1L).user(mockUser)
                .fullName("Abebe Tadesse")
                .licenseNumber("ET-DL-12345")
                .phone("0911111111").available(true).build();

        mockVehicle = Vehicle.builder()
                .id(1L).plateNumber("AA-12345")
                .model("Isuzu NPR")
                .capacityKg(java.math.BigDecimal.valueOf(3000))
                .status(VehicleStatus.ACTIVE).build();

        mockWarehouse = Warehouse.builder()
                .id(1L).name("Bole Warehouse")
                .city("Addis Ababa").address("Bole Road")
                .contactPhone("0911000000").build();

        mockRoute = Route.builder()
                .id(1L).warehouse(mockWarehouse)
                .name("Addis to Hawassa")
                .originCity("Addis Ababa")
                .destinationCity("Hawassa")
                .estimatedHours(4).build();

        mockCustomer = Customer.builder()
                .id(1L).fullName("Abebe Kebede")
                .phone("0922222222").build();

        mockShipment = Shipment.builder()
                .id(1L).driver(mockDriver).vehicle(mockVehicle)
                .route(mockRoute).customer(mockCustomer)
                .originWarehouse(mockWarehouse)
                .trackingCode("FF-A1B2C3D4")
                .status(ShipmentStatus.PENDING)
                .weight(100.0).build();

        createRequest = new CreateShipmentRequest();
        createRequest.setDriverId(1L);
        createRequest.setVehicleId(1L);
        createRequest.setRouteId(1L);
        createRequest.setCustomerId(1L);
        createRequest.setOriginWarehouseId(1L);
        createRequest.setWeight(100.0);
        createRequest.setDescription("Test shipment");
    }

    // ── createShipment tests ──────────────────────────────────────────────

    @Test
    void createShipment_success_generatesTrackingCode() {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(mockDriver));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(mockVehicle));
        when(routeRepository.findById(1L)).thenReturn(Optional.of(mockRoute));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(mockWarehouse));
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(mockShipment);
        doNothing().when(auditLogService).log(any(), any(), any(), any());

        ShipmentDTO result = shipmentService.createShipment(createRequest);

        assertNotNull(result);
        assertNotNull(result.getTrackingCode());
        assertEquals(ShipmentStatus.PENDING, result.getStatus());
        verify(shipmentRepository, times(1)).save(any(Shipment.class));
    }

    @Test
    void createShipment_throwsException_whenDriverNotFound() {
        when(driverRepository.findById(99L)).thenReturn(Optional.empty());
        createRequest.setDriverId(99L);

        assertThrows(ResourceNotFoundException.class,
                () -> shipmentService.createShipment(createRequest));
    }

    // ── getShipmentById tests ─────────────────────────────────────────────

    @Test
    void getShipmentById_success() {
        when(shipmentRepository.findById(1L))
                .thenReturn(Optional.of(mockShipment));

        ShipmentDTO result = shipmentService.getShipmentById(1L);

        assertNotNull(result);
        assertEquals("FF-A1B2C3D4", result.getTrackingCode());
    }

    @Test
    void getShipmentById_throwsException_whenNotFound() {
        when(shipmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> shipmentService.getShipmentById(99L));
    }

    // ── updateStatus tests ────────────────────────────────────────────────

    @Test
    void updateStatus_success_fromPendingToPickedUp() {
        mockShipment.setStatus(ShipmentStatus.PENDING);
        when(shipmentRepository.findById(1L))
                .thenReturn(Optional.of(mockShipment));
        when(shipmentRepository.save(any(Shipment.class)))
                .thenReturn(mockShipment);
        doNothing().when(auditLogService).log(any(), any(), any(), any());

        ShipmentDTO result = shipmentService.updateStatus(1L, "PICKED_UP");

        assertNotNull(result);
        verify(auditLogService, times(1)).log(any(), any(), any(), any());
    }

    @Test
    void updateStatus_throwsException_whenDeliveredAlready() {
        mockShipment.setStatus(ShipmentStatus.DELIVERED);
        when(shipmentRepository.findById(1L))
                .thenReturn(Optional.of(mockShipment));

        assertThrows(BadRequestException.class,
                () -> shipmentService.updateStatus(1L, "PENDING"));
    }

    @Test
    void updateStatus_throwsException_whenGoingBackwards() {
        mockShipment.setStatus(ShipmentStatus.IN_TRANSIT);
        when(shipmentRepository.findById(1L))
                .thenReturn(Optional.of(mockShipment));

        assertThrows(BadRequestException.class,
                () -> shipmentService.updateStatus(1L, "PENDING"));
    }

    @Test
    void updateStatus_success_cancelFromAnyState() {
        mockShipment.setStatus(ShipmentStatus.IN_TRANSIT);
        when(shipmentRepository.findById(1L))
                .thenReturn(Optional.of(mockShipment));
        when(shipmentRepository.save(any(Shipment.class)))
                .thenReturn(mockShipment);
        doNothing().when(auditLogService).log(any(), any(), any(), any());

        assertDoesNotThrow(
                () -> shipmentService.updateStatus(1L, "CANCELLED"));
    }

    // ── getAllShipments pagination test ───────────────────────────────────

    @Test
    void getAllShipments_returnsPaginatedResult() {
        Page<Shipment> page = new PageImpl<>(List.of(mockShipment));
        when(shipmentRepository.findAll(any(PageRequest.class)))
                .thenReturn(page);

        Page<ShipmentDTO> result = shipmentService.getAllShipments(
                PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }
}
