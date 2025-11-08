package ru.ylab.levon.service;

import java.util.LinkedHashMap;
import java.util.Map;

public class CacheService<K, V> {
    private static final float LOAD_FACTOR = 0.75f;
    private final int maxSize;
    private final Map<K, V> cache;

    public CacheService(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<>(maxSize, LOAD_FACTOR, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > CacheService.this.maxSize;
            }
        };
    }

    public V get(K key) {
        return cache.get(key);
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }

    public boolean contains(K key) {
        return cache.containsKey(key);
    }

    public void clear() {
        cache.clear();
    }

    public int size() {
        return cache.size();
    }
}
