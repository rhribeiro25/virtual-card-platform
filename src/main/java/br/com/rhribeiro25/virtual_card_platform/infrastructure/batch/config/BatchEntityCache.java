package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class BatchEntityCache<K, V> {

    private final Map<K, V> cache = new ConcurrentHashMap<>();

    public V getOrCreate(K key, Supplier<V> supplier) {
        return cache.computeIfAbsent(key, k -> supplier.get());
    }

    public void clear() {
        cache.clear();
    }
}
