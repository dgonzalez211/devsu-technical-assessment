package com.diegogonzalez.devsu.utils;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.io.Serializable;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
@FunctionalInterface
public interface FieldValidator<T extends Serializable> extends Function<T, Set<String>> {

    static <T extends Serializable> FieldValidator<T> create() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        return dto -> validator.validate(dto)
                .stream()
                .map(violation -> violation.getMessage()
                        .replace("{property}", violation.getPropertyPath().toString()))
                .collect(Collectors.toSet());
    }
}