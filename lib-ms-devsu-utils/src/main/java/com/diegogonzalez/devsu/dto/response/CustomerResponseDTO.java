package com.diegogonzalez.devsu.dto.response;

import com.diegogonzalez.devsu.entity.Customer.CustomerStatus;
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
public class CustomerResponseDTO {

    private UUID uuid;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String customerId;
    private CustomerStatus status;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer accountCount;
}