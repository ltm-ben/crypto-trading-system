-- Insert wallet records with only user_id and wallet balances
INSERT INTO tbl_wallet (user_id, usdt_balance, btc_balance, eth_balance)
VALUES
    ('user1', 50000.0, 0.0, 0.0),
    ('user2', 50000.0, 0.0, 0.0),
    ('user3', 50000.0, 0.0, 0.0),
    ('user4', 50000.0, 0.0, 0.0),
    ('user5', 50000.0, 0.0, 0.0);