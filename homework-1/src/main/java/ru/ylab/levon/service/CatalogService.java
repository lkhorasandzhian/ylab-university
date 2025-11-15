package ru.ylab.levon.service;

import lombok.NonNull;
import ru.ylab.levon.model.Product;
import ru.ylab.levon.repository.api.ProductRepository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CatalogService {

    private final ProductRepository repository;
    private final CacheService<String, List<Product>> cache;

    public CatalogService(ProductRepository repository,
                          CacheService<String, List<Product>> cache) {
        this.repository = repository;
        this.cache = cache;
    }

    public void addProduct(@NonNull Product product) {
        repository.save(product);
        cache.clear();
    }

    public Product getProduct(@NonNull String id) {
        return repository.findById(id);
    }

    public Collection<Product> getAllProducts() {
        return repository.findAll();
    }

    public java.util.Map<String, Product> getStorage() {
        return repository.getStorage();
    }

    public void removeProduct(@NonNull String id) {
        repository.delete(id);
        cache.clear();
    }

    public boolean updateProduct(@NonNull String id,
                                 String name,
                                 String category,
                                 String brand,
                                 BigDecimal price,
                                 String description) {

        Product p = repository.findById(id);
        if (p == null) return false;

        if (name != null) p.setName(name);
        if (category != null) p.setCategory(category);
        if (brand != null) p.setBrand(brand);
        if (price != null) p.setPrice(price);
        if (description != null) p.setDescription(description);

        cache.clear();
        return true;
    }

    public List<Product> findByCategory(@NonNull String category) {
        String key = "category:" + category.toLowerCase();
        if (cache.contains(key)) return cache.get(key);

        List<Product> result = repository.findAll().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());

        cache.put(key, result);
        return result;
    }

    public List<Product> findByBrand(@NonNull String brand) {
        String key = "brand:" + brand.toLowerCase();
        if (cache.contains(key)) return cache.get(key);

        List<Product> result = repository.findAll().stream()
                .filter(p -> p.getBrand().equalsIgnoreCase(brand))
                .collect(Collectors.toList());

        cache.put(key, result);
        return result;
    }

    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        String key = "range:" + minPrice + "-" + maxPrice;
        if (cache.contains(key)) return cache.get(key);

        List<Product> result = repository.findAll().stream()
                .filter(p -> p.getPrice().compareTo(minPrice) >= 0
                        && p.getPrice().compareTo(maxPrice) <= 0)
                .collect(Collectors.toList());

        cache.put(key, result);
        return result;
    }

    public List<Product> search(@NonNull String keyword) {
        String key = "search:" + keyword.toLowerCase();
        if (cache.contains(key)) return cache.get(key);

        String lower = keyword.toLowerCase();
        List<Product> result = repository.findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(lower)
                        || (p.getDescription() != null
                        && p.getDescription().toLowerCase().contains(lower)))
                .collect(Collectors.toList());

        cache.put(key, result);
        return result;
    }
}
