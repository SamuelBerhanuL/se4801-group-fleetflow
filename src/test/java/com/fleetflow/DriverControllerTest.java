package com.fleetflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleetflow.dto.DriverDTO;
import com.fleetflow.service.DriverService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * DriverControllerTest — tests the HTTP layer of DriverController.
 *
 * @WithMockUser simulates a logged-in user with a specific role,
 * so we can test @PreAuthorize rules without needing a real JWT token.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DriverService driverService;

    private DriverDTO sampleDriver() {
        DriverDTO dto = new DriverDTO();
        dto.setId(1L);
        dto.setUserId(2L);
        dto.setFullName("Abebe Tadesse");
        dto.setLicenseNumber("ET-DL-001");
        dto.setPhone("0911234567");
        dto.setAvailable(true);
        return dto;
    }

    // ── GET all (ADMIN/DISPATCHER allowed) ────────────────────────────────
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_returns200_forAdmin() throws Exception {
        when(driverService.getAllDrivers()).thenReturn(List.of(sampleDriver()));

        mockMvc.perform(get("/api/drivers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Abebe Tadesse"));
    }

    @Test
    void getAll_returns403_whenNoAuth() throws Exception {
        mockMvc.perform(get("/api/drivers"))
                .andExpect(status().isForbidden());
    }

    // ── GET available ──────────────────────────────────────────────────────
    @Test
    @WithMockUser(roles = "DISPATCHER")
    void getAvailable_returns200() throws Exception {
        when(driverService.getAvailableDrivers()).thenReturn(List.of(sampleDriver()));

        mockMvc.perform(get("/api/drivers/available"))
                .andExpect(status().isOk());
    }

    // ── GET by id ──────────────────────────────────────────────────────────
    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_returns200() throws Exception {
        when(driverService.getDriverById(1L)).thenReturn(sampleDriver());

        mockMvc.perform(get("/api/drivers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licenseNumber").value("ET-DL-001"));
    }

    // ── POST create (ADMIN only) ──────────────────────────────────────────
    @Test
    @WithMockUser(roles = "ADMIN")
    void create_returns201_forAdmin() throws Exception {
        when(driverService.createDriver(any(DriverDTO.class)))
                .thenReturn(sampleDriver());

        mockMvc.perform(post("/api/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleDriver())))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void create_returns403_forDriverRole() throws Exception {
        mockMvc.perform(post("/api/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleDriver())))
                .andExpect(status().isForbidden());
    }

    // ── PUT update (ADMIN only) ────────────────────────────────────────────
    @Test
    @WithMockUser(roles = "ADMIN")
    void update_returns200_forAdmin() throws Exception {
        when(driverService.updateDriver(anyLong(), any(DriverDTO.class)))
                .thenReturn(sampleDriver());

        mockMvc.perform(put("/api/drivers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleDriver())))
                .andExpect(status().isOk());
    }

    // ── PATCH toggle availability ─────────────────────────────────────────
    @Test
    @WithMockUser(roles = "DISPATCHER")
    void toggleAvailability_returns200() throws Exception {
        when(driverService.toggleAvailability(1L)).thenReturn(sampleDriver());

        mockMvc.perform(patch("/api/drivers/1/toggle"))
                .andExpect(status().isOk());
    }

    // ── DELETE (ADMIN only) ────────────────────────────────────────────────
    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_returns204_forAdmin() throws Exception {
        mockMvc.perform(delete("/api/drivers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "DISPATCHER")
    void delete_returns403_forDispatcher() throws Exception {
        mockMvc.perform(delete("/api/drivers/1"))
                .andExpect(status().isForbidden());
    }
}
