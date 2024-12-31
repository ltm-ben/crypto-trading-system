package com.example.cryptotrading.scheduler;

import com.example.cryptotrading.dto.CryptoPriceResponse;
import com.example.cryptotrading.entity.CryptoPrice;
import com.example.cryptotrading.exception.CryptoTradingException;
import com.example.cryptotrading.repository.CryptoPriceRepository;
import com.example.cryptotrading.service.CacheService;
import com.example.cryptotrading.service.CryptoPriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PriceAggregatorTest {
    @Mock
    private CryptoPriceRepository cryptoPriceRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private CryptoPriceService cryptoPriceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetBestPrice_CacheHit() throws CryptoTradingException {
        String pair = "BTCUSDT";
        CryptoPriceResponse cachedPrice = new CryptoPriceResponse(pair, 40000.0, 40100.0);
        when(cacheService.getFromCache(pair)).thenReturn(Optional.of(cachedPrice));

        CryptoPriceResponse result = cryptoPriceService.getBestPrice(pair);

        assertNotNull(result);
        assertEquals(pair, result.getPair());
        assertEquals(40000.0, result.getBidPrice());
        assertEquals(40100.0, result.getAskPrice());
        verify(cacheService, times(1)).getFromCache(pair);
    }

    @Test
    void testGetBestPrice_CacheMiss_FetchFromDb() throws CryptoTradingException {
        String pair = "BTCUSDT";

        when(cacheService.getFromCache(pair)).thenReturn(Optional.empty());

        CryptoPrice cryptoPrice = new CryptoPrice();
        cryptoPrice.setPair(pair);
        cryptoPrice.setBidPrice(40000.0);
        cryptoPrice.setAskPrice(40100.0);

        when(cryptoPriceRepository.findByPair(pair)).thenReturn(Optional.of(cryptoPrice));

        CryptoPriceResponse result = cryptoPriceService.getBestPrice(pair);

        assertNotNull(result);
        assertEquals(pair, result.getPair());
        assertEquals(40000.0, result.getBidPrice());
        assertEquals(40100.0, result.getAskPrice());

        verify(cacheService, times(1)).getFromCache(pair);
        verify(cryptoPriceRepository, times(1)).findByPair(pair);
        verify(cacheService, times(1)).updateCache(pair, result);
    }

    @Test
    void testGetBestPrice_CacheMiss_DbMiss() {
        String pair = "BTCUSDT";
        when(cacheService.getFromCache(pair)).thenReturn(Optional.empty());
        when(cryptoPriceRepository.findByPair(pair)).thenReturn(Optional.empty());

        CryptoTradingException exception = assertThrows(CryptoTradingException.class, () -> cryptoPriceService.getBestPrice(pair));

        assertEquals("Price not found for pair: " + pair, exception.getMessage());
        verify(cacheService, times(1)).getFromCache(pair);
        verify(cryptoPriceRepository, times(1)).findByPair(pair);
    }

    @Test
    void testSavePrices() {
        CryptoPriceResponse price1 = new CryptoPriceResponse("BTCUSDT", 40000.0, 40100.0);
        CryptoPriceResponse price2 = new CryptoPriceResponse("ETHUSDT", 2500.0, 2550.0);
        List<CryptoPriceResponse> prices = Arrays.asList(price1, price2);

        cryptoPriceService.savePrices(prices);

        verify(cryptoPriceRepository, times(2)).saveAndFlush(any(CryptoPrice.class));
        verify(cacheService, times(2)).updateCache(anyString(), any(CryptoPriceResponse.class));
    }

    @Test
    void testUpdateOrCreateCryptoPrice_UpdateExistingPrice() {
        CryptoPriceResponse price = new CryptoPriceResponse("BTCUSDT", 40000.0, 40100.0);
        CryptoPrice existingCryptoPrice = new CryptoPrice();
        existingCryptoPrice.setPair("BTCUSDT");
        existingCryptoPrice.setBidPrice(39000.0);
        existingCryptoPrice.setAskPrice(39500.0);

        when(cryptoPriceRepository.findByPair(price.getPair())).thenReturn(Optional.of(existingCryptoPrice));

        cryptoPriceService.updateOrCreateCryptoPrice(price);

        assertEquals(40000.0, existingCryptoPrice.getBidPrice());
        assertEquals(40100.0, existingCryptoPrice.getAskPrice());
        verify(cryptoPriceRepository, times(1)).saveAndFlush(existingCryptoPrice);
    }

    @Test
    void testUpdateOrCreateCryptoPrice_CreateNewPrice() {
        CryptoPriceResponse price = new CryptoPriceResponse("BTCUSDT", 40000.0, 40100.0);
        when(cryptoPriceRepository.findByPair(price.getPair())).thenReturn(Optional.empty());

        cryptoPriceService.updateOrCreateCryptoPrice(price);

        verify(cryptoPriceRepository, times(1)).saveAndFlush(any(CryptoPrice.class));
    }
}