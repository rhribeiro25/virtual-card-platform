ALTER TABLE transactions
ADD COLUMN request_id UUID;

CREATE UNIQUE INDEX ux_card_request ON transactions(card_id, request_id);
