package ru.ylab.levon.dto;

import jakarta.validation.constraints.NotBlank;

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
        @NotBlank String username,
        @NotBlank String password
) {}
