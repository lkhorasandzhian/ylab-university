package ru.ylab.levon.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NonNull;
import lombok.AllArgsConstructor;

@Getter
@ToString
@AllArgsConstructor
public class Product {
    private Long id;
    private @NonNull String name;
    private @NonNull String category;
    private @NonNull String brand;
    private BigDecimal price;
    @Setter
    private String description;

    public void setId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Существующий ID не может быть изменён");
        }
        this.id = id;
    }

    public void setName(@NonNull String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Название не может быть пустым");
        }
        this.name = name;
    }

    public void setCategory(@NonNull String category) {
        if (category.isBlank()) {
            throw new IllegalArgumentException("Категория не может быть пустой");
        }
        this.category = category;
    }

    public void setBrand(@NonNull String brand) {
        if (brand.isBlank()) {
            throw new IllegalArgumentException("Бренд не может быть пустым");
        }
        this.brand = brand;
    }

    public void setPrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Цена должна быть положительной");
        }
        this.price = price;
    }
}
