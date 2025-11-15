package ru.ylab.levon.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NonNull;
import lombok.AllArgsConstructor;

/**
 * Модель товара в каталоге.
 * <p>
 * Содержит идентификатор товара, название, категорию, бренд, цену
 * и дополнительное описание. Предоставляет методы для изменения полей
 * с базовой валидацией входных данных.
 */
@Getter
@ToString
@AllArgsConstructor
public class Product implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Уникальный идентификатор товара.
     */
    private final @NonNull String id;

    /**
     * Название товара.
     */
    private @NonNull String name;

    /**
     * Категория товара.
     */
    private @NonNull String category;

    /**
     * Бренд или производитель товара.
     */
    private @NonNull String brand;

    /**
     * Цена товара.
     */
    private BigDecimal price;

    /**
     * Описание товара (может быть {@code null}).
     */
    @Setter
    private String description;

    /**
     * Устанавливает новое название товара.
     *
     * @param name новое название
     * @throws IllegalArgumentException если строка пуста или содержит только пробелы
     */
    public void setName(@NonNull String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Название не может быть пустым");
        }
        this.name = name;
    }

    /**
     * Устанавливает новую категорию товара.
     *
     * @param category новая категория
     * @throws IllegalArgumentException если строка пуста или содержит только пробелы
     */
    public void setCategory(@NonNull String category) {
        if (category.isBlank()) {
            throw new IllegalArgumentException("Категория не может быть пустой");
        }
        this.category = category;
    }

    /**
     * Устанавливает новый бренд товара.
     *
     * @param brand новый бренд
     * @throws IllegalArgumentException если строка пуста или содержит только пробелы
     */
    public void setBrand(@NonNull String brand) {
        if (brand.isBlank()) {
            throw new IllegalArgumentException("Бренд не может быть пустым");
        }
        this.brand = brand;
    }

    /**
     * Устанавливает новую цену товара.
     *
     * @param price новая цена
     * @throws IllegalArgumentException если цена меньше или равна нулю
     */
    public void setPrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Цена должна быть положительной");
        }
        this.price = price;
    }
}
