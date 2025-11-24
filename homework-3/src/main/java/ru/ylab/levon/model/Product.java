package ru.ylab.levon.model;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NonNull;

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
    @Setter
    private String description;

    public void setId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Существующий ID не может быть изменён");
        }
        this.id = id;
    }
}
