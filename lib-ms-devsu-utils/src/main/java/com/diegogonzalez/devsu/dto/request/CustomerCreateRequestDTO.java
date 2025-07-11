package com.diegogonzalez.devsu.dto.request;

import com.diegogonzalez.devsu.entity.Customer;
import com.diegogonzalez.devsu.entity.Person;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

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
public class CustomerCreateRequestDTO implements Serializable {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be less than 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be less than 50 characters")
    private String lastName;

    @Email(message = "Invalid email format")
    private String email;

    private String phoneNumber;

    private String address;

    @NotBlank(message = "Customer ID is required")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Invalid customer ID format")
    private String customerId;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    @NotBlank(message = "Identification is required")
    @Pattern(regexp = "^[A-Z0-9]{5,20}$", message = "Invalid identification format")
    private String identification;

    @NotNull(message = "Gender is required")
    private Person.Gender gender;

    @NotNull(message = "Age is required")
    @Positive(message = "Age must be a positive number")
    private Integer age;

    @NotNull(message = "Status is required")
    private Customer.CustomerStatus customerStatus;
}
