package com.fleetflow;

import com.fleetflow.dto.AuditLogDTO;
import com.fleetflow.entity.ShipmentStatus;
import com.fleetflow.service.AuditLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuditLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuditLogService auditLogService;

    private AuditLogDTO sampleLog() {
        AuditLogDTO dto = new AuditLogDTO();
        dto.setId(1L);
        dto.setShipmentId(1L);
        dto.setTrackingCode("FF-A1B2C3D4");
        dto.setActor("dispatcher@fleetflow.com");
        dto.setOldStatus(ShipmentStatus.PENDING);
        dto.setNewStatus(ShipmentStatus.PICKED_UP);
        return dto;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getLogs_returns200() throws Exception {
        when(auditLogService.getLogsForShipment(anyLong()))
                .thenReturn(List.of(sampleLog()));

        mockMvc.perform(get("/api/shipments/1/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].actor").value("dispatcher@fleetflow.com"))
                .andExpect(jsonPath("$[0].newStatus").value("PICKED_UP"));
    }

    @Test
    @WithMockUser(roles = "DISPATCHER")
    void getLogs_returns200_forDispatcher() throws Exception {
        when(auditLogService.getLogsForShipment(anyLong()))
                .thenReturn(List.of(sampleLog()));

        mockMvc.perform(get("/api/shipments/1/logs"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void getLogs_returns403_forDriver() throws Exception {
        mockMvc.perform(get("/api/shipments/1/logs"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getLogs_returns403_whenNoAuth() throws Exception {
        mockMvc.perform(get("/api/shipments/1/logs"))
                .andExpect(status().isForbidden());
    }
}
