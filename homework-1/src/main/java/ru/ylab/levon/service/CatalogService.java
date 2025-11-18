package ru.ylab.levon.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.NonNull;
import ru.ylab.levon.dto.ProductCreateDto;
import ru.ylab.levon.dto.ProductUpdateDto;
import ru.ylab.levon.model.Product;
import ru.ylab.levon.repository.api.ProductRepository;

/**
 * Сервис для управления каталогом товаров.
 * <p>
 * Предоставляет операции создания, поиска, обновления, удаления товаров,
 * а также кеширование результатов поисковых запросов.
 */
public class CatalogService {

    private final ProductRepository repository;
    private final CacheService<String, List<Product>> cache;

    /**
     * Создаёт сервис каталога.
     *
     * @param repository репозиторий товаров
     * @param cache      кеш для результатов поиска
     */
    public CatalogService(ProductRepository repository,
                          CacheService<String, List<Product>> cache) {
        this.repository = repository;
        this.cache = cache;
    }

    /**
     * Добавляет новый товар в каталог.
     * <p>
     * Выполняет валидацию данных и генерирует уникальный идентификатор товара.
     *
     * @param dto данные для создания нового товара
     * @throws IllegalArgumentException если переданные поля некорректны
     */
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

    /**
     * Возвращает товар по идентификатору.
     *
     * @param id идентификатор товара
     * @return товар или {@code null}, если не найден
     */
    public Product getProduct(@NonNull String id) {
        return repository.findById(id);
    }

    /**
     * Возвращает все товары в каталоге.
     *
     * @return коллекция всех товаров
     */
    public Collection<Product> getAllProducts() {
        return repository.findAll();
    }

    /**
     * Возвращает внутреннее хранилище товаров.
     *
     * @return карта товаров по ID
     */
    public java.util.Map<String, Product> getStorage() {
        return repository.getStorage();
    }

    /**
     * Удаляет товар по идентификатору.
     *
     * @param id идентификатор товара
     */
    public void removeProduct(@NonNull String id) {
        repository.delete(id);
        cache.clear();
    }

    /**
     * Обновляет данные существующего товара.
     * <p>
     * Обновляются только те поля, которые указаны в DTO.
     *
     * @param id  идентификатор товара
     * @param dto обновляемые поля
     * @return {@code true}, если товар обновлён; {@code false}, если не найден
     */
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

    /**
     * Выполняет поиск товаров по категории.
     * <p>
     * Результат кешируется по ключу {@code category:*}.
     *
     * @param category категория товаров
     * @return список найденных товаров
     */
    public List<Product> findByCategory(@NonNull String category) {
        String key = "category:" + category.toLowerCase();
        if (cache.contains(key)) return cache.get(key);

        List<Product> result = repository.findAll().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());

        cache.put(key, result);
        return result;
    }

    /**
     * Выполняет поиск товаров по бренду.
     * <p>
     * Результат кешируется по ключу {@code brand:*}.
     *
     * @param brand бренд товара
     * @return список найденных товаров
     */
    public List<Product> findByBrand(@NonNull String brand) {
        String key = "brand:" + brand.toLowerCase();
        if (cache.contains(key)) return cache.get(key);

        List<Product> result = repository.findAll().stream()
                .filter(p -> p.getBrand().equalsIgnoreCase(brand))
                .collect(Collectors.toList());

        cache.put(key, result);
        return result;
    }

    /**
     * Выполняет поиск товаров в диапазоне цен.
     * <p>
     * Результат кешируется по ключу {@code range:min-max}.
     *
     * @param minPrice минимальная цена
     * @param maxPrice максимальная цена
     * @return список товаров в выбранном диапазоне
     */
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

    /**
     * Выполняет полнотекстовый поиск по названию или описанию товара.
     * <p>
     * Результат кешируется по ключу {@code search:*}.
     *
     * @param keyword ключевое слово
     * @return список товаров, содержащих ключевое слово
     */
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
