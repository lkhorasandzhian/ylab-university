package ru.ylab.levon.dto;

import ru.ylab.levon.model.Role;

public record UserResponseDto(
        Long id,
        String username,
        Role role
) {}
