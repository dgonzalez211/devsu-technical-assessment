package com.diegogonzalez.devsu.dto.request;

import com.diegogonzalez.devsu.entity.Customer.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
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
public class CustomerUpdateRequestDTO implements Serializable {

    @NotNull(message = "Customer UUID is required")
    private UUID uuid;

    @Size(max = 50, message = "First name must be less than 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must be less than 50 characters")
    private String lastName;

    @Email(message = "Invalid email format")
    private String email;

    private String phoneNumber;

    private String address;

    private CustomerStatus status;
}