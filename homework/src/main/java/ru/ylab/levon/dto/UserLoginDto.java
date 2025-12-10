package ru.ylab.levon.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO для входа пользователя.
 * <p>
 * Используется при авторизации и содержит минимальный набор данных,
 * необходимых для формирования сущности {@link ru.ylab.levon.model.User}.
 *
 * @param username имя пользователя
 * @param password пароль пользователя
 */
public record UserLoginDto(
        @NotBlank(message = "Username cannot be blank")
        String username,

        @NotBlank(message = "Password cannot be blank")
        String password
) {}
