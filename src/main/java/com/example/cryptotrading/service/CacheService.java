package com.example.cryptotrading.service;

import com.example.cryptotrading.dto.CryptoPriceResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class CacheService {
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final long cacheTTL;

    public CacheService(@Value("${application.cache.ttl}") long cacheTTL) {
        this.cacheTTL = cacheTTL;
    }

    public Optional<CryptoPriceResponse> getFromCache(String pair) {
        CacheEntry cacheEntry = cache.get(pair);

        if (cacheEntry != null && System.currentTimeMillis() - cacheEntry.getTimestamp() < cacheTTL) {
            return Optional.of(cacheEntry.getCryptoPrice());
        }

        return Optional.empty();
    }

    public void updateCache(String pair, CryptoPriceResponse cryptoPrice) {
        cache.put(pair, new CacheEntry(cryptoPrice, System.currentTimeMillis()));
    }

    @Data
    @AllArgsConstructor
    private static class CacheEntry {
        private CryptoPriceResponse cryptoPrice;
        private long timestamp;
    }
}