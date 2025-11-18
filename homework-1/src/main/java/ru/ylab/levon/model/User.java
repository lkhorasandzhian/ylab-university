package ru.ylab.levon.model;

import java.io.Serial;
import java.io.Serializable;

import lombok.Getter;
import lombok.ToString;
import lombok.NonNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Модель пользователя системы.
 * <p>
 * Содержит имя пользователя, пароль и роль (ADMIN или USER).
 * Используется для аутентификации и проверки прав доступа.
 */
@Getter
@ToString(exclude = "password")
@AllArgsConstructor
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Уникальное имя пользователя.
     */
    private final @NonNull String username;

    /**
     * Пароль пользователя.
     * <p>
     * Не включается в вывод методов {@code toString()}.
     */
    @Getter(AccessLevel.NONE)
    private final @NonNull String password;

    /**
     * Роль пользователя в системе.
     */
    private final @NonNull Role role;

    /**
     * Проверяет соответствие введённого пароля сохранённому значению.
     *
     * @param input введённый пароль
     * @return {@code true}, если пароль совпадает; {@code false} — иначе
     */
    public boolean checkPassword(@NonNull String input) {
        return input.equals(password);
    }
}
