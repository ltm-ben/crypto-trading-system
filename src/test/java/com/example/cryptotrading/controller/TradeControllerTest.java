package com.example.cryptotrading.controller;

import com.example.cryptotrading.dto.TradeRequest;
import com.example.cryptotrading.dto.TradeTransactionResponse;
import com.example.cryptotrading.exception.CryptoTradingException;
import com.example.cryptotrading.service.TradeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.TRADE_TYPE_BUY;
import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.TRADING_PAIR_BTCUSDT;
import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.TradeControllerConstants.BASE_URL;
import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.TradeControllerConstants.GET_TRADES;
import static com.example.cryptotrading.exception.CryptoTradingErrorType.ERR_INVALID_TRADE_TYPE;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TradeController.class)
@AutoConfigureMockMvc
class TradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TradeService tradeService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testSuccessfulTrade() throws Exception {
        TradeRequest request = new TradeRequest();
        request.setUserId("user1");
        request.setPair(TRADING_PAIR_BTCUSDT);
        request.setType(TRADE_TYPE_BUY);
        request.setAmount(1.0);
        request.setPrice(42000.0);

        doNothing().when(tradeService).processTrade(any(TradeRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    void testFailedTrade_ValidationException() throws Exception {
        TradeRequest request = new TradeRequest();
        request.setUserId("user1");
        request.setPair(TRADING_PAIR_BTCUSDT);
        request.setType(TRADE_TYPE_BUY);
        request.setAmount(1.0);
        request.setPrice(42000.0);

        doThrow(new CryptoTradingException(ERR_INVALID_TRADE_TYPE, "Invalid trade type"))
                .when(tradeService).processTrade(any(TradeRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testFailedTrade_UnexpectedException() throws Exception {
        TradeRequest request = new TradeRequest();
        request.setUserId("user1");
        request.setPair(TRADING_PAIR_BTCUSDT);
        request.setType(TRADE_TYPE_BUY);
        request.setAmount(1.0);
        request.setPrice(42000.0);

        doThrow(new RuntimeException("Unexpected error"))
                .when(tradeService).processTrade(any(TradeRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetTrades() throws Exception {
        String userId = "user123";
        List<TradeTransactionResponse> responses = List.of(
                new TradeTransactionResponse(),
                new TradeTransactionResponse()
        );

        when(tradeService.getUserTrades(userId)).thenReturn(responses);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + GET_TRADES, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(responses.size())));
    }

    @Test
    void testGetTrades_UnexpectedException() throws Exception {
        String userId = "user123";

        when(tradeService.getUserTrades(userId)).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + GET_TRADES, userId))
                .andExpect(status().isInternalServerError());
    }
}