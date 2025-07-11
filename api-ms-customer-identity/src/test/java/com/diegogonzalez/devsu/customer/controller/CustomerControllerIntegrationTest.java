package com.diegogonzalez.devsu.customer.controller;

import com.diegogonzalez.devsu.customer.MicroserviceApplication;
import com.diegogonzalez.devsu.customer.repository.CustomerRepository;
import com.diegogonzalez.devsu.dto.request.CustomerCreateRequestDTO;
import com.diegogonzalez.devsu.dto.request.CustomerUpdateRequestDTO;
import com.diegogonzalez.devsu.entity.Customer;
import com.diegogonzalez.devsu.entity.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = MicroserviceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer testCustomer;
    private CustomerCreateRequestDTO createRequestDTO;
    private CustomerUpdateRequestDTO updateRequestDTO;

    @BeforeEach
    void setUp() {

        customerRepository.deleteAll();


        testCustomer = new Customer();
        testCustomer.setUuid(UUID.randomUUID());
        testCustomer.setCustomerId(UUID.randomUUID().toString());
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setIdentification("1234567890");
        testCustomer.setEmail("john.doe@example.com");
        testCustomer.setStatus(Person.PersonStatus.ACTIVE);
        testCustomer.setCustomerStatus(Customer.CustomerStatus.ACTIVE);
        testCustomer.setPassword("$2a$10$Xt8Yt1EoWvZj3rZbgRlKXOQQhHRJlj7BKJJg5.yQzJ/Zs5pXVEJyO");
        testCustomer.setCreatedAt(LocalDateTime.now());
        testCustomer.setCreatedBy("system");
        testCustomer.setAge(30);
        testCustomer.setGender(Person.Gender.MALE);
        testCustomer.setBirthDate(LocalDate.of(1993, 1, 1));
        testCustomer = customerRepository.save(testCustomer);


        createRequestDTO = new CustomerCreateRequestDTO();
        createRequestDTO.setFirstName("Jane");
        createRequestDTO.setLastName("Smith");
        createRequestDTO.setEmail("jane.smith@example.com");
        createRequestDTO.setPassword("password456");
        createRequestDTO.setAddress("123 Main St");

        updateRequestDTO = new CustomerUpdateRequestDTO();
        updateRequestDTO.setFirstName("John");
        updateRequestDTO.setLastName("Updated");
        updateRequestDTO.setEmail("john.updated@example.com");
        updateRequestDTO.setAddress("456 New St");
    }

    @Test
    @DisplayName("Should get all customers")
    void getAllCustomers_ShouldReturnAllCustomers() throws Exception {

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].firstName", is(testCustomer.getFirstName())))
                .andExpect(jsonPath("$.data[0].lastName", is(testCustomer.getLastName())));
    }

    @Test
    @DisplayName("Should get customer by ID")
    void getCustomerById_ShouldReturnCustomer() throws Exception {

        mockMvc.perform(get("/api/v1/customers/{id}", testCustomer.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.firstName", is(testCustomer.getFirstName())))
                .andExpect(jsonPath("$.data.lastName", is(testCustomer.getLastName())))
                .andExpect(jsonPath("$.data.email", is(testCustomer.getEmail())));
    }

    @Test
    @DisplayName("Should return 404 when customer not found")
    void getCustomerById_ShouldReturn404WhenNotFound() throws Exception {

        mockMvc.perform(get("/api/v1/customers/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should create customer successfully")
    void createCustomer_ShouldCreateAndReturnCustomer() throws Exception {

        ResultActions result = mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequestDTO)));


        result.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.firstName", is(createRequestDTO.getFirstName())))
                .andExpect(jsonPath("$.data.lastName", is(createRequestDTO.getLastName())))
                .andExpect(jsonPath("$.data.email", is(createRequestDTO.getEmail())));
    }

    @Test
    @DisplayName("Should update customer successfully")
    void updateCustomer_ShouldUpdateAndReturnCustomer() throws Exception {

        ResultActions result = mockMvc.perform(put("/api/v1/customers/{id}", testCustomer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequestDTO)));


        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.firstName", is(updateRequestDTO.getFirstName())))
                .andExpect(jsonPath("$.data.lastName", is(updateRequestDTO.getLastName())))
                .andExpect(jsonPath("$.data.email", is(updateRequestDTO.getEmail())));
    }

    @Test
    @DisplayName("Should delete customer successfully")
    void deleteCustomer_ShouldDeleteCustomer() throws Exception {

        mockMvc.perform(delete("/api/v1/customers/{id}", testCustomer.getId()))
                .andExpect(status().isNoContent());


        mockMvc.perform(get("/api/v1/customers/{id}", testCustomer.getId()))
                .andExpect(status().isNotFound());
    }
}