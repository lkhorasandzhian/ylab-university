package ru.ylab.levon.repository.file;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ru.ylab.levon.model.Product;
import ru.ylab.levon.repository.api.ProductRepository;

/**
 * Файловая реализация {@link ProductRepository}, работающая с данными,
 * загруженными из сериализованного хранилища.
 * <p>
 * Все операции выполняются над внутренней картой товаров.
 */
public class FileProductRepository implements ProductRepository {

    private final Map<String, Product> products;

    /**
     * Создаёт файловый репозиторий товаров.
     *
     * @param initialData предварительно загруженные данные товаров
     */
    public FileProductRepository(Map<String, Product> initialData) {
        this.products = new HashMap<>(initialData);
    }

    /**
     * Сохраняет товар в хранилище.
     * <p>
     * Если товар с таким ID уже существует, он будет перезаписан.
     *
     * @param product сохраняемый товар
     */
    @Override
    public void save(Product product) {
        products.put(product.getId(), product);
    }

    /**
     * Находит товар по его идентификатору.
     *
     * @param id идентификатор товара
     * @return товар или {@code null}, если не найден
     */
    @Override
    public Product findById(String id) {
        return products.get(id);
    }

    /**
     * Удаляет товар по идентификатору.
     *
     * @param id идентификатор товара
     */
    @Override
    public void delete(String id) {
        products.remove(id);
    }

    /**
     * Возвращает коллекцию всех товаров.
     *
     * @return коллекция товаров
     */
    @Override
    public Collection<Product> findAll() {
        return products.values();
    }

    /**
     * Возвращает внутреннее хранилище товаров.
     *
     * @return карта товаров
     */
    @Override
    public Map<String, Product> getStorage() {
        return products;
    }
}
