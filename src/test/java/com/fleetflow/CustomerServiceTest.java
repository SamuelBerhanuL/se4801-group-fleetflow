package com.fleetflow;

import com.fleetflow.dto.CustomerDTO;
import com.fleetflow.entity.Customer;
import com.fleetflow.exception.ResourceNotFoundException;
import com.fleetflow.repository.CustomerRepository;
import com.fleetflow.service.CustomerService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer mockCustomer;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        mockCustomer = Customer.builder()
                .id(1L)
                .fullName("Abebe Kebede")
                .email("abebe@gmail.com")
                .phone("0911234567")
                .address("Bole, Addis Ababa")
                .build();

        customerDTO = new CustomerDTO();
        customerDTO.setFullName("Abebe Kebede");
        customerDTO.setEmail("abebe@gmail.com");
        customerDTO.setPhone("0911234567");
        customerDTO.setAddress("Bole, Addis Ababa");
    }

    @Test
    void createCustomer_success() {
        when(customerRepository.save(any(Customer.class)))
                .thenReturn(mockCustomer);

        CustomerDTO result = customerService.createCustomer(customerDTO);

        assertNotNull(result);
        assertEquals("Abebe Kebede", result.getFullName());
        assertEquals("0911234567", result.getPhone());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void getCustomerById_success() {
        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(mockCustomer));

        CustomerDTO result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Abebe Kebede", result.getFullName());
    }

    @Test
    void getCustomerById_throwsException_whenNotFound() {
        when(customerRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> customerService.getCustomerById(99L));
    }

    @Test
    void getAllCustomers_returnsAllCustomers() {
        when(customerRepository.findAll())
                .thenReturn(List.of(mockCustomer));

        List<CustomerDTO> result = customerService.getAllCustomers();

        assertEquals(1, result.size());
        assertEquals("Abebe Kebede", result.get(0).getFullName());
    }

    @Test
    void deleteCustomer_success() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(1L);

        assertDoesNotThrow(() -> customerService.deleteCustomer(1L));
        verify(customerRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCustomer_throwsException_whenNotFound() {
        when(customerRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> customerService.deleteCustomer(99L));
    }

    @Test
    void searchByName_returnsMatchingCustomers() {
        when(customerRepository.findByFullNameContainingIgnoreCase("Abebe"))
                .thenReturn(List.of(mockCustomer));

        List<CustomerDTO> result = customerService.searchByName("Abebe");

        assertEquals(1, result.size());
        assertEquals("Abebe Kebede", result.get(0).getFullName());
    }
}
