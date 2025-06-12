CREATE TABLE cards (
    id UUID PRIMARY KEY,
    cardholder_name VARCHAR(255) NOT NULL,
    balance DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    version BIGINT
);

CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    card_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_transaction_card FOREIGN KEY (card_id) REFERENCES cards(id)
);
