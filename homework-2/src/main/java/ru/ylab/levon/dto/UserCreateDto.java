package ru.ylab.levon.dto;

import lombok.NonNull;
import ru.ylab.levon.model.Role;

/**
 * DTO для создания нового пользователя.
 * <p>
 * Используется при регистрации и содержит минимальный набор данных,
 * необходимых для формирования сущности {@link ru.ylab.levon.model.User}.
 *
 * @param username имя пользователя
 * @param password пароль пользователя
 * @param role     роль нового пользователя
 */
public record UserCreateDto(
        @NonNull String username,
        @NonNull String password,
        @NonNull Role role
) {}
