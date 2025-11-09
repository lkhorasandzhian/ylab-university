package ru.ylab.levon.service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Универсальный класс {@code CacheService}, реализующий простое кэширование на основе {@link LinkedHashMap}.
 * <p>
 * Использует стратегию удаления «наименее недавно использованных» (LRU — Least Recently Used),
 * автоматически очищая самые старые записи при превышении заданного размера.
 *
 * @param <K> тип ключей
 * @param <V> тип значений
 */
public class CacheService<K, V> {
    private static final float LOAD_FACTOR = 0.75f;
    private final int maxSize;
    private final Map<K, V> cache;

    /**
     * Создаёт новый кэш с ограничением по количеству элементов.
     *
     * @param maxSize максимальное количество записей в кэше
     */
    public CacheService(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<>(maxSize, LOAD_FACTOR, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > CacheService.this.maxSize;
            }
        };
    }

    /**
     * Возвращает значение по ключу, если оно есть в кэше.
     *
     * @param key ключ для поиска
     * @return значение или {@code null}, если отсутствует
     */
    public V get(K key) {
        return cache.get(key);
    }

    /**
     * Добавляет значение в кэш или обновляет существующее.
     *
     * @param key   ключ элемента
     * @param value значение элемента
     */
    public void put(K key, V value) {
        cache.put(key, value);
    }

    /**
     * Проверяет, содержится ли элемент с указанным ключом в кэше.
     *
     * @param key ключ элемента
     * @return {@code true}, если элемент найден; {@code false} — если отсутствует
     */
    public boolean contains(K key) {
        return cache.containsKey(key);
    }

    /**
     * Полностью очищает кэш.
     */
    public void clear() {
        cache.clear();
    }

    /**
     * Возвращает текущее количество элементов в кэше.
     *
     * @return число элементов
     */
    public int size() {
        return cache.size();
    }
}
