package ru.ylab.levon.service.api;

import ru.ylab.levon.dto.ProductCreateDto;
import ru.ylab.levon.dto.ProductUpdateDto;
import ru.ylab.levon.model.Product;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * Интерфейс сервиса для управления каталогом товаров.
 * <p>
 * Определяет операции создания, получения, обновления, удаления товаров,
 * а также различные варианты поиска по их характеристикам.
 */
public interface ProductService {
    /**
     * Добавляет новый товар в каталог.
     *
     * @param dto данные для создания товара
     * @return созданный товар
     * @throws IllegalArgumentException если данные некорректны
     */
    Product addProduct(ProductCreateDto dto);

    /**
     * Возвращает товар по идентификатору.
     *
     * @param id идентификатор товара
     * @return найденный товар или {@code null}, если он отсутствует
     */
    Product getProduct(Long id);

    /**
     * Возвращает коллекцию всех товаров каталога.
     *
     * @return коллекция товаров
     */
    Collection<Product> getAllProducts();

    /**
     * Удаляет товар по идентификатору.
     *
     * @param id идентификатор товара
     */
    void removeProduct(Long id);

    /**
     * Обновляет данные существующего товара.
     * <p>
     * Обновляются только поля, указанные в DTO.
     *
     * @param id  идентификатор товара
     * @param dto данные для изменения
     * @return {@code true}, если товар обновлён; {@code false}, если не найден
     */
    boolean updateProduct(Long id, ProductUpdateDto dto);

    /**
     * Выполняет поиск товаров по категории.
     *
     * @param category категория товаров
     * @return список найденных товаров
     */
    List<Product> findByCategory(String category);

    /**
     * Выполняет поиск товаров по бренду.
     *
     * @param brand название бренда
     * @return список найденных товаров
     */
    List<Product> findByBrand(String brand);

    /**
     * Ищет товары, попадающие в заданный диапазон цен.
     *
     * @param minPrice минимальная цена
     * @param maxPrice максимальная цена
     * @return список товаров в указанном диапазоне
     */
    List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Выполняет полнотекстовый поиск по названию или описанию товара.
     *
     * @param keyword ключевое слово для поиска
     * @return список совпадающих товаров
     */
    List<Product> search(String keyword);
}
