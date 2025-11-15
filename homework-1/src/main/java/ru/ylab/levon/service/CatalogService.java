package ru.ylab.levon.service;

import lombok.NonNull;
import ru.ylab.levon.dto.ProductCreateDto;
import ru.ylab.levon.dto.ProductUpdateDto;
import ru.ylab.levon.model.Product;
import ru.ylab.levon.repository.api.ProductRepository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CatalogService {

    private final ProductRepository repository;
    private final CacheService<String, List<Product>> cache;

    public CatalogService(ProductRepository repository,
                          CacheService<String, List<Product>> cache) {
        this.repository = repository;
        this.cache = cache;
    }

    public void addProduct(@NonNull ProductCreateDto dto) {
        if (dto.name().isBlank()) {
            throw new IllegalArgumentException("Название товара не может быть пустым.");
        }

        if (dto.category().isBlank()) {
            throw new IllegalArgumentException("Категория не может быть пустой.");
        }

        if (dto.brand().isBlank()) {
            throw new IllegalArgumentException("Бренд не может быть пустым.");
        }

        if (dto.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Цена должна быть больше 0.");
        }

        Product product = new Product(
                UUID.randomUUID().toString(),
                dto.name(),
                dto.category(),
                dto.brand(),
                dto.price(),
                dto.description()
        );

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

    public boolean updateProduct(@NonNull String id, @NonNull ProductUpdateDto dto) {
        Product p = repository.findById(id);
        if (p == null) {
            return false;
        }

        if (dto.name() != null) {
            p.setName(dto.name());
        }
        if (dto.category() != null) {
            p.setCategory(dto.category());
        }
        if (dto.brand() != null) {
            p.setBrand(dto.brand());
        }
        if (dto.price() != null) {
            p.setPrice(dto.price());
        }
        if (dto.description() != null) {
            p.setDescription(dto.description());
        }

        repository.save(p);
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
