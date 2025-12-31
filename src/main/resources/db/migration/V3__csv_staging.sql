CREATE TABLE stg_virtual_cards
(
    id             UUID PRIMARY KEY,
    card_ref       VARCHAR(50) NOT NULL,
    provider_code  VARCHAR(50),
    tx_request_ref UUID,
    raw_payload    CLOB        NOT NULL,
    processed      BOOLEAN   DEFAULT FALSE,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE INDEX idx_stg_card_ref ON stg_virtual_cards (card_ref);
CREATE INDEX idx_stg_provider_code ON stg_virtual_cards (provider_code);
CREATE INDEX idx_stg_tx_request_ref ON stg_virtual_cards (tx_request_ref);
CREATE INDEX idx_stg_processed ON stg_virtual_cards (processed);
