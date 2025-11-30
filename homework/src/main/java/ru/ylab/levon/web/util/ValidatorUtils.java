package ru.ylab.levon.web.util;

import java.util.Set;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;

/**
 * Утилитарный класс для валидации DTO с использованием Jakarta Bean Validation.
 */
public class ValidatorUtils {
    @SuppressWarnings("resource")
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * Валидирует переданный объект и возвращает набор нарушений ограничений.
     *
     * @param object объект для проверки
     * @param <T>    тип проверяемого объекта
     * @return множество нарушений; пустое множество — если нарушений нет
     */
    public static <T> Set<ConstraintViolation<T>> validate(T object) {
        return validator.validate(object);
    }
}
