package com.diegogonzalez.devsu.controller;

import com.diegogonzalez.devsu.dto.request.MovementCreateRequestDTO;
import com.diegogonzalez.devsu.dto.response.IntegrationResponse;
import com.diegogonzalez.devsu.service.MovementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/movements", produces = MediaType.APPLICATION_JSON_VALUE)
public class MovementController {

    private final MovementService service;

    @SneakyThrows
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Register a new movement")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "201", description = "Movement registered"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "General error"),
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<IntegrationResponse> registrarMovimiento(@RequestBody MovementCreateRequestDTO request) {
        return ResponseEntity.ok(IntegrationResponse.success(service.register(request)));
    }

    @SneakyThrows
    @GetMapping(value = "/customer/{customerId}")
    @Operation(description = "Get a movement report for customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "General error"),
    })
    public ResponseEntity<IntegrationResponse> reporte(@PathVariable UUID customerId,
                                                       @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
                                                       @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate) {
        return ResponseEntity.ok(IntegrationResponse.success(service.report(customerId, startDate, endDate)));
    }

}
