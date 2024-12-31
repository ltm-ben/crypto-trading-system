package com.example.cryptotrading.service;

import com.example.cryptotrading.dto.CryptoPriceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CacheServiceTest {
    @Mock
    private CryptoPriceResponse cryptoPriceResponse;

    private CacheService cacheService;

    private static final long CACHE_TTL = 5000;

    @BeforeEach
    void setUp() {
        cacheService = new CacheService(CACHE_TTL);

        cryptoPriceResponse = mock(CryptoPriceResponse.class);
    }

    @Test
    void testGetFromCache_CacheMiss() {
        String pair = "BTCUSDT";

        Optional<CryptoPriceResponse> result = cacheService.getFromCache(pair);

        assertFalse(result.isPresent(), "Cache should miss and return an empty Optional.");
    }

    @Test
    void testGetFromCache_CacheHit() {
        String pair = "BTCUSDT";
        when(cryptoPriceResponse.getPair()).thenReturn(pair);

        cacheService.updateCache(pair, cryptoPriceResponse);

        Optional<CryptoPriceResponse> result = cacheService.getFromCache(pair);

        assertTrue(result.isPresent(), "Cache should hit and return a cached value.");
        assertEquals(cryptoPriceResponse, result.get(), "The returned value should match the cached value.");
    }

    @Test
    void testUpdateCache() {
        String pair = "BTCUSDT";
        CryptoPriceResponse newPrice = new CryptoPriceResponse();
        newPrice.setPair(pair);
        newPrice.setBidPrice(40000.0);
        newPrice.setAskPrice(40100.0);

        cacheService.updateCache(pair, newPrice);

        Optional<CryptoPriceResponse> cachedPrice = cacheService.getFromCache(pair);
        assertTrue(cachedPrice.isPresent(), "Cache should contain the updated entry.");
        assertEquals(newPrice, cachedPrice.get(), "The cached value should match the updated value.");
    }

    @Test
    void testCacheTTL_Expired() {
        String pair = "BTCUSDT";
        CryptoPriceResponse price = new CryptoPriceResponse();
        price.setPair(pair);
        price.setBidPrice(40000.0);
        price.setAskPrice(40100.0);

        cacheService.updateCache(pair, price);

        try {
            Thread.sleep(CACHE_TTL + 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Optional<CryptoPriceResponse> result = cacheService.getFromCache(pair);

        assertFalse(result.isPresent(), "Cache should miss after TTL expiration.");
    }
}