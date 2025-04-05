package com.diegogonzalez.devsu.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
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
public class MovementUpdateRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Movement UUID is required")
    private UUID uuid;

    @PastOrPresent(message = "Movement date must be in the past or present")
    private LocalDateTime date;

    private String type;

    private BigDecimal amount;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    private String status;
}