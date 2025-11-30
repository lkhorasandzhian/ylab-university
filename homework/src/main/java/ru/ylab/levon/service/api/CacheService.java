package ru.ylab.levon.service.api;

public interface CacheService<K, V> {
    V get(K key);

    void put(K key, V value);

    boolean contains(K key);

    void clear();

    int size();
}
