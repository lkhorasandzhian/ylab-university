package ru.ylab.levon.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;
import ru.ylab.levon.model.Product;


public class CatalogService {
    private final Map<String, Product> products;

    public CatalogService() {
        this.products = new HashMap<>();
    }

    public CatalogService(Map<String, Product> products) {
        this.products = new HashMap<>(products);
    }

    public void addProduct(@NonNull Product product) {
        products.put(product.getId(), product);
    }

    public Product getProduct(@NonNull String id) {
        return products.get(id);
    }

    public Collection<Product> getAllProductsCollection() {
        return products.values();
    }

    public Map<String, Product> getAllProducts() {
        return new HashMap<>(products);
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

        return name != null || category != null || brand != null || price != null || description != null;
    }

    public List<Product> findByCategory(@NonNull String category) {
        return products.values().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Product> findByBrand(@NonNull String brand) {
        return products.values().stream()
                .filter(p -> p.getBrand().equalsIgnoreCase(brand))
                .collect(Collectors.toList());
    }

    public List<Product> findByPriceRange(double minPrice, double maxPrice) {
        return products.values().stream()
                .filter(p -> p.getPrice() >= minPrice && p.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    public List<Product> search(@NonNull String keyword) {
        String lower = keyword.toLowerCase();
        return products.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(lower)
                        || (p.getDescription() != null && p.getDescription().toLowerCase().contains(lower)))
                .collect(Collectors.toList());
    }
}
