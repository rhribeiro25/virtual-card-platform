CREATE TABLE transactions
(
    id         UUID           PRIMARY KEY,
    card_id    UUID           NOT NULL,
    created_at TIMESTAMP      NOT NULL,
    updated_at TIMESTAMP,
    type       VARCHAR(50)    NOT NULL,
    amount     DECIMAL(19, 2) NOT NULL,
    request_id VARCHAR(50)    NOT NULL UNIQUE,
    CONSTRAINT fk_transaction_card FOREIGN KEY (card_id) REFERENCES cards (id)
);

