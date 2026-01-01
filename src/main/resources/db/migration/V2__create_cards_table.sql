CREATE TABLE cards
(
    id                     UUID           PRIMARY KEY,
    external_id            VARCHAR(50)    NOT NULL,
    created_at             TIMESTAMP      NOT NULL,
    updated_at             TIMESTAMP,
    status                 VARCHAR(50)    NOT NULL,
    brand                  VARCHAR(50),
    holder_name            VARCHAR(255)   NOT NULL,
    balance                DECIMAL(19, 2) NOT NULL,
    pin_code               VARCHAR(10),
    expiry_date            TIMESTAMP,
    cvv                    INTEGER,
    country                VARCHAR(100),
    currency               VARCHAR(10),
    international_allowed  BOOLEAN        NOT NULL DEFAULT TRUE,
    max_daily_transactions INTEGER,
    max_transaction_amount DECIMAL(19, 2),
    notes                  TEXT,
    version                BIGINT,
    CONSTRAINT uk_card_external_id UNIQUE (external_id)
);
