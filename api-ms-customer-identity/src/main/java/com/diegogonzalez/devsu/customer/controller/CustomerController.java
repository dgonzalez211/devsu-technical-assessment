package com.diegogonzalez.devsu.customer.controller;

import com.diegogonzalez.devsu.customer.service.CustomerService;
import com.diegogonzalez.devsu.dto.request.CustomerCreateRequestDTO;
import com.diegogonzalez.devsu.dto.request.CustomerUpdateRequestDTO;
import com.diegogonzalez.devsu.dto.response.IntegrationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "Customer Controller", description = "Customer API")
@RequestMapping(path = "/customers", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @SneakyThrows
    @Operation(summary = "List all customers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of customers found"),
            @ApiResponse(responseCode = "204", description = "No customers found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<IntegrationResponse> getAllCustomers() {
        return ResponseEntity.ok(IntegrationResponse.success(customerService.findCustomers()));
    }

    @GetMapping("/{customerId}")
    @SneakyThrows
    @Operation(summary = "Get customer by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<IntegrationResponse> getCustomerById(@PathVariable Long customerId) {
        return ResponseEntity.ok(IntegrationResponse.success(customerService.findCustomerById(customerId)));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @SneakyThrows
    @Operation(summary = "Create new customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<IntegrationResponse> createCustomer(@RequestBody CustomerCreateRequestDTO customerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(IntegrationResponse.success(customerService.createCustomer(customerRequest)));
    }

    @PutMapping(path = "/{customerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @SneakyThrows
    @Operation(summary = "Update existing customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<IntegrationResponse> updateCustomer(
            @PathVariable Long customerId,
            @RequestBody CustomerUpdateRequestDTO customerRequest) {
        return ResponseEntity.ok(IntegrationResponse.success(customerService.updateCustomer(customerId, customerRequest)));
    }

    @DeleteMapping("/{customerId}")
    @SneakyThrows
    @Operation(summary = "Delete customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<IntegrationResponse> deleteCustomer(@PathVariable Long customerId) {
        customerService.removeCustomer(customerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(IntegrationResponse.success());
    }
}