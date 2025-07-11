package com.diegogonzalez.devsu.customer.service;

import com.diegogonzalez.devsu.component.mapper.CustomerMapper;
import com.diegogonzalez.devsu.customer.repository.CustomerRepository;
import com.diegogonzalez.devsu.customer.service.impl.CustomerServiceImpl;
import com.diegogonzalez.devsu.customer.service.publisher.ApplicationEventProducer;
import com.diegogonzalez.devsu.dto.CustomerDTO;
import com.diegogonzalez.devsu.dto.request.CustomerCreateRequestDTO;
import com.diegogonzalez.devsu.dto.request.CustomerUpdateRequestDTO;
import com.diegogonzalez.devsu.entity.Customer;
import com.diegogonzalez.devsu.entity.Person;
import com.diegogonzalez.devsu.event.customer.CustomerCreatedEvent;
import com.diegogonzalez.devsu.event.customer.CustomerDeletedEvent;
import com.diegogonzalez.devsu.event.customer.CustomerModifiedEvent;
import com.diegogonzalez.devsu.exception.MicroserviceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ApplicationEventProducer applicationEventProducer;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer testCustomer;
    private CustomerDTO testCustomerDTO;
    private CustomerCreateRequestDTO createRequestDTO;
    private CustomerUpdateRequestDTO updateRequestDTO;

    @BeforeEach
    void setUp() {

        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setUuid(UUID.randomUUID());
        testCustomer.setCustomerId(UUID.randomUUID().toString());
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setIdentification("1234567890");
        testCustomer.setEmail("john.doe@example.com");
        testCustomer.setStatus(Person.PersonStatus.ACTIVE);
        testCustomer.setCustomerStatus(Customer.CustomerStatus.ACTIVE);
        testCustomer.setPassword("password123");
        testCustomer.setCreatedAt(LocalDateTime.now());
        testCustomer.setCreatedBy("system");
        testCustomer.setAge(30);
        testCustomer.setGender(Person.Gender.MALE);
        testCustomer.setBirthDate(LocalDate.of(1993, 1, 1));

        testCustomerDTO = CustomerMapper.INSTANCE.toDto(testCustomer);

        createRequestDTO = new CustomerCreateRequestDTO();
        createRequestDTO.setFirstName("Jane");
        createRequestDTO.setLastName("Smith");
        createRequestDTO.setEmail("jane.smith@example.com");
        createRequestDTO.setPassword("password456");
        createRequestDTO.setAddress("123 Main St");
        createRequestDTO.setCustomerId(UUID.randomUUID().toString());
        createRequestDTO.setIdentification("ABCDE12345");
        createRequestDTO.setGender(Person.Gender.FEMALE);
        createRequestDTO.setAge(25);
        createRequestDTO.setCustomerStatus(Customer.CustomerStatus.ACTIVE);

        updateRequestDTO = new CustomerUpdateRequestDTO();
        updateRequestDTO.setFirstName("John");
        updateRequestDTO.setLastName("Updated");
        updateRequestDTO.setEmail("john.updated@example.com");
        updateRequestDTO.setAddress("456 New St");
    }

    @Test
    @DisplayName("Should find all customers successfully")
    void findCustomers_ShouldReturnAllCustomers() {

        when(customerRepository.findAll(any(Sort.class))).thenReturn(List.of(testCustomer));


        List<CustomerDTO> result = customerService.findCustomers();


        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testCustomerDTO.getFirstName(), result.get(0).getFirstName());
        assertEquals(testCustomerDTO.getLastName(), result.get(0).getLastName());
        verify(customerRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    @DisplayName("Should throw exception when no customers found")
    void findCustomers_ShouldThrowExceptionWhenNoCustomersFound() {

        when(customerRepository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());


        assertThrows(MicroserviceException.class, () -> customerService.findCustomers());
        verify(customerRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    @DisplayName("Should find customer by ID successfully")
    void findCustomerById_ShouldReturnCustomer() {

        when(customerRepository.findByCustomerId(anyString())).thenReturn(Optional.of(testCustomer));


        CustomerDTO result = customerService.findCustomerById(UUID.randomUUID().toString());


        assertNotNull(result);
        assertEquals(testCustomerDTO.getFirstName(), result.getFirstName());
        assertEquals(testCustomerDTO.getLastName(), result.getLastName());
        verify(customerRepository, times(1)).findByCustomerId(anyString());
    }

    @Test
    @DisplayName("Should throw exception when customer not found by ID")
    void findCustomerById_ShouldThrowExceptionWhenCustomerNotFound() {

        when(customerRepository.findByCustomerId(anyString())).thenReturn(Optional.empty());


        assertThrows(MicroserviceException.class, () -> customerService.findCustomerById(UUID.randomUUID().toString()));
        verify(customerRepository, times(1)).findByCustomerId(anyString());
    }

    @Test
    @DisplayName("Should create customer successfully")
    void createCustomer_ShouldCreateAndReturnCustomer() {

        Customer newCustomer = CustomerMapper.INSTANCE.toEntity(createRequestDTO);
        newCustomer.setId(2L);
        newCustomer.setUuid(UUID.randomUUID());
        newCustomer.setCustomerId(UUID.randomUUID().toString());
        newCustomer.setStatus(Person.PersonStatus.ACTIVE);
        newCustomer.setCustomerStatus(Customer.CustomerStatus.ACTIVE);
        newCustomer.setCreatedAt(LocalDateTime.now());
        newCustomer.setCreatedBy("system");

        when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);
        doNothing().when(applicationEventProducer).publish(any(CustomerCreatedEvent.class));


        CustomerDTO result = customerService.createCustomer(createRequestDTO);


        assertNotNull(result);
        assertEquals(createRequestDTO.getFirstName(), result.getFirstName());
        assertEquals(createRequestDTO.getLastName(), result.getLastName());
        assertEquals(createRequestDTO.getEmail(), result.getEmail());
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(applicationEventProducer, times(1)).publish(any(CustomerCreatedEvent.class));
    }

    @Test
    @DisplayName("Should update customer successfully")
    void updateCustomer_ShouldUpdateAndReturnCustomer() {

        when(customerRepository.findByCustomerId(anyString())).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        doNothing().when(applicationEventProducer).publish(any(CustomerModifiedEvent.class));


        CustomerDTO result = customerService.updateCustomer(UUID.randomUUID().toString(), updateRequestDTO);


        assertNotNull(result);
        verify(customerRepository, times(1)).findByCustomerId(anyString());
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(applicationEventProducer, times(1)).publish(any(CustomerModifiedEvent.class));
    }

    @Test
    @DisplayName("Should remove customer successfully")
    void removeCustomer_ShouldRemoveCustomer() {

        when(customerRepository.findByCustomerId(anyString())).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        doNothing().when(applicationEventProducer).publish(any(CustomerDeletedEvent.class));


        customerService.removeCustomer(UUID.randomUUID().toString());


        verify(customerRepository, times(1)).findByCustomerId(anyString());
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(applicationEventProducer, times(1)).publish(any(CustomerDeletedEvent.class));
    }
}
