package ru.ylab.levon.dto;

import java.math.BigDecimal;

import lombok.NonNull;

public record ProductCreateDto(
        @NonNull String name,
        @NonNull String category,
        @NonNull String brand,
        @NonNull BigDecimal price,
        String description
) {}
