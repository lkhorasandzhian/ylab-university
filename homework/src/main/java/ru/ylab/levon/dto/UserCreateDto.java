package ru.ylab.levon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO для создания нового пользователя.
 * <p>
 * Используется при регистрации и содержит минимальный набор данных,
 * необходимых для формирования сущности {@link ru.ylab.levon.model.User}.
 *
 * @param username имя пользователя
 * @param password пароль пользователя
 */
public record UserCreateDto(
        @NotBlank(message = "Username cannot be blank")
        String username,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 3, message = "Password must be at least 3 characters long")
        String password
) {}
