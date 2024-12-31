package com.example.cryptotrading.service;

import com.example.cryptotrading.dto.WalletResponse;
import com.example.cryptotrading.entity.Wallet;
import com.example.cryptotrading.exception.CryptoTradingException;
import com.example.cryptotrading.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {
    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    private WalletResponse walletResponse;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        walletResponse = new WalletResponse();
        walletResponse.setUserId("user123");
        walletResponse.setUsdtBalance(1000.0);
        walletResponse.setEthBalance(0.5);
        walletResponse.setBtcBalance(1.0);

        wallet = new Wallet();
        wallet.setUserId("user123");
        wallet.setUsdtBalance(1000.0);
        wallet.setEthBalance(0.5);
        wallet.setBtcBalance(1.0);
    }

    @Test
    void testGetWalletResponse_Success() throws CryptoTradingException {
        when(walletRepository.findByUserId("user123")).thenReturn(Optional.of(wallet));

        WalletResponse result = walletService.getWalletResponse("user123");

        assertNotNull(result);
        assertEquals("user123", result.getUserId());
        assertEquals(1000.0, result.getUsdtBalance());
        assertEquals(0.5, result.getEthBalance());
        assertEquals(1.0, result.getBtcBalance());

        verify(walletRepository, times(1)).findByUserId("user123");
    }

    @Test
    void testGetWalletResponse_WalletNotFound() {
        when(walletRepository.findByUserId("user123")).thenReturn(Optional.empty());

        CryptoTradingException exception = assertThrows(CryptoTradingException.class, () -> walletService.getWalletResponse("user123"));
        assertEquals("Wallet not found for userId: user123", exception.getMessage());
    }

    @Test
    void testUpdateWallet_Success() throws CryptoTradingException {
        WalletResponse updatedResponse = new WalletResponse();
        updatedResponse.setUserId("user123");
        updatedResponse.setUsdtBalance(2000.0);
        updatedResponse.setEthBalance(1.0);
        updatedResponse.setBtcBalance(0.5);

        when(walletRepository.findByUserId("user123")).thenReturn(Optional.of(wallet));
        when(walletRepository.saveAndFlush(any(Wallet.class))).thenReturn(wallet);

        WalletResponse result = walletService.updateWallet(updatedResponse);

        assertNotNull(result);
        assertEquals("user123", result.getUserId());
        assertEquals(2000.0, result.getUsdtBalance());
        assertEquals(1.0, result.getEthBalance());
        assertEquals(0.5, result.getBtcBalance());

        verify(walletRepository, times(1)).findByUserId("user123");
        verify(walletRepository, times(1)).saveAndFlush(any(Wallet.class));
    }

    @Test
    void testUpdateWallet_WalletNotFound() {
        WalletResponse updatedResponse = new WalletResponse();
        updatedResponse.setUserId("user123");
        updatedResponse.setUsdtBalance(2000.0);
        updatedResponse.setEthBalance(1.0);
        updatedResponse.setBtcBalance(0.5);

        when(walletRepository.findByUserId("user123")).thenReturn(Optional.empty());

        CryptoTradingException exception = assertThrows(CryptoTradingException.class, () -> walletService.updateWallet(updatedResponse));
        assertEquals("Wallet not found for userId: user123", exception.getMessage());
    }
}