package com.diegogonzalez.devsu.service;

import com.diegogonzalez.devsu.component.mapper.AccountMapper;
import com.diegogonzalez.devsu.dto.AccountDTO;
import com.diegogonzalez.devsu.dto.request.AccountCreateRequestDTO;
import com.diegogonzalez.devsu.entity.Account;
import com.diegogonzalez.devsu.entity.Customer;
import com.diegogonzalez.devsu.exception.MicroserviceException;
import com.diegogonzalez.devsu.repository.AccountRepository;
import com.diegogonzalez.devsu.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account testAccount;
    private AccountDTO testAccountDTO;
    private AccountCreateRequestDTO createRequestDTO;
    private Customer testCustomer;
    private UUID accountId;
    private String accountNumber;

    @BeforeEach
    void setUp() {

        accountId = UUID.randomUUID();
        accountNumber = "1000100010001";

        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setUuid(UUID.randomUUID());
        testCustomer.setCustomerId(UUID.randomUUID());
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");

        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setUuid(accountId);
        testAccount.setAccountNumber(accountNumber);
        testAccount.setAccountType(Account.AccountType.SAVINGS);
        testAccount.setInitialBalance(new BigDecimal("1000.00"));
        testAccount.setCurrentBalance(new BigDecimal("1000.00"));
        testAccount.setStatus(Account.AccountStatus.ACTIVE);
        testAccount.setCurrencyCode("USD");
        testAccount.setCustomer(testCustomer);
        testAccount.setOpenedAt(LocalDateTime.now());
        testAccount.setCreatedAt(LocalDateTime.now());

        testAccountDTO = AccountMapper.INSTANCE.toDto(testAccount);

        createRequestDTO = new AccountCreateRequestDTO();
        createRequestDTO.setAccountNumber("1000100010002");
        createRequestDTO.setAccountType(Account.AccountType.CHECKING);
        createRequestDTO.setInitialBalance(new BigDecimal("2000.00"));
        createRequestDTO.setCustomerId(testCustomer.getUuid());
    }

    @Test
    @DisplayName("Should list all accounts successfully")
    void listAccounts_ShouldReturnAllAccounts() {

        Page<Account> accountPage = new PageImpl<>(List.of(testAccount));
        when(accountRepository.findAllAccounts(any(Pageable.class))).thenReturn(accountPage);


        Page<AccountDTO> result = accountService.listAccounts(Pageable.unpaged());


        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(testAccountDTO.getAccountNumber(), result.getContent().get(0).getAccountNumber());
        verify(accountRepository, times(1)).findAllAccounts(any(Pageable.class));
    }

    @Test
    @DisplayName("Should find account by account number successfully")
    void findAccountByAccountNumber_ShouldReturnAccount() {

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(testAccount));


        AccountDTO result = accountService.findAccountByAccountNumber(accountNumber);


        assertNotNull(result);
        assertEquals(testAccountDTO.getAccountNumber(), result.getAccountNumber());
        assertEquals(testAccountDTO.getAccountType(), result.getAccountType());
        assertEquals(testAccountDTO.getInitialBalance(), result.getInitialBalance());
        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
    }

    @Test
    @DisplayName("Should throw exception when account not found by account number")
    void findAccountByAccountNumber_ShouldThrowExceptionWhenAccountNotFound() {

        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());


        assertThrows(MicroserviceException.class, () -> accountService.findAccountByAccountNumber("nonexistent"));
        verify(accountRepository, times(1)).findByAccountNumber(anyString());
    }

    @Test
    @DisplayName("Should find account by ID successfully")
    void findAccountById_ShouldReturnAccount() {

        when(accountRepository.findByUuid(accountId)).thenReturn(Optional.of(testAccount));


        Account result = accountService.findAccountById(accountId);


        assertNotNull(result);
        assertEquals(testAccount.getAccountNumber(), result.getAccountNumber());
        assertEquals(testAccount.getAccountType(), result.getAccountType());
        verify(accountRepository, times(1)).findByUuid(accountId);
    }

    @Test
    @DisplayName("Should throw exception when account not found by ID")
    void findAccountById_ShouldThrowExceptionWhenAccountNotFound() {

        when(accountRepository.findByUuid(any(UUID.class))).thenReturn(Optional.empty());


        assertThrows(MicroserviceException.class, () -> accountService.findAccountById(UUID.randomUUID()));
        verify(accountRepository, times(1)).findByUuid(any(UUID.class));
    }

    @Test
    @DisplayName("Should create account successfully")
    void createAccount_ShouldCreateAndReturnAccount() {

        Account newAccount = Account.builder()
                .accountNumber(createRequestDTO.getAccountNumber())
                .accountType(createRequestDTO.getAccountType())
                .initialBalance(createRequestDTO.getInitialBalance())
                .currentBalance(createRequestDTO.getInitialBalance())
                .status(Account.AccountStatus.ACTIVE)
                .customer(testCustomer)
                .build();

        when(customerService.findCustomer(any(UUID.class))).thenReturn(testCustomer);
        when(accountRepository.save(any(Account.class))).thenReturn(newAccount);


        AccountDTO result = accountService.createAccount(createRequestDTO);


        assertNotNull(result);
        assertEquals(createRequestDTO.getAccountNumber(), result.getAccountNumber());
        assertEquals(createRequestDTO.getAccountType(), result.getAccountType());
        assertEquals(createRequestDTO.getInitialBalance(), result.getInitialBalance());
        verify(customerService, times(1)).findCustomer(any(UUID.class));
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw exception when creating account with negative initial balance")
    void createAccount_ShouldThrowExceptionWithNegativeInitialBalance() {

        createRequestDTO.setInitialBalance(new BigDecimal("-100.00"));


        assertThrows(MicroserviceException.class, () -> accountService.createAccount(createRequestDTO));
        verify(customerService, never()).findCustomer(any(UUID.class));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Should delete account successfully")
    void deleteAccount_ShouldSetAccountStatusToInactive() {

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);


        accountService.deleteAccount(accountNumber);


        assertEquals(Account.AccountStatus.INACTIVE, testAccount.getStatus());
        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
        verify(accountRepository, times(1)).save(testAccount);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent account")
    void deleteAccount_ShouldThrowExceptionWhenAccountNotFound() {

        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());


        assertThrows(MicroserviceException.class, () -> accountService.deleteAccount("nonexistent"));
        verify(accountRepository, times(1)).findByAccountNumber(anyString());
        verify(accountRepository, never()).save(any(Account.class));
    }
}