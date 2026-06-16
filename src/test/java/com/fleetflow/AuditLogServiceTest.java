package com.fleetflow;

import com.fleetflow.dto.AuditLogDTO;
import com.fleetflow.entity.*;
import com.fleetflow.exception.ResourceNotFoundException;
import com.fleetflow.repository.AuditLogRepository;
import com.fleetflow.repository.ShipmentRepository;
import com.fleetflow.service.AuditLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private ShipmentRepository shipmentRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    private Shipment mockShipment;
    private AuditLog mockLog;

    @BeforeEach
    void setUp() {
        User mockUser = User.builder()
                .id(1L).email("driver@fleetflow.com")
                .role(Role.DRIVER).active(true)
                .passwordHash("hashed").build();

        Driver mockDriver = Driver.builder()
                .id(1L).user(mockUser)
                .fullName("Abebe").licenseNumber("ET-001")
                .phone("0911111111").available(true).build();

        Vehicle mockVehicle = Vehicle.builder()
                .id(1L).plateNumber("AA-001").model("Isuzu")
                .capacityKg(java.math.BigDecimal.valueOf(3000))
                .status(VehicleStatus.ACTIVE).build();

        Warehouse mockWarehouse = Warehouse.builder()
                .id(1L).name("Bole Warehouse")
                .city("Addis Ababa").address("Bole Road")
                .contactPhone("0911000000").build();

        Route mockRoute = Route.builder()
                .id(1L).warehouse(mockWarehouse)
                .name("Addis to Hawassa")
                .originCity("Addis Ababa")
                .destinationCity("Hawassa")
                .estimatedHours(4).build();

        Customer mockCustomer = Customer.builder()
                .id(1L).fullName("Abebe Kebede")
                .phone("0922222222").build();

        mockShipment = Shipment.builder()
                .id(1L).driver(mockDriver).vehicle(mockVehicle)
                .route(mockRoute).customer(mockCustomer)
                .originWarehouse(mockWarehouse)
                .trackingCode("FF-A1B2C3D4")
                .status(ShipmentStatus.PENDING).build();

        mockLog = AuditLog.builder()
                .id(1L).shipment(mockShipment)
                .actor("dispatcher@fleetflow.com")
                .oldStatus(ShipmentStatus.PENDING)
                .newStatus(ShipmentStatus.PICKED_UP)
                .build();
    }

    @Test
    void log_createsAuditEntry_withCorrectFields() {
        when(auditLogRepository.save(any(AuditLog.class)))
                .thenReturn(mockLog);

        auditLogService.log(
                mockShipment,
                ShipmentStatus.PENDING,
                ShipmentStatus.PICKED_UP,
                "dispatcher@fleetflow.com"
        );

        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void getLogsForShipment_returnsLogsInOrder() {
        when(shipmentRepository.existsById(1L)).thenReturn(true);
        when(auditLogRepository.findByShipmentIdOrderByChangedAtAsc(1L))
                .thenReturn(List.of(mockLog));

        List<AuditLogDTO> result =
                auditLogService.getLogsForShipment(1L);

        assertEquals(1, result.size());
        assertEquals("dispatcher@fleetflow.com", result.get(0).getActor());
        assertEquals(ShipmentStatus.PENDING, result.get(0).getOldStatus());
        assertEquals(ShipmentStatus.PICKED_UP, result.get(0).getNewStatus());
    }

    @Test
    void getLogsForShipment_throwsException_whenShipmentNotFound() {
        when(shipmentRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> auditLogService.getLogsForShipment(99L));
    }

    @Test
    void log_worksWithNullOldStatus_forNewShipment() {
        when(auditLogRepository.save(any(AuditLog.class)))
                .thenReturn(mockLog);

        // Old status is null when shipment is first created
        assertDoesNotThrow(() -> auditLogService.log(
                mockShipment,
                null,
                ShipmentStatus.PENDING,
                "dispatcher@fleetflow.com"
        ));
    }
}
