package com.example.cryptotrading.repository;

import com.example.cryptotrading.entity.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class WalletRepositoryTest {
    @Autowired
    private WalletRepository walletRepository;

    @BeforeEach
    void setUp() {
        walletRepository.deleteAll();
    }

    @Test
    void testFindByUserId_ExistingUserId() {
        Wallet wallet = new Wallet();
        wallet.setUserId("user1");
        wallet.setUsdtBalance(1000.0);
        wallet.setBtcBalance(2.5);
        wallet.setEthBalance(50.0);
        walletRepository.save(wallet);

        Optional<Wallet> result = walletRepository.findByUserId("user1");

        assertTrue(result.isPresent(), "Expected to find a wallet for user1.");
        assertEquals("user1", result.get().getUserId(), "The userId should be user1.");
        assertEquals(1000.0, result.get().getUsdtBalance(), "The USDT balance should be 1000.0.");
    }

    @Test
    void testFindByUserId_NoWalletForUser() {
        Optional<Wallet> result = walletRepository.findByUserId("nonexistentUser");

        assertFalse(result.isPresent(), "Expected no wallet for nonexistentUser.");
    }

    @Test
    void testFindByUserId_EmptyUserId() {
        Wallet wallet = new Wallet();
        wallet.setUserId("");
        wallet.setUsdtBalance(500.0);
        wallet.setBtcBalance(1.0);
        wallet.setEthBalance(20.0);
        walletRepository.save(wallet);

        Optional<Wallet> result = walletRepository.findByUserId("");

        assertTrue(result.isPresent(), "Expected to find a wallet with an empty userId.");
        assertEquals("", result.get().getUserId(), "The userId should be empty.");
        assertEquals(500.0, result.get().getUsdtBalance(), "The USDT balance should be 500.0.");
    }

    @Test
    void testFindByUserId_UserIdWithNoMatchingWallet() {
        Wallet wallet = new Wallet();
        wallet.setUserId("user2");
        wallet.setUsdtBalance(2000.0);
        wallet.setBtcBalance(1.5);
        wallet.setEthBalance(30.0);
        walletRepository.save(wallet);

        Optional<Wallet> result = walletRepository.findByUserId("user1");

        assertFalse(result.isPresent(), "Expected no wallet for user1.");
    }
}