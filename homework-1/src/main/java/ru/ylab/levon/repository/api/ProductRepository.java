package ru.ylab.levon.repository.api;

import java.util.Collection;
import java.util.Map;

import ru.ylab.levon.model.Product;

/**
 * Репозиторий для управления товарами.
 * <p>
 * Определяет базовые CRUD-операции и доступ к внутреннему хранилищу товаров.
 */
public interface ProductRepository {

    /**
     * Сохраняет товар в хранилище.
     * <p>
     * Если товар с таким ID уже существует, он должен быть перезаписан.
     *
     * @param product товар для сохранения
     */
    void save(Product product);

    /**
     * Находит товар по его идентификатору.
     *
     * @param id идентификатор товара
     * @return товар или {@code null}, если не найден
     */
    Product findById(String id);

    /**
     * Удаляет товар по его идентификатору.
     *
     * @param id идентификатор удаляемого товара
     */
    void delete(String id);

    /**
     * Возвращает коллекцию всех товаров.
     *
     * @return коллекция товаров
     */
    Collection<Product> findAll();

    /**
     * Возвращает внутреннее хранилище товаров.
     *
     * @return карта товаров по их идентификаторам
     */
    Map<String, Product> getStorage();
}
