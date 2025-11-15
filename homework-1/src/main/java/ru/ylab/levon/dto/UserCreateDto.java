package ru.ylab.levon.dto;

import ru.ylab.levon.model.Role;

public record UserCreateDto(
        String username,
        String password,
        Role role
) {}
