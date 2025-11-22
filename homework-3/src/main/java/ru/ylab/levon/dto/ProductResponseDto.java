package ru.ylab.levon.dto;

import java.math.BigDecimal;

public record ProductResponseDto(
        Long id,
        String name,
        String category,
        String brand,
        BigDecimal price,
        String description
) {}
