CREATE TABLE audit_import
(
    id             UUID         PRIMARY KEY,
    card_ref       VARCHAR(50)  NOT NULL,
    provider_code  VARCHAR(50),
    tx_request_ref UUID,
    raw_payload    CLOB         NOT NULL,
    processed_step VARCHAR(50)  DEFAULT FALSE,
    is_processed   BOOLEAN   DEFAULT FALSE,
    created_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_stg_card_ref ON audit_import (card_ref);
CREATE INDEX idx_stg_provider_code ON audit_import (provider_code);
CREATE INDEX idx_stg_tx_request_ref ON audit_import (tx_request_ref);
