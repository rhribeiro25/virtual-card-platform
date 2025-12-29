CREATE TABLE providers
(
    id         UUID         PRIMARY KEY,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP,
    name       VARCHAR(255) NOT NULL UNIQUE,
    country    VARCHAR(100) NOT NULL
);

CREATE TABLE card_provider
(
    id             UUID           PRIMARY KEY,
    created_at     TIMESTAMP      NOT NULL,
    updated_at     TIMESTAMP,
    card_id        UUID           NOT NULL,
    provider_id    UUID           NOT NULL,
    fee_percentage DECIMAL(5, 2)  NOT NULL,
    daily_limit    DECIMAL(19, 2) NOT NULL,
    priority       INTEGER        NOT NULL,
    enabled        BOOLEAN        NOT NULL,
    CONSTRAINT fk_card_provider_card FOREIGN KEY (card_id) REFERENCES cards (id),
    CONSTRAINT fk_card_provider_provider FOREIGN KEY (provider_id) REFERENCES providers (id),
    CONSTRAINT uk_card_provider UNIQUE (card_id, provider_id)
);
