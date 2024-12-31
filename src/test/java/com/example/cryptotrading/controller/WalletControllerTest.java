package com.example.cryptotrading.controller;

import com.example.cryptotrading.dto.WalletResponse;
import com.example.cryptotrading.exception.CryptoTradingException;
import com.example.cryptotrading.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.WalletControllerConstants.BASE_URL;
import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.WalletControllerConstants.GET_WALLET;
import static com.example.cryptotrading.exception.CryptoTradingErrorType.ERR_WALLET_NOT_FOUND;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
@AutoConfigureMockMvc
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Test
    void testGetWalletSuccess() throws Exception {
        String userId = "user123";
        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setUserId(userId);

        when(walletService.getWalletResponse(userId)).thenReturn(walletResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + GET_WALLET, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(userId)));
    }

    @Test
    void testGetWallet_CryptoTradingException() throws Exception {
        String userId = "user123";
        CryptoTradingException exception = new CryptoTradingException(ERR_WALLET_NOT_FOUND, "Wallet not found for userId: " + userId);

        when(walletService.getWalletResponse(userId)).thenThrow(exception);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + GET_WALLET, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetWallet_UnexpectedException() throws Exception {
        String userId = "user123";

        when(walletService.getWalletResponse(userId)).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + GET_WALLET, userId))
                .andExpect(status().isInternalServerError());
    }
}