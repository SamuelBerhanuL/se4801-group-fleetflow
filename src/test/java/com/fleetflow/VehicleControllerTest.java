package com.fleetflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleetflow.dto.VehicleDTO;
import com.fleetflow.entity.VehicleStatus;
import com.fleetflow.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VehicleService vehicleService;

    private VehicleDTO sampleVehicle() {
        VehicleDTO dto = new VehicleDTO();
        dto.setId(1L);
        dto.setPlateNumber("AA-12345");
        dto.setModel("Isuzu NPR");
        dto.setCapacityKg(BigDecimal.valueOf(3000));
        dto.setStatus(VehicleStatus.ACTIVE);
        return dto;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_returns200() throws Exception {
        when(vehicleService.getAllVehicles()).thenReturn(List.of(sampleVehicle()));

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].plateNumber").value("AA-12345"));
    }

    @Test
    void getAll_returns403_whenNoAuth() throws Exception {
        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DISPATCHER")
    void getActive_returns200() throws Exception {
        when(vehicleService.getActiveVehicles()).thenReturn(List.of(sampleVehicle()));

        mockMvc.perform(get("/api/vehicles/active"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_returns200() throws Exception {
        when(vehicleService.getVehicleById(1L)).thenReturn(sampleVehicle());

        mockMvc.perform(get("/api/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("Isuzu NPR"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_returns201_forAdmin() throws Exception {
        when(vehicleService.createVehicle(any(VehicleDTO.class)))
                .thenReturn(sampleVehicle());

        mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleVehicle())))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void create_returns403_forDriver() throws Exception {
        mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleVehicle())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_returns200() throws Exception {
        when(vehicleService.updateVehicle(anyLong(), any(VehicleDTO.class)))
                .thenReturn(sampleVehicle());

        mockMvc.perform(put("/api/vehicles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleVehicle())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/vehicles/1"))
                .andExpect(status().isNoContent());
    }
}
