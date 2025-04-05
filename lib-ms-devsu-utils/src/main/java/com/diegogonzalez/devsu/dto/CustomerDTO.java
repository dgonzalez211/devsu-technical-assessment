package com.diegogonzalez.devsu.dto;

import com.diegogonzalez.devsu.entity.Customer.CustomerStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

    private UUID uuid;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be less than 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be less than 50 characters")
    private String lastName;

    private String fullName;

    @Email(message = "Invalid email format")
    private String email;

    private String phoneNumber;

    private String address;

    @NotBlank(message = "Customer ID is required")
    @Pattern(regexp = "^[A-Z0-9]{5,15}$", message = "Invalid customer ID format")
    private String customerId;

    @NotNull(message = "Status is required")
    private CustomerStatus status;

    private LocalDateTime lastLoginAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}