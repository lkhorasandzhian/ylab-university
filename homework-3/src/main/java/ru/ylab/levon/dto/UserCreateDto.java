package ru.ylab.levon.dto;

import lombok.NonNull;

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
        @NonNull String username,
        @NonNull String password
) {}
