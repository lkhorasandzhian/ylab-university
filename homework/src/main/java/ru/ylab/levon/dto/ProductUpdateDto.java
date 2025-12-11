package ru.ylab.levon.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO для обновления существующего товара.
 * <p>
 * Предоставляет набор необязательных полей, которые могут быть изменены.
 * Любое поле может быть {@code null} — в этом случае соответствующее
 * значение товара остаётся неизменным.
 *
 * @param name        новое название товара (или {@code null})
 * @param category    новая категория (или {@code null})
 * @param brand       новый бренд (или {@code null})
 * @param price       новая цена (или {@code null})
 * @param description новое описание (или {@code null})
 */
public record ProductUpdateDto(
        String name,
        String category,
        String brand,

        @Positive(message = "Price must be positive")
        BigDecimal price,

        String description
) {}
