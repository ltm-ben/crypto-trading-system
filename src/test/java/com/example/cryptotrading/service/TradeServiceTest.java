package com.example.cryptotrading.service;

import com.example.cryptotrading.dto.CryptoPriceResponse;
import com.example.cryptotrading.dto.TradeRequest;
import com.example.cryptotrading.dto.TradeTransactionResponse;
import com.example.cryptotrading.dto.WalletResponse;
import com.example.cryptotrading.entity.TradeTransaction;
import com.example.cryptotrading.exception.CryptoTradingException;
import com.example.cryptotrading.repository.TradeTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.*;
import static com.example.cryptotrading.exception.CryptoTradingErrorType.ERR_INVALID_PAIR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {
    @Mock
    private WalletService walletService;

    @Mock
    private CryptoPriceService cryptoPriceService;

    @Mock
    private TradeTransactionRepository tradeTransactionRepository;

    @InjectMocks
    private TradeService tradeService;

    private TradeRequest tradeRequest;
    private WalletResponse walletResponse;
    private CryptoPriceResponse cryptoPriceResponse;

    @BeforeEach
    void setUp() {
        walletResponse = new WalletResponse();
        walletResponse.setUsdtBalance(1000.0);
        walletResponse.setEthBalance(1.0);
        walletResponse.setBtcBalance(0.5);

        cryptoPriceResponse = new CryptoPriceResponse();
        cryptoPriceResponse.setBidPrice(490.0);
        cryptoPriceResponse.setAskPrice(500.0);

        tradeRequest = new TradeRequest();
        tradeRequest.setUserId("user123");
        tradeRequest.setPair(TRADING_PAIR_ETHUSDT);
        tradeRequest.setAmount(1.0);
        tradeRequest.setPrice(500.0);
        tradeRequest.setType(TRADE_TYPE_BUY);
    }

    @Test
    void testProcessTrade_Buy_Success() throws CryptoTradingException {
        when(walletService.getWalletResponse("user123")).thenReturn(walletResponse);
        when(cryptoPriceService.getBestPrice(TRADING_PAIR_ETHUSDT)).thenReturn(cryptoPriceResponse);

        tradeService.processTrade(tradeRequest);

        assertEquals(500.0, walletResponse.getUsdtBalance(), "USDT balance should be reduced by 500.0");
        assertEquals(2.0, walletResponse.getEthBalance(), "ETH balance should be increased by 1.0");

        verify(tradeTransactionRepository, times(1)).saveAndFlush(any());
    }

    @Test
    void testProcessTrade_Sell_Success() throws CryptoTradingException {
        tradeRequest.setType(TRADE_TYPE_SELL);
        tradeRequest.setPrice(490.0);

        when(walletService.getWalletResponse("user123")).thenReturn(walletResponse);
        when(cryptoPriceService.getBestPrice(TRADING_PAIR_ETHUSDT)).thenReturn(cryptoPriceResponse);

        tradeService.processTrade(tradeRequest);

        assertEquals(1490.0, walletResponse.getUsdtBalance(), "USDT balance should be increased to 1490.0");
        assertEquals(0.0, walletResponse.getEthBalance(), "ETH balance should be reduced to 0.0");

        verify(tradeTransactionRepository, times(1)).saveAndFlush(any());
    }

    @Test
    void testProcessTrade_InsufficientFunds_ThrowsException() throws CryptoTradingException {
        tradeRequest.setPrice(1200.0);

        when(walletService.getWalletResponse("user123")).thenReturn(walletResponse);
        when(cryptoPriceService.getBestPrice(TRADING_PAIR_ETHUSDT)).thenReturn(cryptoPriceResponse);

        assertThrows(CryptoTradingException.class, () -> tradeService.processTrade(tradeRequest));
    }

    @Test
    void testProcessTrade_InsufficientCurrency_ThrowsException() throws CryptoTradingException {
        tradeRequest.setType(TRADE_TYPE_SELL);
        tradeRequest.setAmount(2.0);

        when(walletService.getWalletResponse("user123")).thenReturn(walletResponse);
        when(cryptoPriceService.getBestPrice(TRADING_PAIR_ETHUSDT)).thenReturn(cryptoPriceResponse);

        assertThrows(CryptoTradingException.class, () -> tradeService.processTrade(tradeRequest));
    }

    @Test
    void testProcessTrade_InvalidPair_ThrowsException() throws CryptoTradingException {
        tradeRequest.setPair("INVALID_PAIR");

        when(walletService.getWalletResponse("user123")).thenReturn(walletResponse);
        when(cryptoPriceService.getBestPrice("INVALID_PAIR")).thenThrow(new CryptoTradingException(ERR_INVALID_PAIR, "Unsupported trading pair"));

        assertThrows(CryptoTradingException.class, () -> tradeService.processTrade(tradeRequest));
    }

    @Test
    void testProcessTrade_InvalidTradeType_ThrowsException() {
        tradeRequest.setType("INVALID_TYPE");

        assertThrows(CryptoTradingException.class, () -> tradeService.processTrade(tradeRequest));
    }

    @Test
    void testProcessTrade_Buy_PriceMismatch_ThrowsException() throws CryptoTradingException {
        tradeRequest.setPrice(400.0);

        when(walletService.getWalletResponse("user123")).thenReturn(walletResponse);
        when(cryptoPriceService.getBestPrice(TRADING_PAIR_ETHUSDT)).thenReturn(cryptoPriceResponse);

        assertThrows(CryptoTradingException.class, () -> tradeService.processTrade(tradeRequest));
    }

    @Test
    void testProcessTrade_Sell_PriceMismatch_ThrowsException() throws CryptoTradingException {
        tradeRequest.setType(TRADE_TYPE_SELL);
        tradeRequest.setPrice(600.0);

        when(walletService.getWalletResponse("user123")).thenReturn(walletResponse);
        when(cryptoPriceService.getBestPrice(TRADING_PAIR_ETHUSDT)).thenReturn(cryptoPriceResponse);

        assertThrows(CryptoTradingException.class, () -> tradeService.processTrade(tradeRequest));
    }

    @Test
    void testGetUserTrades_EmptyList() {
        when(tradeTransactionRepository.findByUserId("user123")).thenReturn(Collections.emptyList());

        List<TradeTransactionResponse> userTrades = tradeService.getUserTrades("user123");

        assertNotNull(userTrades);
        assertTrue(userTrades.isEmpty(), "The trade list should be empty.");
    }

    @Test
    void testGetUserTrades_NonEmptyList() {
        TradeTransaction tradeTransaction = new TradeTransaction();
        tradeTransaction.setUserId("user123");
        tradeTransaction.setPair(TRADING_PAIR_ETHUSDT);
        tradeTransaction.setAmount(1.0);
        tradeTransaction.setPrice(500.0);
        tradeTransaction.setType(TRADE_TYPE_BUY);
        tradeTransaction.setTimestamp(LocalDateTime.now());

        when(tradeTransactionRepository.findByUserId("user123")).thenReturn(List.of(tradeTransaction));

        List<TradeTransactionResponse> userTrades = tradeService.getUserTrades("user123");

        assertNotNull(userTrades);
        assertEquals(1, userTrades.size(), "The trade list should contain one trade.");
        assertEquals(TRADE_TYPE_BUY, userTrades.get(0).getType(), "The trade type should be BUY.");
    }
}