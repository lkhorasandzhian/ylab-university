package ru.ylab.levon.model;

import lombok.Getter;
import lombok.ToString;
import lombok.NonNull;
import lombok.AllArgsConstructor;

/**
 * Модель пользователя системы.
 * <p>
 * Содержит идентификатор, имя пользователя, пароль и роль.
 * Поле {@code password} исключено из {@code toString()} для безопасности.
 */
@Getter
@ToString(exclude = "password")
@AllArgsConstructor
public class User {
    private Long id;
    private final @NonNull String username;
    private final @NonNull String password;
    private final @NonNull Role role;

    /**
     * Устанавливает идентификатор пользователя.
     * <p>
     * Метод допускает установку ID только один раз — при первом сохранении.
     * Попытка изменить уже установленный ID приведёт к исключению.
     *
     * @param id новый идентификатор
     * @throws IllegalStateException если ID уже был установлен ранее
     */
    public void setId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Существующий ID не может быть изменён");
        }
        this.id = id;
    }

    /**
     * Проверяет корректность введённого пароля.
     *
     * @param input введённый пароль
     * @return {@code true}, если пароль совпадает; {@code false} иначе
     */
    public boolean checkPassword(@NonNull String input) {
        return input.equals(password);
    }
}
