package com.example.cryptotrading.client.huobi;

import com.example.cryptotrading.dto.CryptoPriceResponse;
import com.example.cryptotrading.exception.CryptoTradingException;
import com.example.cryptotrading.properties.EndpointProperties;
import com.example.cryptotrading.properties.HuobiEndpointProperties;
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
import static org.mockito.Mockito.*;

class HuobiPriceServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HuobiEndpointProperties huobiEndpointProperties;

    @Mock
    private EndpointProperties endpointProperties;

    @InjectMocks
    private HuobiPriceService huobiPriceService;

    private static final String BEST_PRICES_URL = "https://api.huobi.com/market/detail/merged";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(huobiEndpointProperties.getBestPrices()).thenReturn(endpointProperties);
        when(endpointProperties.toUrl()).thenReturn(BEST_PRICES_URL);
    }

    @Test
    void testGetBestPrices_Success() throws Exception {
        String jsonResponse = "{\"data\": [{" +
                "\"symbol\": \"ethusdt\", \"bid\": 2000.0, \"ask\": 2001.0}, " +
                "{\"symbol\": \"btcusdt\", \"bid\": 30000.0, \"ask\": 30010.0}]}";

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(jsonResponse);

        List<String> tradingPairs = Arrays.asList("ETHUSDT", "BTCUSDT");

        List<CryptoPriceResponse> prices = huobiPriceService.getBestPrices(tradingPairs);

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
        String jsonResponse = "{\"data\": []}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(jsonResponse);

        List<String> tradingPairs = Arrays.asList("ETHUSDT", "BTCUSDT");

        List<CryptoPriceResponse> prices = huobiPriceService.getBestPrices(tradingPairs);

        assertTrue(prices.isEmpty());
    }

    @Test
    void testGetBestPrices_ErrorFetchingData() {
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        List<String> tradingPairs = Arrays.asList("ETHUSDT", "BTCUSDT");

        CryptoTradingException exception = assertThrows(CryptoTradingException.class, () ->
                huobiPriceService.getBestPrices(tradingPairs));

        assertEquals("Error fetching prices from Huobi: 400 BAD_REQUEST", exception.getMessage());
    }

    @Test
    void testGetBestPrices_UnexpectedError() {
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenThrow(new RuntimeException("Unknown error"));

        List<String> tradingPairs = Arrays.asList("ETHUSDT", "BTCUSDT");

        CryptoTradingException exception = assertThrows(CryptoTradingException.class, () ->
                huobiPriceService.getBestPrices(tradingPairs));

        assertEquals("Unexpected error while processing Huobi data: Unknown error", exception.getMessage());
    }

    @Test
    void testGetBestPrices_PairNotFound() throws Exception {
        String jsonResponse = "{\"data\": [{" +
                "\"symbol\": \"ethusdt\", \"bid\": 2000.0, \"ask\": 2001.0}]}";  // No "BTCUSDT" in the response

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(jsonResponse);

        List<String> tradingPairs = Arrays.asList("BTCUSDT"); // "BTCUSDT" is not in the response

        List<CryptoPriceResponse> prices = huobiPriceService.getBestPrices(tradingPairs);

        assertTrue(prices.isEmpty());
    }
}