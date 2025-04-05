package com.diegogonzalez.devsu.exception;

import com.diegogonzalez.devsu.dto.response.IntegrationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;

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
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpStatus status) {

        return createErrorResponse(ex, ApplicationResponse.INVALID_ARGS, status);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpStatus status) {

        log.warn("Unsupported HTTP method: {}", ex.getMessage());
        return createErrorResponse(ex, ApplicationResponse.NOT_ALLOWED, status);
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<Object> handleServletRequestBindingException(
            ServletRequestBindingException ex,
            HttpStatus status) {

        return createErrorResponse(ex, ApplicationResponse.NOT_ALLOWED, status);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpStatus status) {

        return createErrorResponse(ex, ApplicationResponse.INVALID_BODY, status);
    }

    @ExceptionHandler(MicroserviceException.class)
    public ResponseEntity<Object> handleMicroserviceException(
            MicroserviceException ex,
            WebRequest request) {

        log.warn("Microservice exception. Status: {}, Code: {}, Message: {}",
                ex.getStatus(), ex.getCode(), ex.getMessage());

        return createErrorResponse(ex,
                ApplicationResponse.DEFAULT_ERROR,
                HttpStatus.valueOf(ex.getStatus()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(
            Exception ex) {

        log.error("Unhandled exception", ex);
        return createErrorResponse(
                ex,
                ApplicationResponse.DEFAULT_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> createErrorResponse(
            Exception ex,
            ApplicationResponse error,
            HttpStatus status) {

        return ResponseEntity.status(status)
                .body(IntegrationResponse.builder()
                        .code(error.getCode())
                        .message(ex.getMessage())
                        .errors(Arrays.stream(ex.getStackTrace()).limit(10)
                                .map(StackTraceElement::toString)
                                .toList())
                        .build());
    }
}