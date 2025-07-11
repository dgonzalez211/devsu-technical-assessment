package com.diegogonzalez.devsu.service;

import com.diegogonzalez.devsu.dto.request.MovementCreateRequestDTO;
import com.diegogonzalez.devsu.dto.response.MovementResponseDTO;
import com.diegogonzalez.devsu.entity.Account;
import com.diegogonzalez.devsu.entity.Customer;
import com.diegogonzalez.devsu.entity.Movement;
import com.diegogonzalez.devsu.event.publisher.MovementCreatedPublisher;
import com.diegogonzalez.devsu.exception.MicroserviceException;
import com.diegogonzalez.devsu.repository.MovementRepository;
import com.diegogonzalez.devsu.service.impl.MovementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovementServiceTest {

    @Mock
    private MovementRepository movementRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private MovementCreatedPublisher movementCreatedPublisher;

    @InjectMocks
    private MovementServiceImpl movementService;

    private Account testAccount;
    private Movement testMovement;
    private MovementCreateRequestDTO createRequestDTO;
    private Customer testCustomer;
    private UUID accountId;
    private UUID customerId;

    @BeforeEach
    void setUp() {

        accountId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setUuid(customerId);
        testCustomer.setCustomerId(customerId.toString());
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");

        testAccount = new Account();
        testAccount.setId(1L);
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

        testMovement = new Movement();
        testMovement.setId(1L);
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

        createRequestDTO = new MovementCreateRequestDTO();
        createRequestDTO.setAccountId(accountId);
        createRequestDTO.setAmount(new BigDecimal("500.00"));
        createRequestDTO.setDescription("Test deposit");
        createRequestDTO.setType(Movement.MovementType.DEPOSIT.name());
    }

    @Test
    @DisplayName("Should register deposit movement successfully")
    void register_ShouldRegisterDepositMovement() {

        when(accountService.findAccountById(accountId)).thenReturn(testAccount);
        when(movementRepository.save(any(Movement.class))).thenReturn(testMovement);
        doNothing().when(movementCreatedPublisher).publishEvent(any(Movement.class));


        MovementResponseDTO result = movementService.register(createRequestDTO);


        assertNotNull(result);
        assertEquals(accountId, result.getAccountId());
        assertEquals(createRequestDTO.getAmount(), result.getAmount());
        assertEquals(testAccount.getCurrentBalance(), result.getBalance());
        verify(accountService, times(1)).findAccountById(accountId);
        verify(movementRepository, times(1)).save(any(Movement.class));
        verify(movementCreatedPublisher, times(1)).publishEvent(any(Movement.class));
    }

    @Test
    @DisplayName("Should register withdrawal movement successfully")
    void register_ShouldRegisterWithdrawalMovement() {

        createRequestDTO.setAmount(new BigDecimal("-200.00"));
        createRequestDTO.setType(Movement.MovementType.WITHDRAWAL.name());

        testMovement.setType(Movement.MovementType.WITHDRAWAL);
        testMovement.setAmount(new BigDecimal("-200.00"));
        testMovement.setBalance(new BigDecimal("800.00"));

        when(accountService.findAccountById(accountId)).thenReturn(testAccount);
        when(movementRepository.save(any(Movement.class))).thenReturn(testMovement);
        doNothing().when(movementCreatedPublisher).publishEvent(any(Movement.class));


        MovementResponseDTO result = movementService.register(createRequestDTO);


        assertNotNull(result);
        assertEquals(accountId, result.getAccountId());
        assertEquals(createRequestDTO.getAmount(), result.getAmount());
        assertEquals(testAccount.getCurrentBalance(), result.getBalance());
        verify(accountService, times(1)).findAccountById(accountId);
        verify(movementRepository, times(1)).save(any(Movement.class));
        verify(movementCreatedPublisher, times(1)).publishEvent(any(Movement.class));
    }

    @Test
    @DisplayName("Should throw exception when insufficient balance for withdrawal")
    void register_ShouldThrowExceptionWhenInsufficientBalance() {

        createRequestDTO.setAmount(new BigDecimal("-1500.00"));
        createRequestDTO.setType(Movement.MovementType.WITHDRAWAL.name());

        when(accountService.findAccountById(accountId)).thenReturn(testAccount);


        assertThrows(MicroserviceException.class, () -> movementService.register(createRequestDTO));
        verify(accountService, times(1)).findAccountById(accountId);
        verify(movementRepository, never()).save(any(Movement.class));
        verify(movementCreatedPublisher, never()).publishEvent(any(Movement.class));
    }

    @Test
    @DisplayName("Should generate movement report successfully")
    void report_ShouldGenerateMovementReport() {

        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        List<Movement> movements = List.of(testMovement);

        when(movementRepository.findAllByCustomerBetween(
                eq(customerId.toString()),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Sort.class)
        )).thenReturn(movements);


        List<MovementResponseDTO> result = movementService.report(customerId.toString(), startDate, endDate);


        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(movementRepository, times(1)).findAllByCustomerBetween(
                eq(customerId.toString()),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Sort.class)
        );
    }

    @Test
    @DisplayName("Should throw exception when report end date is before start date")
    void report_ShouldThrowExceptionWhenEndDateBeforeStartDate() {

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusDays(30);


        assertThrows(MicroserviceException.class, () -> movementService.report(customerId.toString(), startDate, endDate));
        verify(movementRepository, never()).findAllByCustomerBetween(
                any(String.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Sort.class)
        );
    }

    @Test
    @DisplayName("Should throw exception when report date is in the future")
    void report_ShouldThrowExceptionWhenDateInFuture() {

        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(30);


        assertThrows(MicroserviceException.class, () -> movementService.report(customerId.toString(), startDate, endDate));
        verify(movementRepository, never()).findAllByCustomerBetween(
                any(String.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Sort.class)
        );
    }
}