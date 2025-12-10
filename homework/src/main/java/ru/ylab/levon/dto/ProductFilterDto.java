package ru.ylab.levon.dto;

import java.math.BigDecimal;

/**
 * DTO для обновления существующего товара.
 * <p>
 * Предоставляет набор необязательных полей, которые могут быть изменены.
 * Любое поле может быть {@code null} — в этом случае соответствующее
 * значение товара остаётся неизменным.
 *
 * @param brand поиск по наименованию бренда (или {@code null})
 * @param category поиск по категории (или {@code null})
 * @param search поиск ключевому слову в наименовании или описании (или {@code null})
 * @param minPrice фильтр минимальной цены включительно (или {@code null})
 * @param maxPrice фильтр максимальной цены включительно (или {@code null})
 */
public record ProductFilterDto(
        String brand,
        String category,
        String search,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {}
