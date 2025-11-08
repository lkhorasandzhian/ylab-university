package ru.ylab.levon.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.NonNull;
import ru.ylab.levon.model.Product;


public class CatalogService {
    private final Map<String, Product> products = new HashMap<>();

    public void addProduct(@NonNull Product product) {
        products.put(product.getId(), product);
    }

    public Product getProduct(@NonNull String id) {
        return products.get(id);
    }

    public Collection<Product> getAllProducts() {
        return products.values();
    }

    public void removeProduct(@NonNull String id) {
        products.remove(id);
    }

    public boolean updateProduct(@NonNull String id,
                                 String name,
                                 String category,
                                 String brand,
                                 Double price,
                                 String description) {
        Product product = products.get(id);
        if (product == null) {
            return false;
        }

        if (name != null) product.setName(name);
        if (category != null) product.setCategory(category);
        if (brand != null) product.setBrand(brand);
        if (price != null) product.setPrice(price);
        if (description != null) product.setDescription(description);

        return true;
    }
}
