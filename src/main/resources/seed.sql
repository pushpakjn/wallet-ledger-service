-- 1. Create Tables
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL
);

CREATE TABLE wallets (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    currency VARCHAR(20) NOT NULL,
    balance DECIMAL(19, 4) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, currency)
);

CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    reference_id VARCHAR(100) UNIQUE NOT NULL, 
    source_wallet_id UUID REFERENCES wallets(id),
    destination_wallet_id UUID REFERENCES wallets(id),
    amount DECIMAL(19, 4) NOT NULL,
    type VARCHAR(50) NOT NULL, 
    status VARCHAR(20) NOT NULL, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Seed Initial Data
-- Create the System/Treasury User
INSERT INTO users (id, username, email) 
VALUES ('00000000-0000-0000-0000-000000000000', 'System Treasury', 'treasury@dinoventures.com');

-- Create the Treasury Wallet with effectively infinite initial balance
INSERT INTO wallets (id, user_id, currency, balance) 
VALUES ('11111111-1111-1111-1111-111111111111', '00000000-0000-0000-0000-000000000000', 'GOLD_COINS', 1000000000.00);

-- Create two normal users for testing
INSERT INTO users (id, username, email) VALUES ('22222222-2222-2222-2222-222222222222', 'PlayerOne', 'p1@test.com');
INSERT INTO users (id, username, email) VALUES ('33333333-3333-3333-3333-333333333333', 'PlayerTwo', 'p2@test.com');

-- Give PlayerOne an initial balance of 500
INSERT INTO wallets (id, user_id, currency, balance) VALUES ('44444444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222', 'GOLD_COINS', 500.00);

-- Give PlayerTwo an initial balance of 100
INSERT INTO wallets (id, user_id, currency, balance) VALUES ('55555555-5555-5555-5555-555555555555', '33333333-3333-3333-3333-333333333333', 'GOLD_COINS', 100.00);