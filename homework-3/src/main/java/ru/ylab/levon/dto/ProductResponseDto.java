package ru.ylab.levon.dto;

import java.math.BigDecimal;

/**
 * DTO для передачи данных о продукте клиенту.
 * <p>
 * Используется сервлетами в JSON-ответах как безопасное представление сущности {@code Product}.
 */
public record ProductResponseDto(
        Long id,
        String name,
        String category,
        String brand,
        BigDecimal price,
        String description
) {}
