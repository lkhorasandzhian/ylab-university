package ru.ylab.levon.model;

import java.io.Serial;
import java.io.Serializable;

import lombok.Getter;
import lombok.ToString;
import lombok.NonNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Класс {@code User} представляет пользователя системы.
 * <p>
 * Содержит информацию об имени пользователя, пароле и роли (например, ADMIN или USER).
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
     * Пароль пользователя (не выводится в {@code toString()}).
     */
    @Getter(AccessLevel.NONE)
    private final @NonNull String password;

    /**
     * Роль пользователя (например, ADMIN или USER).
     */
    private final @NonNull Role role;

    /**
     * Проверяет корректность введённого пароля.
     *
     * @param input введённый пароль
     * @return {@code true}, если пароль совпадает с сохранённым; {@code false} — иначе
     */
    public boolean checkPassword(@NonNull String input) {
        return input.equals(password);
    }
}
