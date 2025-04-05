package com.diegogonzalez.devsu.repository;

import com.diegogonzalez.devsu.entity.Customer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
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
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findCustomerByUuid(UUID uuid);

    Optional<Customer> findCustomerByCustomerId(@NotBlank(message = "Customer ID is required") @Pattern(regexp = "^[A-Z0-9]{5,15}$", message = "Invalid customer ID format") String customerId);
}
