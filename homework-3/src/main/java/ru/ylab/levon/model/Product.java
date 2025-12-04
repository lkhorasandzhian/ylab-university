package ru.ylab.levon.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NonNull;
import lombok.AccessLevel;

/**
 * Модель продукта каталога.
 * <p>
 * Содержит основные сведения о товаре: название, категорию, бренд,
 * цену и описание. Поле {@code id} может быть установлено только один раз.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Product {
    @Setter(AccessLevel.NONE)
    private Long id;
    private @NonNull String name;
    private @NonNull String category;
    private @NonNull String brand;
    private BigDecimal price;
    private String description;

    /**
     * Устанавливает идентификатор продукта.
     * <p>
     * ID может быть присвоен только один раз — после вставки в БД.
     * Повторная попытка приведёт к ошибке.
     *
     * @param id новый идентификатор
     * @throws IllegalStateException если ID уже установлен
     */
    public void setId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Существующий ID не может быть изменён");
        }
        this.id = id;
    }
}
