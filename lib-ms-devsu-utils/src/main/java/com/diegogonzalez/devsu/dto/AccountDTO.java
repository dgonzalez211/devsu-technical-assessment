package com.diegogonzalez.devsu.dto;

import com.diegogonzalez.devsu.entity.Account.AccountStatus;
import com.diegogonzalez.devsu.entity.Account.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/*
 * Author: Diego González
 *
 * This code is the exclusive property of the author. Any unauthorized use,
 * distribution, or modification is prohibited without the author's explicit consent.
 *
 * Disclaimer: This code is provided "as is" without any warranties of any kind,
 * either express or implied, including but not limited to warranties of merchantability
 * or fitness for a particular purpose.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID uuid;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @NotNull(message = "Initial balance is required")
    @PositiveOrZero(message = "Initial balance must be zero or positive")
    private BigDecimal initialBalance;

    private BigDecimal currentBalance;

    @NotNull(message = "Status is required")
    private AccountStatus status;

    private String currencyCode;

    @NotNull(message = "Customer ID is required")
    private String customerId;

    private LocalDateTime openedAt;
    private LocalDateTime lastTransactionAt;
    private LocalDateTime closedAt;
    private BigDecimal interestRate;
    private BigDecimal overdraftLimit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Set<MovementDTO> movements;
}
