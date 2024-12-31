package com.example.cryptotrading.controller;

import com.example.cryptotrading.CryptoTradingApplication;
import com.example.cryptotrading.client.binance.BinancePriceService;
import com.example.cryptotrading.client.huobi.HuobiPriceService;
import com.example.cryptotrading.dto.CryptoPriceResponse;
import com.example.cryptotrading.dto.TradeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.*;
import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.TradeControllerConstants.BASE_URL;
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
@Sql(scripts = "classpath:data-integration-test.sql")
class TradeControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BinancePriceService binancePriceService;

    @MockBean
    private HuobiPriceService huobiPriceService;

    @Test
    void shouldProcessTradeSuccessfully() throws Exception {

        TradeRequest tradeRequest = new TradeRequest();
        tradeRequest.setUserId("user1");
        tradeRequest.setPair(TRADING_PAIR_BTCUSDT);
        tradeRequest.setType(TRADE_TYPE_BUY);
        tradeRequest.setAmount(1.0);
        tradeRequest.setPrice(42000.0);

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

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(tradeRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnErrorForInvalidTradeType() throws Exception {
        TradeRequest tradeRequest = new TradeRequest();
        tradeRequest.setUserId("user1");
        tradeRequest.setPair(TRADING_PAIR_BTCUSDT);
        tradeRequest.setType("INVALID_TYPE");
        tradeRequest.setAmount(1.0);
        tradeRequest.setPrice(30000.0);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(tradeRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnUserTradesSuccessfully() throws Exception {

        String userId = "user1";

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].pair", is(TRADING_PAIR_BTCUSDT)))
                .andExpect(jsonPath("$[0].type", is(TRADE_TYPE_BUY)))
                .andExpect(jsonPath("$[1].pair", is(TRADING_PAIR_ETHUSDT)))
                .andExpect(jsonPath("$[1].type", is(TRADE_TYPE_SELL)));
    }
}