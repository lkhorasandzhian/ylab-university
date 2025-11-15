package ru.ylab.levon.dto;

import java.math.BigDecimal;

import lombok.NonNull;

/**
 * DTO для создания нового товара.
 * <p>
 * Используется при добавлении товара в каталог и содержит набор полей,
 * необходимых для формирования новой сущности {@link ru.ylab.levon.model.Product}.
 *
 * @param name        название товара (обязательно)
 * @param category    категория товара (обязательно)
 * @param brand       бренд товара (обязательно)
 * @param price       цена товара (обязательно, должна быть положительной)
 * @param description дополнительное описание товара (может быть {@code null})
 */
public record ProductCreateDto(
        @NonNull String name,
        @NonNull String category,
        @NonNull String brand,
        @NonNull BigDecimal price,
        String description
) {}
