package br.com.rhribeiro25.virtual_card_platform.shared.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class BatchCacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager =
                new CaffeineCacheManager("card-cache", "provider-cache");

        manager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(500_000)
                        .expireAfterWrite(1, TimeUnit.HOURS)
        );

        return manager;
    }
}