package com.example.cryptotrading.repository;

import com.example.cryptotrading.entity.TradeTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class TradeTransactionRepositoryTest {
    @Autowired
    private TradeTransactionRepository tradeTransactionRepository;

    @BeforeEach
    void setUp() {
        tradeTransactionRepository.deleteAll();
    }

    @Test
    void testFindByUserId_ExistingUserId() {
        TradeTransaction transaction1 = new TradeTransaction();
        transaction1.setUserId("user1");
        transaction1.setAmount(1000.0);
        transaction1.setType("BUY");
        tradeTransactionRepository.save(transaction1);

        TradeTransaction transaction2 = new TradeTransaction();
        transaction2.setUserId("user1");
        transaction2.setAmount(2000.0);
        transaction2.setType("SELL");
        tradeTransactionRepository.save(transaction2);

        List<TradeTransaction> transactions = tradeTransactionRepository.findByUserId("user1");

        assertEquals(2, transactions.size(), "Expected 2 transactions for user1.");
        assertTrue(transactions.stream().anyMatch(t -> t.getAmount() == 1000.0), "Expected to find a transaction with amount 1000.0");
        assertTrue(transactions.stream().anyMatch(t -> t.getAmount() == 2000.0), "Expected to find a transaction with amount 2000.0");
    }

    @Test
    void testFindByUserId_NoTransactionsForUser() {
        List<TradeTransaction> transactions = tradeTransactionRepository.findByUserId("nonexistentUser");

        assertTrue(transactions.isEmpty(), "Expected no transactions for nonexistentUser.");
    }

    @Test
    void testFindByUserId_EmptyUserId() {
        TradeTransaction transaction = new TradeTransaction();
        transaction.setUserId("");
        transaction.setAmount(500.0);
        transaction.setType("BUY");
        tradeTransactionRepository.save(transaction);

        List<TradeTransaction> transactions = tradeTransactionRepository.findByUserId("");

        assertEquals(1, transactions.size(), "Expected 1 transaction with an empty userId.");
        assertEquals(500.0, transactions.get(0).getAmount(), "Expected the transaction with amount 500.0.");
    }

    @Test
    void testFindByUserId_UserIdWithNoMatchingTransactions() {
        TradeTransaction transaction = new TradeTransaction();
        transaction.setUserId("user2");
        transaction.setAmount(1500.0);
        transaction.setType("SELL");
        tradeTransactionRepository.save(transaction);

        List<TradeTransaction> transactions = tradeTransactionRepository.findByUserId("user1");

        assertTrue(transactions.isEmpty(), "Expected no transactions for user1.");
    }
}