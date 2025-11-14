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
 * Класс {@code Product} представляет товар в каталоге маркетплейса.
 * <p>
 * Содержит основную информацию о товаре: идентификатор, название, категорию,
 * бренд, цену и описание. Реализует базовую валидацию при изменении данных.
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
     * Категория, к которой принадлежит товар.
     */
    private @NonNull String category;

    /**
     * Бренд или производитель товара.
     */
    private @NonNull String brand;

    /**
     * Стоимость товара (в условных единицах).
     */
    private BigDecimal price;

    /**
     * Дополнительное описание товара (может быть пустым или {@code null}).
     */
    @Setter
    private String description;

    /**
     * Устанавливает новое название товара.
     *
     * @param name новое название
     * @throws IllegalArgumentException если строка пустая или содержит только пробелы
     */
    public void setName(@NonNull String name) {
        if (name.isBlank()) throw new IllegalArgumentException("Название не может быть пустым");
        this.name = name;
    }

    /**
     * Устанавливает новую категорию товара.
     *
     * @param category новая категория
     * @throws IllegalArgumentException если строка пустая или содержит только пробелы
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
     * @throws IllegalArgumentException если строка пустая или содержит только пробелы
     */
    public void setBrand(@NonNull String brand) {
        if (brand.isBlank()) throw new IllegalArgumentException("Бренд не может быть пустым");
        this.brand = brand;
    }

    /**
     * Устанавливает цену товара.
     *
     * @param price новая цена
     * @throws IllegalArgumentException если цена меньше либо равна нулю
     */
    public void setPrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Цена должна быть положительной");
        this.price = price;
    }
}
