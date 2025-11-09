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
    private final CacheService<String, List<Product>> cache;

    private static final int CACHE_SIZE = 20;

    public CatalogService() {
        this.products = new HashMap<>();
        this.cache = new CacheService<>(CACHE_SIZE);
    }

    public CatalogService(Map<String, Product> products) {
        this.products = new HashMap<>(products);
        this.cache = new CacheService<>(CACHE_SIZE);
    }

    public void addProduct(@NonNull Product product) {
        products.put(product.getId(), product);
        cache.clear();
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
        cache.clear();
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

        cache.clear();  // Кэш сбрасывается при изменениях

        return name != null || category != null || brand != null || price != null || description != null;
    }

    public List<Product> findByCategory(@NonNull String category) {
        String key = "category:" + category.toLowerCase();
        if (cache.contains(key)) {
            return cache.get(key);
        }

        List<Product> result = products.values().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());

        cache.put(key, result);
        return result;
    }

    public List<Product> findByBrand(@NonNull String brand) {
        String key = "brand:" + brand.toLowerCase();
        if (cache.contains(key)) {
            return cache.get(key);
        }

        List<Product> result = products.values().stream()
                .filter(p -> p.getBrand().equalsIgnoreCase(brand))
                .collect(Collectors.toList());

        cache.put(key, result);
        return result;
    }

    public List<Product> findByPriceRange(double minPrice, double maxPrice) {
        String key = "price:" + minPrice + "-" + maxPrice;
        if (cache.contains(key)) {
            return cache.get(key);
        }

        List<Product> result = products.values().stream()
                .filter(p -> p.getPrice() >= minPrice && p.getPrice() <= maxPrice)
                .collect(Collectors.toList());

        cache.put(key, result);
        return result;
    }

    public List<Product> search(@NonNull String keyword) {
        String key = "search:" + keyword.toLowerCase();
        if (cache.contains(key)) {
            return cache.get(key);
        }

        String lower = keyword.toLowerCase();
        List<Product> result = products.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(lower)
                        || (p.getDescription() != null && p.getDescription().toLowerCase().contains(lower)))
                .collect(Collectors.toList());

        cache.put(key, result);
        return result;
    }
}
