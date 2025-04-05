package com.diegogonzalez.devsu.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

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
@Getter
@ToString
@AllArgsConstructor
public enum ApplicationResponse {

    SUCCESS("200.00.000", "Ok", HttpStatus.OK),
    DEFAULT_ERROR("500.00.000", "General error", HttpStatus.INTERNAL_SERVER_ERROR),
    REST_CLIENT("500.01.000", "Unexpected exception when calling external service", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_ARGS("400.01.000", "Invalid arguments", HttpStatus.BAD_REQUEST),
    NOT_ALLOWED("401.00.000", "Not allowed", HttpStatus.UNAUTHORIZED),
    INVALID_BODY("400.02.000", "Invalid request body", HttpStatus.BAD_REQUEST),
    CUSTOMER_NOT_FOUND("404.01.000", "Customer not found", HttpStatus.NOT_FOUND),
    CUSTOMER_VALIDATION_ERROR("400.03.000", "Error at request validation", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_FOUND("404.02.000", "Account not found", HttpStatus.NOT_FOUND),
    INSUFFICIENT_BALANCE("400.04.000", "Insufficient balance", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatusCode;
}
