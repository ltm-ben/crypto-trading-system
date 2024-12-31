package com.example.cryptotrading.client.binance;

import com.example.cryptotrading.dto.CryptoPriceResponse;
import com.example.cryptotrading.exception.CryptoTradingException;
import com.example.cryptotrading.properties.BinanceEndpointProperties;
import com.example.cryptotrading.properties.EndpointProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class BinancePriceServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private BinanceEndpointProperties binanceEndpointProperties;

    @Mock
    private EndpointProperties endpointProperties;

    @InjectMocks
    private BinancePriceService binancePriceService;

    private static final String BEST_PRICES_URL = "https://api.binance.com/api/v3/ticker/bookTicker";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(binanceEndpointProperties.getBestPrices()).thenReturn(endpointProperties);
        when(endpointProperties.toUrl()).thenReturn(BEST_PRICES_URL);
    }

    @Test
    void testGetBestPrices_Success() throws Exception {
        String jsonResponse = "[{\"symbol\": \"ETHUSDT\", \"bidPrice\": \"2000.0\", \"askPrice\": \"2001.0\"}, " +
                "{\"symbol\": \"BTCUSDT\", \"bidPrice\": \"30000.0\", \"askPrice\": \"30010.0\"}]";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(jsonResponse);

        List<String> tradingPairs = Arrays.asList("ETHUSDT", "BTCUSDT");

        List<CryptoPriceResponse> prices = binancePriceService.getBestPrices(tradingPairs);

        assertNotNull(prices);
        assertEquals(2, prices.size());

        CryptoPriceResponse ethPrice = prices.stream().filter(p -> p.getPair().equals("ETHUSDT")).findFirst().orElse(null);
        assertNotNull(ethPrice);
        assertEquals(2000.0, ethPrice.getBidPrice());
        assertEquals(2001.0, ethPrice.getAskPrice());

        CryptoPriceResponse btcPrice = prices.stream().filter(p -> p.getPair().equals("BTCUSDT")).findFirst().orElse(null);
        assertNotNull(btcPrice);
        assertEquals(30000.0, btcPrice.getBidPrice());
        assertEquals(30010.0, btcPrice.getAskPrice());
    }

    @Test
    void testGetBestPrices_EmptyResponse() throws Exception {
        String jsonResponse = "[]";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(jsonResponse);

        List<String> tradingPairs = Arrays.asList("ETHUSDT", "BTCUSDT");

        List<CryptoPriceResponse> prices = binancePriceService.getBestPrices(tradingPairs);

        assertTrue(prices.isEmpty());
    }

    @Test
    void testGetBestPrices_ErrorFetchingData() {
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        List<String> tradingPairs = Arrays.asList("ETHUSDT", "BTCUSDT");

        CryptoTradingException exception = assertThrows(CryptoTradingException.class, () ->
                binancePriceService.getBestPrices(tradingPairs));

        assertEquals("Error fetching prices from Binance: 400 BAD_REQUEST", exception.getMessage());
    }

    @Test
    void testGetBestPrices_UnexpectedError() {
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenThrow(new RuntimeException("Unknown error"));

        List<String> tradingPairs = Arrays.asList("ETHUSDT", "BTCUSDT");

        CryptoTradingException exception = assertThrows(CryptoTradingException.class, () ->
                binancePriceService.getBestPrices(tradingPairs));

        assertEquals("Unexpected error while processing Binance data: Unknown error", exception.getMessage());
    }

    @Test
    void testGetBestPrices_PairNotFound() throws Exception {
        String jsonResponse = "[{\"symbol\": \"ETHUSDT\", \"bidPrice\": \"2000.0\", \"askPrice\": \"2001.0\"}]";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(jsonResponse);

        List<String> tradingPairs = Arrays.asList("BTCUSDT"); // "BTCUSDT" is not in the response

        List<CryptoPriceResponse> prices = binancePriceService.getBestPrices(tradingPairs);

        assertTrue(prices.isEmpty());
    }
}