package ru.ylab.levon.web.util;

import java.util.Set;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;

public class ValidatorUtils {
    @SuppressWarnings("resource")
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> Set<ConstraintViolation<T>> validate(T object) {
        return validator.validate(object);
    }
}
