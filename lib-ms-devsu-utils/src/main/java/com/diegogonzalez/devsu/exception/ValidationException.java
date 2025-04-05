package com.diegogonzalez.devsu.exception;

import lombok.Getter;

import java.util.Map;

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
public class ValidationException extends RuntimeException {
    private final Map<String, String> errors;

    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }

}