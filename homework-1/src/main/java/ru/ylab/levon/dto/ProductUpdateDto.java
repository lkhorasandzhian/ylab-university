package ru.ylab.levon.dto;

import java.math.BigDecimal;

public record ProductUpdateDto(
        String name,
        String category,
        String brand,
        BigDecimal price,
        String description
) {}
