package com.example.demo.common.util;

import com.example.demo.common.exception.BusinessException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 参数校验工具
 */
public class ValidationUtil {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private ValidationUtil() {
    }

    public static <T> void validate(T obj, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(obj, groups);
        if (!violations.isEmpty()) {
            String msg = violations.stream()
                    .map(v -> v.getPropertyPath() + " " + v.getMessage())
                    .collect(Collectors.joining("; "));
            throw new BusinessException(msg);
        }
    }
}

