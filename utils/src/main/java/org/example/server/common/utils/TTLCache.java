package org.example.server.common.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

public class TTLCache<K, V> {
    private final Cache<K, ValueWithExpire<V>> cache;

    public TTLCache(long maxSize, long fallbackExpire, TimeUnit unit) {
        this.cache = CacheBuilder.newBuilder().maximumSize(maxSize).expireAfterWrite(fallbackExpire, unit).build();
    }

    public void put(K key, V value, long ttl, TimeUnit unit) {
        if (value == null) {
            this.cache.invalidate(key);
        } else {
            this.cache.put(key, new ValueWithExpire<>(value, ttl, unit));
        }
    }

    public V getIfPresent(K key) {
        ValueWithExpire<V> wrap = this.cache.getIfPresent(key);
        if (wrap == null) {
            return null;
        } else if (wrap.isExpired()) {
            this.cache.invalidate(key);
            return null;
        } else {
            return wrap.getValue();
        }
    }

    public void invalidate(K key) {
        this.cache.invalidate(key);
    }

    public void invalidateAll() {
        this.cache.invalidateAll();
    }

    private static class ValueWithExpire<V> {
        @Getter
        private final V value;
        private final long expireAt;

        public ValueWithExpire(V value, long ttl, TimeUnit unit) {
            this.value = value;
            this.expireAt = System.currentTimeMillis() + unit.toMillis(ttl);
        }

        public boolean isExpired() {

            return System.currentTimeMillis() > this.expireAt;
        }
    }
}
