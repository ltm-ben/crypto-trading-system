package com.example.cryptotrading.controller;

import com.example.cryptotrading.CryptoTradingApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.WalletControllerConstants.BASE_URL;
import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.WalletControllerConstants.GET_WALLET;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = CryptoTradingApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integration-test.yaml")
class WalletControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetWallet_Success() throws Exception {
        String userId = "user1";

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + GET_WALLET, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.usdtBalance").value(50000.0))
                .andExpect(jsonPath("$.ethBalance").value(0.0))
                .andExpect(jsonPath("$.btcBalance").value(0.0));
    }

    @Test
    void testGetWallet_NotFound() throws Exception {
        String userId = "nonExistentUser";

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + GET_WALLET, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}