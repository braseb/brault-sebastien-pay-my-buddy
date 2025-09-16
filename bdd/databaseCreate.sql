-- Create database software
CREATE DATABASE paymybuddy

-- =====================
-- Table : user
-- =====================
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    account_balance DOUBLE PRECISION NOT NULL DEFAULT 0
);

-- =====================
-- Table : transaction
-- =====================
CREATE TABLE transaction (
    id SERIAL PRIMARY KEY,
    sender_id INTEGER NOT NULL,
    receiver_id INTEGER NOT NULL,
    description TEXT,
    amount DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_transaction_sender FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_transaction_receiver FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_sender_receiver_diff CHECK (sender_id <> receiver_id)
);

-- =====================
-- Table : user_connection
-- =====================
CREATE TABLE user_connection (
    user_id INTEGER NOT NULL,
    user_connection_id INTEGER NOT NULL,
    CONSTRAINT pk_user_connections PRIMARY KEY (user_id, user_connection_id),
    CONSTRAINT fk_connection_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_connection_partner FOREIGN KEY (user_connection_id) REFERENCES users(id) ON DELETE CASCADE

);

ALTER DATABASE paymybuddy SET search_path TO public;
