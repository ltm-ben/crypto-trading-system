package com.example.cryptotrading.service;

import com.example.cryptotrading.dto.CryptoPriceResponse;
import com.example.cryptotrading.entity.CryptoPrice;
import com.example.cryptotrading.exception.CryptoTradingException;
import com.example.cryptotrading.repository.CryptoPriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CryptoPriceServiceTest {
    @InjectMocks
    private CryptoPriceService cryptoPriceService;

    @Mock
    private CryptoPriceRepository cryptoPriceRepository;

    @Mock
    private CacheService cacheService;

    @Mock
    private CryptoPriceResponse cryptoPriceResponse;

    private CryptoPrice cryptoPrice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        cryptoPrice = new CryptoPrice();
        cryptoPrice.setPair("BTCUSDT");
        cryptoPrice.setBidPrice(50000.0);
        cryptoPrice.setAskPrice(50500.0);

        cryptoPriceResponse = new CryptoPriceResponse();
        cryptoPriceResponse.setPair("BTCUSDT");
        cryptoPriceResponse.setBidPrice(50000.0);
        cryptoPriceResponse.setAskPrice(50500.0);

        when(cryptoPriceRepository.findByPair("BTCUSDT")).thenReturn(Optional.of(cryptoPrice));
    }

    @Test
    void testAggregatePrices() {
        CryptoPriceResponse binancePrice = new CryptoPriceResponse();
        binancePrice.setPair("BTCUSDT");
        binancePrice.setBidPrice(51000.0);
        binancePrice.setAskPrice(50550.0);

        CryptoPriceResponse huobiPrice = new CryptoPriceResponse();
        huobiPrice.setPair("BTCUSDT");
        huobiPrice.setBidPrice(49500.0);
        huobiPrice.setAskPrice(50600.0);

        List<CryptoPriceResponse> binancePrices = List.of(binancePrice);
        List<CryptoPriceResponse> huobiPrices = List.of(huobiPrice);

        List<CryptoPriceResponse> aggregatedPrices = cryptoPriceService.aggregatePrices(binancePrices, huobiPrices);

        assertEquals(1, aggregatedPrices.size());
        CryptoPriceResponse aggregatedPrice = aggregatedPrices.get(0);
        assertEquals("BTCUSDT", aggregatedPrice.getPair());
        assertEquals(51000.0, aggregatedPrice.getBidPrice());
        assertEquals(50550.0, aggregatedPrice.getAskPrice());
    }

    @Test
    void testGetBestPrice_CacheHit() throws CryptoTradingException {
        when(cacheService.getFromCache("BTCUSDT")).thenReturn(Optional.of(cryptoPriceResponse));

        CryptoPriceResponse result = cryptoPriceService.getBestPrice("BTCUSDT");

        assertEquals(cryptoPriceResponse, result);
        verify(cacheService, times(1)).getFromCache("BTCUSDT");
        verify(cryptoPriceRepository, never()).findByPair("BTCUSDT");
    }

    @Test
    void testGetBestPrice_CacheMiss_FetchFromDb() throws CryptoTradingException {
        when(cacheService.getFromCache("BTCUSDT")).thenReturn(Optional.empty());
        when(cryptoPriceRepository.findByPair("BTCUSDT")).thenReturn(Optional.of(cryptoPrice));

        CryptoPriceResponse result = cryptoPriceService.getBestPrice("BTCUSDT");

        assertNotNull(result);
        assertEquals("BTCUSDT", result.getPair());
        verify(cacheService, times(1)).getFromCache("BTCUSDT");
        verify(cryptoPriceRepository, times(1)).findByPair("BTCUSDT");
    }

    @Test
    void testGetBestPrice_PriceNotFound() {
        when(cacheService.getFromCache("BTCUSDT")).thenReturn(Optional.empty());
        when(cryptoPriceRepository.findByPair("BTCUSDT")).thenReturn(Optional.empty());

        assertThrows(CryptoTradingException.class, () -> cryptoPriceService.getBestPrice("BTCUSDT"));
    }

    @Test
    void testSavePrices() {
        CryptoPriceResponse price = new CryptoPriceResponse();
        price.setPair("BTCUSDT");
        price.setBidPrice(50000.0);
        price.setAskPrice(50500.0);

        List<CryptoPriceResponse> prices = List.of(price);

        cryptoPriceService.savePrices(prices);

        verify(cryptoPriceRepository, times(1)).saveAndFlush(any(CryptoPrice.class));
        verify(cacheService, times(1)).updateCache("BTCUSDT", price);
    }

    @Test
    void testUpdateOrCreateCryptoPrice_Update() {
        CryptoPriceResponse price = new CryptoPriceResponse();
        price.setPair("BTCUSDT");
        price.setBidPrice(51000.0);
        price.setAskPrice(51500.0);

        when(cryptoPriceRepository.findByPair("BTCUSDT")).thenReturn(Optional.of(cryptoPrice));

        cryptoPriceService.updateOrCreateCryptoPrice(price);

        verify(cryptoPriceRepository, times(1)).saveAndFlush(any(CryptoPrice.class));
    }

    @Test
    void testUpdateOrCreateCryptoPrice_Create() {
        CryptoPriceResponse price = new CryptoPriceResponse();
        price.setPair("BTCUSDT");
        price.setBidPrice(50000.0);
        price.setAskPrice(50500.0);

        when(cryptoPriceRepository.findByPair("BTCUSDT")).thenReturn(Optional.empty());

        cryptoPriceService.updateOrCreateCryptoPrice(price);

        verify(cryptoPriceRepository, times(1)).saveAndFlush(any(CryptoPrice.class));
    }
}