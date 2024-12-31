package com.example.cryptotrading.controller;

import com.example.cryptotrading.CryptoTradingApplication;
import com.example.cryptotrading.client.binance.BinancePriceService;
import com.example.cryptotrading.client.huobi.HuobiPriceService;
import com.example.cryptotrading.dto.CryptoPriceResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.*;
import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.CryptoPriceControllerConstants.BASE_URL;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = CryptoTradingApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integration-test.yaml")
class CryptoPriceControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BinancePriceService binancePriceService;

    @MockBean
    private HuobiPriceService huobiPriceService;

    @Test
    void shouldReturnBestPriceForBtcUsdt() throws Exception {

        List<CryptoPriceResponse> binancePrices = new ArrayList<>();
        CryptoPriceResponse btcUsdtPriceFromBinance = new CryptoPriceResponse();
        btcUsdtPriceFromBinance.setPair(TRADING_PAIR_BTCUSDT);
        btcUsdtPriceFromBinance.setBidPrice(35000.0);
        btcUsdtPriceFromBinance.setAskPrice(40100.0);
        binancePrices.add(btcUsdtPriceFromBinance);
        when(binancePriceService.getBestPrices(SUPPORTED_TRADING_PAIRS)).thenReturn(binancePrices);

        List<CryptoPriceResponse> huobiPrices = new ArrayList<>();
        CryptoPriceResponse btcUsdtPriceFromHuobi = new CryptoPriceResponse();
        btcUsdtPriceFromHuobi.setPair(TRADING_PAIR_BTCUSDT);
        btcUsdtPriceFromHuobi.setBidPrice(40000.0);
        btcUsdtPriceFromHuobi.setAskPrice(45000.0);
        huobiPrices.add(btcUsdtPriceFromHuobi);
        when(huobiPriceService.getBestPrices(SUPPORTED_TRADING_PAIRS)).thenReturn(huobiPrices);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/" + TRADING_PAIR_BTCUSDT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pair", is(TRADING_PAIR_BTCUSDT)))
                .andExpect(jsonPath("$.bidPrice", is(40000.0)))
                .andExpect(jsonPath("$.askPrice", is(40100.0)));
    }

    @Test
    void shouldReturnBestPriceForEthUsdt() throws Exception {

        List<CryptoPriceResponse> binancePrices = new ArrayList<>();
        CryptoPriceResponse ethUsdtPriceFromBinance = new CryptoPriceResponse();
        ethUsdtPriceFromBinance.setPair(TRADING_PAIR_ETHUSDT);
        ethUsdtPriceFromBinance.setBidPrice(2500.0);
        ethUsdtPriceFromBinance.setAskPrice(3000.0);
        binancePrices.add(ethUsdtPriceFromBinance);
        when(binancePriceService.getBestPrices(SUPPORTED_TRADING_PAIRS)).thenReturn(binancePrices);

        List<CryptoPriceResponse> huobiPrices = new ArrayList<>();
        CryptoPriceResponse ethUsdtPriceFromHuobi = new CryptoPriceResponse();
        ethUsdtPriceFromHuobi.setPair(TRADING_PAIR_ETHUSDT);
        ethUsdtPriceFromHuobi.setBidPrice(1800.0);
        ethUsdtPriceFromHuobi.setAskPrice(2550.0);
        huobiPrices.add(ethUsdtPriceFromHuobi);
        when(huobiPriceService.getBestPrices(SUPPORTED_TRADING_PAIRS)).thenReturn(huobiPrices);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/" + TRADING_PAIR_ETHUSDT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pair", is(TRADING_PAIR_ETHUSDT)))
                .andExpect(jsonPath("$.bidPrice", is(2500.0)))
                .andExpect(jsonPath("$.askPrice", is(2550.0)));
    }

    @Test
    void shouldReturnNotFoundForInvalidPair() throws Exception {

        String invalidPair = "INVALID_PAIR";
        
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/" + invalidPair)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}