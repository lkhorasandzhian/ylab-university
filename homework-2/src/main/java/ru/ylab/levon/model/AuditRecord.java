package ru.ylab.levon.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.ToString;
import lombok.NonNull;

/**
 * Модель записи аудита.
 * <p>
 * Содержит информацию о пользователе, совершённом действии
 * и времени его выполнения. Поле {@code id} устанавливается только один раз.
 */
@Getter
@ToString
public class AuditRecord {
    private Long id;
    private final @NonNull String username;
    private final @NonNull String action;
    private final @NonNull LocalDateTime timestamp;

    /**
     * Создаёт новую запись аудита с указанными параметрами.
     *
     * @param username  имя пользователя, совершившего действие
     * @param action    описание действия
     * @param timestamp момент выполнения действия
     */
    public AuditRecord(@NonNull String username, @NonNull String action, @NonNull LocalDateTime timestamp) {
        this.username = username;
        this.action = action;
        this.timestamp = timestamp;
    }

    /**
     * Создаёт запись аудита с текущим временем.
     *
     * @param username имя пользователя
     * @param action   описание действия
     */
    public AuditRecord(@NonNull String username, @NonNull String action) {
        this(username, action, LocalDateTime.now());
    }

    /**
     * Устанавливает идентификатор записи аудита.
     * <p>
     * Идентификатор может быть установлен только один раз.
     *
     * @param id идентификатор записи
     * @throws IllegalStateException если ID уже установлен
     */
    public void setId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Существующий ID не может быть изменён");
        }
        this.id = id;
    }
}
