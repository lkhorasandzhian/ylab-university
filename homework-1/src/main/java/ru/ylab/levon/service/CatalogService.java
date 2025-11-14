package ru.ylab.levon.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;
import ru.ylab.levon.model.Product;

/**
 * Класс {@code CatalogService} реализует бизнес-логику управления каталогом товаров.
 * <p>
 * Поддерживает операции CRUD, а также поиск и фильтрацию по различным критериям.
 * Данные хранятся в памяти в виде отображения {@code Map<String, Product>},
 * где ключ — уникальный идентификатор товара.
 */
public class CatalogService {
    private final Map<String, Product> products;
    private final CacheService<String, List<Product>> cache;

    private static final int CACHE_SIZE = 20;

    /**
     * Создаёт пустой каталог товаров.
     */
    public CatalogService() {
        this.products = new HashMap<>();
        this.cache = new CacheService<>(CACHE_SIZE);
    }

    /**
     * Создаёт сервис каталога с предзагруженными данными.
     *
     * @param products хранилище товаров, где ключ — идентификатор
     */
    public CatalogService(Map<String, Product> products) {
        this.products = new HashMap<>(products);
        this.cache = new CacheService<>(CACHE_SIZE);
    }

    /**
     * Добавляет товар в каталог.
     *
     * @param product объект товара
     */
    public void addProduct(@NonNull Product product) {
        products.put(product.getId(), product);
        cache.clear();
    }

    /**
     * Возвращает товар по идентификатору.
     *
     * @param id уникальный идентификатор товара
     * @return объект {@link Product} или {@code null}, если не найден
     */
    public Product getProduct(@NonNull String id) {
        return products.get(id);
    }

    /**
     * Возвращает коллекцию всех товаров без ключей.
     *
     * @return коллекция товаров
     */
    public Collection<Product> getAllProductsCollection() {
        return products.values();
    }

    /**
     * Возвращает копию хранилища всех товаров.
     *
     * @return копия {@code Map<String, Product>}
     */
    public Map<String, Product> getAllProducts() {
        return new HashMap<>(products);
    }

    /**
     * Удаляет товар по идентификатору.
     *
     * @param id уникальный идентификатор товара
     */
    public void removeProduct(@NonNull String id) {
        products.remove(id);
        cache.clear();
    }

    /**
     * Обновляет информацию о существующем товаре.
     * <p>
     * Любой параметр, равный {@code null}, пропускается без изменений.
     *
     * @param id          идентификатор товара
     * @param name        новое название (или {@code null})
     * @param category    новая категория (или {@code null})
     * @param brand       новый бренд (или {@code null})
     * @param price       новая цена (или {@code null})
     * @param description новое описание (или {@code null})
     * @return {@code true}, если хотя бы одно поле обновлено; иначе {@code false}
     */
    public boolean updateProduct(@NonNull String id,
                                 String name,
                                 String category,
                                 String brand,
                                 BigDecimal price,
                                 String description) {
        Product product = products.get(id);
        if (product == null) {
            return false;
        }

        if (name != null) {
            product.setName(name);
        }
        if (category != null) {
            product.setCategory(category);
        }
        if (brand != null) {
            product.setBrand(brand);
        }
        if (price != null) {
            product.setPrice(price);
        }
        if (description != null) {
            product.setDescription(description);
        }

        cache.clear();  // Кэш сбрасывается при изменениях

        return name != null || category != null || brand != null || price != null || description != null;
    }

    /**
     * Ищет товары по категории.
     *
     * @param category название категории
     * @return список товаров, соответствующих категории
     */
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

    /**
     * Ищет товары по бренду.
     *
     * @param brand название бренда
     * @return список товаров, соответствующих бренду
     */
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

    /**
     * Ищет товары в заданном диапазоне цен.
     *
     * @param minPrice минимальная цена
     * @param maxPrice максимальная цена
     * @return список товаров, удовлетворяющих диапазону
     */
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        String key = "price:" + minPrice + "-" + maxPrice;
        if (cache.contains(key)) {
            return cache.get(key);
        }

        List<Product> result = products.values().stream()
                .filter(p -> p.getPrice().compareTo(minPrice) >= 0 && p.getPrice().compareTo(maxPrice) <= 0)
                .collect(Collectors.toList());

        cache.put(key, result);
        return result;
    }

    /**
     * Выполняет поиск по ключевому слову в названии или описании.
     *
     * @param keyword строка для поиска
     * @return список товаров, содержащих ключевое слово
     */
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
