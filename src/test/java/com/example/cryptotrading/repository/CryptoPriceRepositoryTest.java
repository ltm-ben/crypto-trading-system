package com.example.cryptotrading.repository;

import com.example.cryptotrading.entity.CryptoPrice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CryptoPriceRepositoryTest {
    @Autowired
    private CryptoPriceRepository cryptoPriceRepository;

    @BeforeEach
    void setUp() {
        cryptoPriceRepository.deleteAll();
    }

    @Test
    void testFindByPair() {
        CryptoPrice bitcoinPrice = new CryptoPrice();
        bitcoinPrice.setPair("BTCUSDT");
        bitcoinPrice.setBidPrice(35000.0);
        bitcoinPrice.setAskPrice(34950.0);

        cryptoPriceRepository.save(bitcoinPrice);

        Optional<CryptoPrice> result = cryptoPriceRepository.findByPair("BTCUSDT");

        assertTrue(result.isPresent(), "Expected to find a CryptoPrice for BTCUSDT.");
        assertEquals("BTCUSDT", result.get().getPair(), "The pair should be BTCUSDT.");
        assertEquals(35000.0, result.get().getBidPrice(), "The bid price should be 35000.0.");
        assertEquals(34950.0, result.get().getAskPrice(), "The ask price should be 34950.0.");
    }

    @Test
    void testFindByPair_NotFound() {
        Optional<CryptoPrice> result = cryptoPriceRepository.findByPair("ETHUSDT");

        assertFalse(result.isPresent(), "Expected not to find a CryptoPrice for ETHUSDT.");
    }
}