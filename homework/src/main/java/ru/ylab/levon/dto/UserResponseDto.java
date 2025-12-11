package ru.ylab.levon.dto;

import ru.ylab.levon.model.Role;

/**
 * DTO для передачи информации о пользователе клиенту.
 * <p>
 * Используется в JSON-ответах вместо сущности {@code User}.
 */
public record UserResponseDto(
        Long id,
        String username,
        Role role
) {}
