package com.fleetflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleetflow.dto.CustomerDTO;
import com.fleetflow.service.CustomerService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    private CustomerDTO sampleCustomer() {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(1L);
        dto.setFullName("Abebe Kebede");
        dto.setEmail("abebe@gmail.com");
        dto.setPhone("0911234567");
        dto.setAddress("Bole, Addis Ababa");
        return dto;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_returns200() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(List.of(sampleCustomer()));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Abebe Kebede"));
    }

    @Test
    void getAll_returns403_whenNoAuth() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DISPATCHER")
    void getById_returns200() throws Exception {
        when(customerService.getCustomerById(1L)).thenReturn(sampleCustomer());

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("0911234567"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void search_returns200() throws Exception {
        when(customerService.searchByName(anyString()))
                .thenReturn(List.of(sampleCustomer()));

        mockMvc.perform(get("/api/customers/search").param("name", "Abebe"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DISPATCHER")
    void create_returns201() throws Exception {
        when(customerService.createCustomer(any(CustomerDTO.class)))
                .thenReturn(sampleCustomer());

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleCustomer())))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void create_returns403_forDriver() throws Exception {
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleCustomer())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_returns200() throws Exception {
        when(customerService.updateCustomer(anyLong(), any(CustomerDTO.class)))
                .thenReturn(sampleCustomer());

        mockMvc.perform(put("/api/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleCustomer())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());
    }
}
