package ru.ylab.levon.service.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import ru.ylab.levon.service.api.CacheService;

/**
 * Универсальный сервис кеширования на основе {@link LinkedHashMap}.
 * <p>
 * Использует стратегию удаления «наименее недавно использованных» (LRU — Least Recently Used),
 * автоматически очищая самые старые записи при превышении заданного размера.
 *
 * @param <K> тип ключей
 * @param <V> тип значений
 */
@Service
public class CacheServiceImpl<K, V> implements CacheService<K, V> {
    private static final float LOAD_FACTOR = 0.75f;

    private final int maxSize;
    private final Map<K, V> cache;

    public CacheServiceImpl() {
        this(20);
    }

    /**
     * Создаёт новый кеш с ограничением по числу элементов.
     *
     * @param maxSize максимальное количество записей
     */
    public CacheServiceImpl(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<>(maxSize, LOAD_FACTOR, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > CacheServiceImpl.this.maxSize;
            }
        };
    }

    /**
     * Возвращает значение по ключу.
     *
     * @param key ключ элемента
     * @return значение или {@code null}, если элемент отсутствует
     */
    @Override
    public V get(K key) {
        return cache.get(key);
    }

    /**
     * Добавляет элемент в кеш или обновляет существующий.
     *
     * @param key   ключ элемента
     * @param value сохраняемое значение
     */
    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    /**
     * Проверяет, присутствует ли элемент с данным ключом.
     *
     * @param key ключ элемента
     * @return {@code true}, если элемент найден, иначе {@code false}
     */
    @Override
    public boolean contains(K key) {
        return cache.containsKey(key);
    }

    /**
     * Полностью очищает кеш.
     */
    @Override
    public void clear() {
        cache.clear();
    }

    /**
     * Возвращает текущее количество элементов в кеше.
     *
     * @return размер кеша
     */
    @Override
    public int size() {
        return cache.size();
    }
}
