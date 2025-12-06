package ru.ylab.levon.dto;

import java.time.LocalDateTime;

/**
 * DTO для передачи данных записи аудита пользователю или между слоями приложения.
 * <p>
 * Используется сервлетами и сервисами для возврата безопасной,
 * сериализуемой версии сущности {@code AuditRecord}.
 */
public record AuditRecordDto(
        Long id,
        String username,
        String action,
        LocalDateTime timestamp
) {}
