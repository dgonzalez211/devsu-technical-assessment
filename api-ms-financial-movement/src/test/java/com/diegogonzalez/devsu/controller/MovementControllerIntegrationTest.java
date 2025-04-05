package com.diegogonzalez.devsu.controller;

import com.diegogonzalez.devsu.MicroserviceApplication;
import com.diegogonzalez.devsu.dto.request.MovementCreateRequestDTO;
import com.diegogonzalez.devsu.entity.Account;
import com.diegogonzalez.devsu.entity.Customer;
import com.diegogonzalez.devsu.entity.Movement;
import com.diegogonzalez.devsu.repository.AccountRepository;
import com.diegogonzalez.devsu.repository.MovementRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = MicroserviceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MovementControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MovementRepository movementRepository;

    private Account testAccount;
    private Movement testMovement;
    private MovementCreateRequestDTO createRequestDTO;
    private Customer testCustomer;
    private UUID accountId;
    private UUID customerId;

    @BeforeEach
    void setUp() {

        movementRepository.deleteAll();
        accountRepository.deleteAll();


        customerId = UUID.randomUUID();
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setUuid(customerId);
        testCustomer.setCustomerId(customerId);
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");


        accountId = UUID.randomUUID();
        testAccount = new Account();
        testAccount.setUuid(accountId);
        testAccount.setAccountNumber("1000100010001");
        testAccount.setAccountType(Account.AccountType.SAVINGS);
        testAccount.setInitialBalance(new BigDecimal("1000.00"));
        testAccount.setCurrentBalance(new BigDecimal("1000.00"));
        testAccount.setStatus(Account.AccountStatus.ACTIVE);
        testAccount.setCurrencyCode("USD");
        testAccount.setCustomer(testCustomer);
        testAccount.setOpenedAt(LocalDateTime.now());
        testAccount.setCreatedAt(LocalDateTime.now());
        testAccount = accountRepository.save(testAccount);


        testMovement = new Movement();
        testMovement.setUuid(UUID.randomUUID());
        testMovement.setDate(LocalDateTime.now());
        testMovement.setType(Movement.MovementType.DEPOSIT);
        testMovement.setAmount(new BigDecimal("500.00"));
        testMovement.setBalance(new BigDecimal("1500.00"));
        testMovement.setDescription("Test deposit");
        testMovement.setAccount(testAccount);
        testMovement.setReferenceNumber("REF123456");
        testMovement.setCreatedAt(LocalDateTime.now());
        testMovement.setStatus(Movement.MovementStatus.COMPLETED);
        testMovement = movementRepository.save(testMovement);


        testAccount.setCurrentBalance(testMovement.getBalance());
        accountRepository.save(testAccount);


        createRequestDTO = new MovementCreateRequestDTO();
        createRequestDTO.setAccountId(accountId);
        createRequestDTO.setAmount(new BigDecimal("200.00"));
        createRequestDTO.setDescription("New deposit");
        createRequestDTO.setType(Movement.MovementType.DEPOSIT.name());
    }

    @Test
    @DisplayName("Should register movement successfully")
    void registerMovement_ShouldRegisterAndReturnMovement() throws Exception {


    }

    @Test
    @DisplayName("Should get movement report by customer ID and date range")
    void getMovementReport_ShouldReturnMovements() throws Exception {

        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        String startDateStr = startDate.format(DateTimeFormatter.ISO_DATE);
        String endDateStr = endDate.format(DateTimeFormatter.ISO_DATE);


        mockMvc.perform(get("/api/v1/movements/report/{customerId}", customerId)
                        .param("startDate", startDateStr)
                        .param("endDate", endDateStr))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("Should return 400 when report dates are invalid")
    void getMovementReport_ShouldReturn400WhenDatesInvalid() throws Exception {

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusDays(30);
        String startDateStr = startDate.format(DateTimeFormatter.ISO_DATE);
        String endDateStr = endDate.format(DateTimeFormatter.ISO_DATE);


        mockMvc.perform(get("/api/v1/movements/report/{customerId}", customerId)
                        .param("startDate", startDateStr)
                        .param("endDate", endDateStr))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when report dates are in the future")
    void getMovementReport_ShouldReturn400WhenDatesInFuture() throws Exception {

        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(30);
        String startDateStr = startDate.format(DateTimeFormatter.ISO_DATE);
        String endDateStr = endDate.format(DateTimeFormatter.ISO_DATE);


        mockMvc.perform(get("/api/v1/movements/report/{customerId}", customerId)
                        .param("startDate", startDateStr)
                        .param("endDate", endDateStr))
                .andExpect(status().isBadRequest());
    }
}