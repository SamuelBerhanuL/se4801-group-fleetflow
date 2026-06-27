package com.fleetflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleetflow.dto.WarehouseDTO;
import com.fleetflow.service.WarehouseService;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WarehouseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WarehouseService warehouseService;

    private WarehouseDTO sampleWarehouse() {
        WarehouseDTO dto = new WarehouseDTO();
        dto.setId(1L);
        dto.setName("Bole Warehouse");
        dto.setCity("Addis Ababa");
        dto.setAddress("Bole Road");
        dto.setContactPhone("0911234567");
        return dto;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_returns200() throws Exception {
        when(warehouseService.getAllWarehouses()).thenReturn(List.of(sampleWarehouse()));

        mockMvc.perform(get("/api/warehouses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Bole Warehouse"));
    }

    @Test
    void getAll_returns403_whenNoAuth() throws Exception {
        mockMvc.perform(get("/api/warehouses"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DISPATCHER")
    void getById_returns200() throws Exception {
        when(warehouseService.getWarehouseById(1L)).thenReturn(sampleWarehouse());

        mockMvc.perform(get("/api/warehouses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Addis Ababa"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_returns201_forAdmin() throws Exception {
        when(warehouseService.createWarehouse(any(WarehouseDTO.class)))
                .thenReturn(sampleWarehouse());

        mockMvc.perform(post("/api/warehouses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleWarehouse())))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "DISPATCHER")
    void create_returns403_forDispatcher() throws Exception {
        mockMvc.perform(post("/api/warehouses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleWarehouse())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_returns200() throws Exception {
        when(warehouseService.updateWarehouse(anyLong(), any(WarehouseDTO.class)))
                .thenReturn(sampleWarehouse());

        mockMvc.perform(put("/api/warehouses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleWarehouse())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/warehouses/1"))
                .andExpect(status().isNoContent());
    }
}
