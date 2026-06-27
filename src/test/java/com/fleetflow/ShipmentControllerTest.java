package com.fleetflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleetflow.dto.CreateShipmentRequest;
import com.fleetflow.dto.ShipmentDTO;
import com.fleetflow.entity.ShipmentStatus;
import com.fleetflow.service.ShipmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ShipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ShipmentService shipmentService;

    private ShipmentDTO sampleShipment() {
        ShipmentDTO dto = new ShipmentDTO();
        dto.setId(1L);
        dto.setTrackingCode("FF-A1B2C3D4");
        dto.setStatus(ShipmentStatus.PENDING);
        dto.setWeight(java.math.BigDecimal.valueOf(100.0));
        dto.setDriverId(1L);
        dto.setDriverName("Abebe Tadesse");
        dto.setVehicleId(1L);
        dto.setVehiclePlate("AA-12345");
        dto.setRouteId(1L);
        dto.setRouteName("Addis to Hawassa");
        dto.setCustomerId(1L);
        dto.setCustomerName("Kebede Alemu");
        dto.setOriginWarehouseId(1L);
        dto.setOriginWarehouseName("Bole Warehouse");
        return dto;
    }

    private CreateShipmentRequest sampleRequest() {
        CreateShipmentRequest req = new CreateShipmentRequest();
        req.setDriverId(1L);
        req.setVehicleId(1L);
        req.setRouteId(1L);
        req.setCustomerId(1L);
        req.setOriginWarehouseId(1L);
        req.setWeight(java.math.BigDecimal.valueOf(100.0));
        req.setDescription("Electronics");
        return req;
    }

    @Test
    @WithMockUser(roles = "DISPATCHER")
    void create_returns201() throws Exception {
        when(shipmentService.createShipment(any(CreateShipmentRequest.class)))
                .thenReturn(sampleShipment());

        mockMvc.perform(post("/api/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.trackingCode").value("FF-A1B2C3D4"));
    }

    @Test
    void create_returns403_whenNoAuth() throws Exception {
        mockMvc.perform(post("/api/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_returns200_paginated() throws Exception {
        Page<ShipmentDTO> page = new PageImpl<>(List.of(sampleShipment()));
        when(shipmentService.getAllShipments(any())).thenReturn(page);

        mockMvc.perform(get("/api/shipments"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void getById_returns200() throws Exception {
        when(shipmentService.getShipmentById(1L)).thenReturn(sampleShipment());

        mockMvc.perform(get("/api/shipments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = "DISPATCHER")
    void getByTrackingCode_returns200() throws Exception {
        when(shipmentService.getByTrackingCode("FF-A1B2C3D4"))
                .thenReturn(sampleShipment());

        mockMvc.perform(get("/api/shipments/tracking/FF-A1B2C3D4"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByCustomer_returns200() throws Exception {
        Page<ShipmentDTO> page = new PageImpl<>(List.of(sampleShipment()));
        when(shipmentService.getShipmentsByCustomer(anyLong(), any())).thenReturn(page);

        mockMvc.perform(get("/api/shipments/customer/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByStatus_returns200() throws Exception {
        Page<ShipmentDTO> page = new PageImpl<>(List.of(sampleShipment()));
        when(shipmentService.getShipmentsByStatus(any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/shipments/status/PENDING"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DISPATCHER")
    void updateStatus_returns200() throws Exception {
        ShipmentDTO updated = sampleShipment();
        updated.setStatus(ShipmentStatus.PICKED_UP);
        when(shipmentService.updateStatus(eq(1L), anyString())).thenReturn(updated);

        mockMvc.perform(put("/api/shipments/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("status", "PICKED_UP"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PICKED_UP"));
    }
}
