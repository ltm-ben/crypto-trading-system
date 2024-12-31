-- Truncate all tables
TRUNCATE TABLE tbl_wallet;
TRUNCATE TABLE tbl_crypto_price;
TRUNCATE TABLE tbl_trade_transaction;

-- Insert wallet records with only user_id and wallet balances
INSERT INTO tbl_wallet (user_id, usdt_balance, btc_balance, eth_balance)
VALUES
    ('user1', 50000.0, 0.0, 0.0),
    ('user2', 50000.0, 0.0, 0.0),
    ('user3', 50000.0, 0.0, 0.0),
    ('user4', 50000.0, 0.0, 0.0),
    ('user5', 50000.0, 0.0, 0.0);

-- Insert crypto prices for BTCUSDT and ETHUSDT pairs
INSERT INTO tbl_crypto_price (pair, bid_price, ask_price)
VALUES
    ('BTCUSDT', 40000.0, 40100.0),
    ('ETHUSDT', 2500.0, 2550.0);

-- Insert trade transactions for user1
INSERT INTO tbl_trade_transaction (user_id, pair, type, amount, price, timestamp)
VALUES
    ('user1', 'BTCUSDT', 'BUY', 1.0, 30000.0, '2023-12-10T10:00:00'),
    ('user1', 'ETHUSDT', 'SELL', 2.0, 1500.0, '2023-12-10T10:00:00');