package ru.ylab.levon.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Запись аудита, представляющая действие, совершённое пользователем.
 * <p>
 * Содержит имя пользователя, описание действия и временную метку его выполнения.
 * Используется сервисом {@link ru.ylab.levon.service.AuditService}.
 *
 * @param username  имя пользователя, выполнившего действие
 * @param action    описание действия
 * @param timestamp время выполнения действия
 */
public record AuditRecord(String username, String action, LocalDateTime timestamp) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Создаёт запись аудита с автоматической установкой текущего времени.
     *
     * @param username имя пользователя
     * @param action   описание действия
     */
    public AuditRecord(String username, String action) {
        this(username, action, LocalDateTime.now());
    }
}
