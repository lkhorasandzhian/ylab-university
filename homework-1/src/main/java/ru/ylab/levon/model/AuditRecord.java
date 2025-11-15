package ru.ylab.levon.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Запись аудита, фиксирующая действие, выполненное пользователем.
 * <p>
 * Содержит имя пользователя, описание действия и временную метку выполнения.
 * Используется сервисом аудита для ведения журнала событий.
 *
 * @param username  имя пользователя, совершившего действие
 * @param action    описание выполненного действия
 * @param timestamp время выполнения действия
 */
public record AuditRecord(String username, String action, LocalDateTime timestamp) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Создаёт запись аудита, автоматически устанавливая текущую временную метку.
     *
     * @param username имя пользователя
     * @param action   описание действия
     */
    public AuditRecord(String username, String action) {
        this(username, action, LocalDateTime.now());
    }
}
