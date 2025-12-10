package ru.ylab.levon.service.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import ru.ylab.levon.audit.Audit;
import ru.ylab.levon.dto.ProductCreateDto;
import ru.ylab.levon.dto.ProductUpdateDto;
import ru.ylab.levon.model.Product;
import ru.ylab.levon.repository.api.ProductRepository;
import ru.ylab.levon.service.api.CacheService;
import ru.ylab.levon.service.api.ProductService;

/**
 * Сервис для управления каталогом товаров.
 * <p>
 * Предоставляет операции создания, поиска, обновления, удаления товаров,
 * а также кеширование результатов поисковых запросов.
 */
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;
    private final CacheService<String, List<Product>> cacheService;

    /**
     * Создаёт сервис каталога.
     *
     * @param repository   репозиторий товаров
     * @param cacheService кеш для результатов поиска
     */
    public ProductServiceImpl(ProductRepository repository, CacheService<String, List<Product>> cacheService) {
        this.repository = repository;
        this.cacheService = cacheService;
    }

    /**
     * Добавляет новый товар в каталог.
     * <p>
     * Выполняет валидацию данных и генерирует уникальный идентификатор товара через БД.
     *
     * @param dto данные для создания нового товара
     * @return созданный продукт
     * @throws IllegalArgumentException если переданные поля некорректны
     */
    @Override
    @Audit("CREATE_PRODUCT")
    public Product addProduct(@NonNull ProductCreateDto dto) {
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
                null,
                dto.name(),
                dto.category(),
                dto.brand(),
                dto.price(),
                dto.description()
        );

        repository.save(product);
        cacheService.clear();

        return product;
    }

    /**
     * Возвращает товар по идентификатору.
     *
     * @param id идентификатор товара
     * @return товар или {@code null}, если не найден
     */
    @Override
    public Product getProduct(@NonNull Long id) {
        return repository.findById(id);
    }

    /**
     * Возвращает все товары в каталоге.
     *
     * @return коллекция всех товаров
     */
    @Override
    public Collection<Product> getAllProducts() {
        return repository.findAll();
    }

    /**
     * Удаляет товар по идентификатору.
     *
     * @param id идентификатор товара
     */
    @Override
    @Audit("DELETE_PRODUCT")
    public void removeProduct(@NonNull Long id) {
        repository.delete(id);
        cacheService.clear();
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
    @Override
    @Audit("UPDATE_PRODUCT")
    public boolean updateProduct(@NonNull Long id, @NonNull ProductUpdateDto dto) {
        Product p = repository.findById(id);
        if (p == null) {
            return false;
        }

        if (dto.name() != null) {
            if (dto.name().isBlank()) {
                throw new IllegalArgumentException("Название товара не может быть пустым.");
            }
            p.setName(dto.name());
        }
        if (dto.category() != null) {
            if (dto.category().isBlank()) {
                throw new IllegalArgumentException("Категория не может быть пустой.");
            }
            p.setCategory(dto.category());
        }
        if (dto.brand() != null) {
            if (dto.brand().isBlank()) {
                throw new IllegalArgumentException("Бренд не может быть пустым.");
            }
            p.setBrand(dto.brand());
        }
        if (dto.price() != null) {
            if (dto.price().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Цена должна быть больше 0.");
            }
            p.setPrice(dto.price());
        }
        if (dto.description() != null) {
            p.setDescription(dto.description());
        }

        repository.save(p);
        cacheService.clear();

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
    @Override
    public List<Product> findByCategory(@NonNull String category) {
        String key = "category:" + category.toLowerCase();
        if (cacheService.contains(key)) {
            return cacheService.get(key);
        }

        List<Product> result = repository.findAll().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());

        cacheService.put(key, result);
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
    @Override
    public List<Product> findByBrand(@NonNull String brand) {
        String key = "brand:" + brand.toLowerCase();
        if (cacheService.contains(key)) {
            return cacheService.get(key);
        }

        List<Product> result = repository.findAll().stream()
                .filter(p -> p.getBrand().equalsIgnoreCase(brand))
                .collect(Collectors.toList());

        cacheService.put(key, result);
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
    @Override
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        String key = "range:" + minPrice + "-" + maxPrice;
        if (cacheService.contains(key)) {
            return cacheService.get(key);
        }

        List<Product> result = repository.findAll().stream()
                .filter(p -> p.getPrice().compareTo(minPrice) >= 0
                        && p.getPrice().compareTo(maxPrice) <= 0)
                .collect(Collectors.toList());

        cacheService.put(key, result);
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
    @Override
    public List<Product> search(@NonNull String keyword) {
        String key = "search:" + keyword.toLowerCase();
        if (cacheService.contains(key)) {
            return cacheService.get(key);
        }

        String lower = keyword.toLowerCase();
        List<Product> result = repository.findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(lower)
                        || (p.getDescription() != null
                        && p.getDescription().toLowerCase().contains(lower)))
                .collect(Collectors.toList());

        cacheService.put(key, result);
        return result;
    }
}
