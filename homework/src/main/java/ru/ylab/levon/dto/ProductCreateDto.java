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
        @NotBlank(message = "Product name cannot be blank")
        String name,

        @NotBlank(message = "Category cannot be blank")
        String category,

        @NotBlank(message = "Brand cannot be blank")
        String brand,

        @NotNull(message = "Price must be provided")
        @Positive(message = "Price must be positive")
        BigDecimal price,

        String description
) {}
