package ru.ylab.levon.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

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
        @NotBlank String name,
        @NotBlank String category,
        @NotBlank String brand,
        @NotNull @Positive BigDecimal price,
        String description
) {}
