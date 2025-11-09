package ru.ylab.levon.model;

import java.io.Serial;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NonNull;
import lombok.AllArgsConstructor;

@Getter
@ToString
@AllArgsConstructor
public class Product implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final @NonNull String id;
    private @NonNull String name;
    private @NonNull String category;
    private @NonNull String brand;
    private double price;
    @Setter
    private String description;  // Описание может быть пустым или null.

    public void setName(@NonNull String name) {
        if (name.isBlank()) throw new IllegalArgumentException("Название не может быть пустым");
        this.name = name;
    }

    public void setCategory(@NonNull String category) {
        if (category.isBlank()) throw new IllegalArgumentException("Категория не может быть пустой");
        this.category = category;
    }

    public void setBrand(@NonNull String brand) {
        if (brand.isBlank()) throw new IllegalArgumentException("Бренд не может быть пустым");
        this.brand = brand;
    }

    public void setPrice(double price) {
        if (price <= 0) throw new IllegalArgumentException("Цена должна быть положительной");
        this.price = price;
    }
}
