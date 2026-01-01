CREATE TABLE providers
(
    id              UUID           PRIMARY KEY,
    created_at      TIMESTAMP      NOT NULL,
    updated_at      TIMESTAMP,
    status          VARCHAR(50)    NOT NULL,
    code            VARCHAR(50)    NOT NULL UNIQUE,
    country         VARCHAR(100)   NOT NULL
);
