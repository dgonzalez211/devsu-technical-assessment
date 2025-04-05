package com.diegogonzalez.devsu.controller;

import com.diegogonzalez.devsu.dto.request.AccountCreateRequestDTO;
import com.diegogonzalez.devsu.dto.response.IntegrationResponse;
import com.diegogonzalez.devsu.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@RequiredArgsConstructor
@Tag(name = "Account Controller", description = "Financial Movement API")
@RequestMapping(path = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {

    private final AccountService service;

    @GetMapping
    @SneakyThrows
    @Operation(description = "Account list")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "204", description = "This customer has no accounts"),
            @ApiResponse(responseCode = "500", description = "General error"),
    })
    public ResponseEntity<IntegrationResponse> listAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return ResponseEntity.ok(IntegrationResponse.success(service.listAccounts(pageable)));
    }

    @SneakyThrows
    @GetMapping(value = "/{accountNumber}")
    @Operation(description = "Query account by account number")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "General error"),
    })
    public ResponseEntity<IntegrationResponse> findAccountByAccountNumber(@PathVariable String accountNumber) {
        return ResponseEntity.ok(IntegrationResponse.success(service.findAccountByAccountNumber(accountNumber)));
    }

    @SneakyThrows
    @PostMapping
    @Operation(description = "Creates a new account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "201", description = "Account created successfully"),
            @ApiResponse(responseCode = "500", description = "General error"),
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<IntegrationResponse> createAccount(@RequestBody @Valid AccountCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(IntegrationResponse.success(service.createAccount(request)));
    }

    @SneakyThrows
    @DeleteMapping(value = "/{accountNumber}")
    @Operation(description = "Delete account by account number")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "General error"),
    })
    public ResponseEntity<IntegrationResponse> deleteAccount(@PathVariable String accountNumber) {
        service.deleteAccount(accountNumber);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(IntegrationResponse.success());
    }

}
