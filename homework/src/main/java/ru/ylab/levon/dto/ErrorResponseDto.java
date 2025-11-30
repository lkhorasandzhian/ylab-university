package ru.ylab.levon.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для передачи информации об ошибке клиенту.
 * <p>
 * Используется сервлетами для формирования JSON-ответа при исключениях и ошибках валидации.
 */
public record ErrorResponseDto(
        String message,
        int status,
        LocalDateTime timestamp,
        List<String> errors
) {}
