package com.diegogonzalez.devsu.controller;

import com.diegogonzalez.devsu.MicroserviceApplication;
import com.diegogonzalez.devsu.dto.request.AccountCreateRequestDTO;
import com.diegogonzalez.devsu.entity.Account;
import com.diegogonzalez.devsu.entity.Customer;
import com.diegogonzalez.devsu.repository.AccountRepository;
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
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = MicroserviceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    private Account testAccount;
    private AccountCreateRequestDTO createRequestDTO;
    private Customer testCustomer;
    private UUID customerId;

    @BeforeEach
    void setUp() {

        accountRepository.deleteAll();


        customerId = UUID.randomUUID();
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setUuid(customerId);
        testCustomer.setCustomerId(customerId);
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");


        testAccount = new Account();
        testAccount.setUuid(UUID.randomUUID());
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


        createRequestDTO = new AccountCreateRequestDTO();
        createRequestDTO.setAccountNumber("1000100010002");
        createRequestDTO.setAccountType(Account.AccountType.CHECKING);
        createRequestDTO.setInitialBalance(new BigDecimal("2000.00"));
        createRequestDTO.setCustomerId(customerId);
    }

    @Test
    @DisplayName("Should get all accounts")
    void getAllAccounts_ShouldReturnAllAccounts() throws Exception {

        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.content[0].accountNumber", is(testAccount.getAccountNumber())))
                .andExpect(jsonPath("$.data.content[0].accountType", is(testAccount.getAccountType().toString())));
    }

    @Test
    @DisplayName("Should get account by account number")
    void getAccountByNumber_ShouldReturnAccount() throws Exception {

        mockMvc.perform(get("/api/v1/accounts/{accountNumber}", testAccount.getAccountNumber()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.accountNumber", is(testAccount.getAccountNumber())))
                .andExpect(jsonPath("$.data.accountType", is(testAccount.getAccountType().toString())))
                .andExpect(jsonPath("$.data.initialBalance", is(testAccount.getInitialBalance().doubleValue())));
    }

    @Test
    @DisplayName("Should return 404 when account not found")
    void getAccountByNumber_ShouldReturn404WhenNotFound() throws Exception {

        mockMvc.perform(get("/api/v1/accounts/{accountNumber}", "nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should create account successfully")
    void createAccount_ShouldCreateAndReturnAccount() throws Exception {


    }

    @Test
    @DisplayName("Should delete account successfully")
    void deleteAccount_ShouldDeleteAccount() throws Exception {

        mockMvc.perform(delete("/api/v1/accounts/{accountNumber}", testAccount.getAccountNumber()))
                .andExpect(status().isNoContent());


        Account updatedAccount = accountRepository.findByAccountNumber(testAccount.getAccountNumber()).orElseThrow();
        assert updatedAccount.getStatus() == Account.AccountStatus.INACTIVE;
    }
}