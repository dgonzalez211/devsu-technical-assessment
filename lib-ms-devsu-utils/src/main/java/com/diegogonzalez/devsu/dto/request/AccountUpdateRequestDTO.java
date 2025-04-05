package com.diegogonzalez.devsu.dto.request;

import com.diegogonzalez.devsu.entity.Account.AccountStatus;
import com.diegogonzalez.devsu.entity.Account.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
public class AccountUpdateRequestDTO {

    @NotNull(message = "Account ID is required")
    private UUID uuid;

    private AccountType accountType;

    private AccountStatus status;

    private String currencyCode;

    private BigDecimal interestRate;

    private BigDecimal overdraftLimit;
}