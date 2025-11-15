package ru.ylab.levon.dto;

import java.math.BigDecimal;

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
        BigDecimal price,
        String description
) {}
