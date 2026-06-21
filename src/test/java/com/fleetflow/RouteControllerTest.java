package com.fleetflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleetflow.dto.RouteDTO;
import com.fleetflow.service.RouteService;
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
class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RouteService routeService;

    private RouteDTO sampleRoute() {
        RouteDTO dto = new RouteDTO();
        dto.setId(1L);
        dto.setWarehouseId(1L);
        dto.setName("Addis to Hawassa");
        dto.setOriginCity("Addis Ababa");
        dto.setDestinationCity("Hawassa");
        dto.setEstimatedHours(4);
        return dto;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_returns200() throws Exception {
        when(routeService.getAllRoutes()).thenReturn(List.of(sampleRoute()));

        mockMvc.perform(get("/api/routes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Addis to Hawassa"));
    }

    @Test
    void getAll_returns403_whenNoAuth() throws Exception {
        mockMvc.perform(get("/api/routes"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DISPATCHER")
    void getById_returns200() throws Exception {
        when(routeService.getRouteById(1L)).thenReturn(sampleRoute());

        mockMvc.perform(get("/api/routes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destinationCity").value("Hawassa"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByWarehouse_returns200() throws Exception {
        when(routeService.getRoutesByWarehouse(1L)).thenReturn(List.of(sampleRoute()));

        mockMvc.perform(get("/api/routes/warehouse/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_returns201_forAdmin() throws Exception {
        when(routeService.createRoute(any(RouteDTO.class)))
                .thenReturn(sampleRoute());

        mockMvc.perform(post("/api/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRoute())))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "DISPATCHER")
    void create_returns403_forDispatcher() throws Exception {
        mockMvc.perform(post("/api/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRoute())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_returns200() throws Exception {
        when(routeService.updateRoute(anyLong(), any(RouteDTO.class)))
                .thenReturn(sampleRoute());

        mockMvc.perform(put("/api/routes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRoute())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/routes/1"))
                .andExpect(status().isNoContent());
    }
}
