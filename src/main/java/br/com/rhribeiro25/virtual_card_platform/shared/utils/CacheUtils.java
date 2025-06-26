package br.com.rhribeiro25.virtual_card_platform.shared.utils;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CacheUtils {

    private static CacheManager cacheManager;

    public CacheUtils(CacheManager injectedCacheManager) {
        CacheUtils.cacheManager = injectedCacheManager;
    }

    public static <T> T getFromCache(String cacheName, Object key, Class<T> clazz) {
        return Optional.ofNullable(cacheManager.getCache(cacheName))
                .map(cache -> cache.get(key))
                .map(Cache.ValueWrapper::get)
                .map(clazz::cast)
                .orElse(null);
    }

    public static boolean isInCache(String cacheName, Object key) {
        return Optional.ofNullable(cacheManager.getCache(cacheName))
                .map(cache -> cache.get(key))
                .isPresent();
    }
}
