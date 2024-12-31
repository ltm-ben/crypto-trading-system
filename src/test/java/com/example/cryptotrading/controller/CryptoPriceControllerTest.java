package com.example.cryptotrading.controller;

import com.example.cryptotrading.dto.CryptoPriceResponse;
import com.example.cryptotrading.exception.CryptoTradingException;
import com.example.cryptotrading.service.CryptoPriceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.CryptoPriceControllerConstants.BASE_URL;
import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.CryptoPriceControllerConstants.GET_PRICE;
import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.TRADING_PAIR_BTCUSDT;
import static com.example.cryptotrading.exception.CryptoTradingErrorType.ERR_INVALID_PAIR;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CryptoPriceController.class)
class CryptoPriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CryptoPriceService cryptoPriceService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetPrice_Success() throws Exception {
        String pair = TRADING_PAIR_BTCUSDT;
        Double bidPrice = 50000.0;
        Double askPrice = 50500.0;
        CryptoPriceResponse response = new CryptoPriceResponse(pair, bidPrice, askPrice);
        when(cryptoPriceService.getBestPrice(pair)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + GET_PRICE, pair)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void testGetPrice_CryptoTradingException() throws Exception {
        String pair = TRADING_PAIR_BTCUSDT;
        CryptoTradingException exception = new CryptoTradingException(ERR_INVALID_PAIR, "Invalid pair");
        when(cryptoPriceService.getBestPrice(pair)).thenThrow(exception);

        mockMvc.perform(get(BASE_URL + GET_PRICE, pair)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPrice_InternalServerError() throws Exception {
        String pair = TRADING_PAIR_BTCUSDT;
        when(cryptoPriceService.getBestPrice(pair)).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get(BASE_URL + GET_PRICE, pair)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}