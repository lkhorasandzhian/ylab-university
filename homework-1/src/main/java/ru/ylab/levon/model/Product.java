package ru.ylab.levon.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Product {
    private final String id;
    private String name;
    private String category;
    private String brand;
    private double price;
    private String description;
}
