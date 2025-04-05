package com.diegogonzalez.devsu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class MovementResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID uuid;
    private LocalDateTime date;
    private String type;
    private BigDecimal amount;
    private BigDecimal balance;
    private String description;
    private UUID accountId;
    private String accountNumber;
    private String customerFirstName;
    private String customerLastName;
    private String referenceNumber;
    private LocalDateTime createdAt;
    private String status;
}